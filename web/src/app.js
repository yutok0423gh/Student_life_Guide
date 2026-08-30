import { loadGuides } from "./data.js?app=2026-08-30-25";
import { parseHash, routeHref } from "./router.js";
import { createLocalState } from "./storage.js";
import { renderRoute } from "./views.js?app=2026-08-30-25";

const app = document.querySelector("#app");
const main = document.querySelector("#main-content");
const toast = document.querySelector("#toast");
const localState = createLocalState();

let guides = [];
let rejectedCount = 0;
let deferredInstallPrompt = null;
let lastRecordedGuideId = null;
let toastTimer = null;

function showToast(message) {
  toast.textContent = message;
  toast.classList.add("is-visible");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove("is-visible"), 2200);
}

function topLevelRoute(routeName) {
  if (["home", "category", "emergency"].includes(routeName)) return "home";
  if (["profile", "region", "stage"].includes(routeName)) return "profile";
  if (routeName === "favorites") return "favorites";
  if (routeName === "search") return "search";
  return "";
}

function updateNavigation(route) {
  const active = topLevelRoute(route.name);
  document.querySelectorAll("[data-nav-route]").forEach((link) => {
    if (link.dataset.navRoute === active) link.setAttribute("aria-current", "page");
    else link.removeAttribute("aria-current");
  });
}

function updateInstallButtons() {
  document.querySelectorAll("#install-app, [data-install-button]").forEach((button) => {
    button.hidden = !deferredInstallPrompt;
  });
}

function updateDocumentTitle(route) {
  let prefix = "";
  if (route.name === "guide") prefix = guides.find((guide) => guide.id === route.params.id)?.title ?? "手册";
  if (route.name === "category") prefix = "分类";
  if (route.name === "search") prefix = "搜索";
  if (route.name === "favorites") prefix = "收藏";
  if (route.name === "profile") prefix = "我的";
  document.title = prefix ? `${prefix} · 初级成年人入门手册` : "初级成年人入门手册";
}

function render({ focusMain = false } = {}) {
  const route = parseHash(window.location.hash);
  const state = localState.read();
  app.innerHTML = renderRoute(route, { guides, rejectedCount, localState: state });
  updateNavigation(route);
  updateInstallButtons();
  updateDocumentTitle(route);

  if (route.name === "guide" && guides.some((guide) => guide.id === route.params.id)) {
    if (lastRecordedGuideId !== route.params.id) {
      localState.recordHistory(route.params.id);
      lastRecordedGuideId = route.params.id;
    }
  } else {
    lastRecordedGuideId = null;
  }

  if (focusMain) {
    window.scrollTo({ top: 0, behavior: "instant" });
    main.focus({ preventScroll: true });
  }
}

function navigateToSearch(form) {
  const query = new FormData(form).get("q")?.toString().trim() ?? "";
  window.location.hash = routeHref("/search", { q: query }).slice(1);
}

document.addEventListener("submit", (event) => {
  const form = event.target;
  if (!(form instanceof HTMLFormElement)) return;

  if (form.matches("[data-search-form]")) {
    event.preventDefault();
    navigateToSearch(form);
    return;
  }

  if (form.matches("[data-region-form]")) {
    event.preventDefault();
    const data = new FormData(form);
    localState.setRegion(data.get("province"), data.get("city"));
    render();
    showToast("地区已保存在这台设备");
  }
});

document.addEventListener("change", (event) => {
  const input = event.target;
  if (!(input instanceof HTMLInputElement) || !input.matches("[data-checklist]")) return;
  localState.setChecklist(input.dataset.guideId, input.dataset.checklistId, input.checked);
  showToast(input.checked ? "已标记完成" : "已取消完成");
});

async function requestInstall() {
  if (!deferredInstallPrompt) return;
  deferredInstallPrompt.prompt();
  await deferredInstallPrompt.userChoice;
  deferredInstallPrompt = null;
  updateInstallButtons();
}

document.addEventListener("click", async (event) => {
  const action = event.target.closest("[data-action]");
  if (!action) return;

  if (action.dataset.action === "toggle-favorite") {
    const added = localState.toggleFavorite(action.dataset.guideId);
    render();
    showToast(added ? "已加入收藏" : "已取消收藏");
  }

  if (action.dataset.action === "clear-local-data") {
    const confirmed = window.confirm("要清除本浏览器中的地区、收藏、历史和清单状态吗？此操作无法撤销。");
    if (!confirmed) return;
    localState.clear();
    render();
    showToast("本机记录已清除");
  }

  if (action.dataset.action === "install") await requestInstall();
});

document.querySelector("#install-app")?.addEventListener("click", requestInstall);

function updateConnectionStatus() {
  const offline = !navigator.onLine;
  const desktopLabel = document.querySelector("#connection-label");
  const mobileLabel = document.querySelector("#mobile-connection-label");
  const dot = document.querySelector(".status-dot");
  if (desktopLabel) desktopLabel.textContent = offline ? "当前离线 · 本地内容可用" : "本地内容可用";
  if (mobileLabel) mobileLabel.textContent = offline ? "离线可用" : "本地可用";
  mobileLabel?.classList.toggle("is-offline", offline);
  dot?.classList.toggle("is-offline", offline);
}

window.addEventListener("hashchange", () => render({ focusMain: true }));
window.addEventListener("online", updateConnectionStatus);
window.addEventListener("offline", updateConnectionStatus);
window.addEventListener("beforeinstallprompt", (event) => {
  event.preventDefault();
  deferredInstallPrompt = event;
  updateInstallButtons();
});
window.addEventListener("appinstalled", () => {
  deferredInstallPrompt = null;
  updateInstallButtons();
  showToast("已安装到设备");
});

async function registerServiceWorker() {
  if (!("serviceWorker" in navigator) || !["http:", "https:"].includes(window.location.protocol)) return;
  try {
    await navigator.serviceWorker.register(new URL("../sw.js", import.meta.url), { scope: "../" });
  } catch (error) {
    console.warn("离线缓存注册失败", error);
  }
}

async function start() {
  if (!window.location.hash) {
    history.replaceState(null, "", `${window.location.pathname}${window.location.search}#/home`);
  }
  updateConnectionStatus();
  const loaded = await loadGuides();
  guides = loaded.guides;
  rejectedCount = loaded.rejected.length;
  render();
  await registerServiceWorker();
}

start();
