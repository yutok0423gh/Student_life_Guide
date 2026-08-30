const CACHE_PREFIX = "adult-guide-web";
const CACHE_NAME = `${CACHE_PREFIX}-v3`;
const BASE_URL = new URL("./", self.location.href);
const INDEX_URL = new URL("./index.html", BASE_URL).href;
const SHELL_URLS = [
  "./",
  "./index.html",
  "./styles.css",
  "./manifest.webmanifest",
  "./icons/icon.svg",
  "./icons/maskable.svg",
  "./content/guides.json?content=2026-08-30-25",
  "./src/app.js?app=2026-08-30-26",
  "./src/data.js?app=2026-08-30-26",
  "./src/regions.js?app=2026-08-30-26",
  "./src/router.js",
  "./src/search.js",
  "./src/storage.js",
  "./src/views.js?app=2026-08-30-26",
].map((path) => new URL(path, BASE_URL).href);

self.addEventListener("install", (event) => {
  event.waitUntil(caches.open(CACHE_NAME).then((cache) => cache.addAll(SHELL_URLS)));
  self.skipWaiting();
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches
      .keys()
      .then((keys) =>
        Promise.all(
          keys.filter((key) => key.startsWith(CACHE_PREFIX) && key !== CACHE_NAME).map((key) => caches.delete(key)),
        ),
      ),
  );
  self.clients.claim();
});

async function navigationResponse(request) {
  try {
    const response = await fetch(request);
    if (response.ok) {
      const cache = await caches.open(CACHE_NAME);
      await cache.put(INDEX_URL, response.clone());
    }
    return response;
  } catch {
    return (await caches.match(INDEX_URL)) ?? Response.error();
  }
}

async function localAssetResponse(request, event) {
  const cached = await caches.match(request);
  const network = fetch(request).then(async (response) => {
    if (response.ok) {
      const cache = await caches.open(CACHE_NAME);
      await cache.put(request, response.clone());
    }
    return response;
  });

  if (cached) {
    event.waitUntil(network.catch(() => undefined));
    return cached;
  }
  return network.catch(() => Response.error());
}

self.addEventListener("fetch", (event) => {
  const request = event.request;
  if (request.method !== "GET") return;
  const url = new URL(request.url);
  if (url.origin !== self.location.origin) return;

  if (request.mode === "navigate") {
    event.respondWith(navigationResponse(request));
    return;
  }
  event.respondWith(localAssetResponse(request, event));
});
