const ROUTES = [
  { name: "home", pattern: /^\/home\/?$/ },
  { name: "search", pattern: /^\/search\/?$/ },
  { name: "favorites", pattern: /^\/favorites\/?$/ },
  { name: "profile", pattern: /^\/profile\/?$/ },
  { name: "emergency", pattern: /^\/emergency\/?$/ },
  { name: "region", pattern: /^\/region\/?$/ },
  { name: "school", pattern: /^\/school\/?$/ },
  { name: "stage", pattern: /^\/stage\/?$/ },
  { name: "category", pattern: /^\/category\/([^/]+)\/?$/, params: ["id"] },
  { name: "guide", pattern: /^\/guide\/([^/]+)\/?$/, params: ["id"] },
];

export function parseHash(hash = "") {
  const raw = hash.replace(/^#/, "") || "/home";
  const [rawPath, rawQuery = ""] = raw.split("?", 2);
  const path = rawPath.startsWith("/") ? rawPath : `/${rawPath}`;
  const query = Object.fromEntries(new URLSearchParams(rawQuery));

  for (const route of ROUTES) {
    const match = path.match(route.pattern);
    if (!match) continue;
    const params = {};
    for (let index = 0; index < (route.params?.length ?? 0); index += 1) {
      try {
        params[route.params[index]] = decodeURIComponent(match[index + 1]);
      } catch {
        params[route.params[index]] = "";
      }
    }
    return { name: route.name, path, params, query };
  }
  return { name: "notFound", path, params: {}, query };
}

export function routeHref(path, query = {}) {
  const params = new URLSearchParams();
  for (const [key, value] of Object.entries(query)) {
    if (value !== undefined && value !== null && String(value).length > 0) params.set(key, String(value));
  }
  const suffix = params.toString();
  return `#${path}${suffix ? `?${suffix}` : ""}`;
}
