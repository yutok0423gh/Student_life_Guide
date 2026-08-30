export const STORAGE_KEY = "adult-guide.web.v1";

const DEFAULT_STATE = Object.freeze({
  favorites: [],
  history: [],
  checklist: [],
  region: { province: "", city: "" },
});

function freshState() {
  return {
    favorites: [],
    history: [],
    checklist: [],
    region: { province: "", city: "" },
  };
}

function isString(value) {
  return typeof value === "string";
}

function sanitizeState(value) {
  const state = freshState();
  if (!value || typeof value !== "object" || Array.isArray(value)) return state;

  if (Array.isArray(value.favorites)) {
    state.favorites = value.favorites.filter(
      (item) => item && isString(item.guideId) && isString(item.favoritedAt),
    );
  }
  if (Array.isArray(value.history)) {
    state.history = value.history.filter(
      (item) => item && isString(item.guideId) && isString(item.lastOpenedAt),
    );
  }
  if (Array.isArray(value.checklist)) {
    state.checklist = value.checklist.filter(
      (item) =>
        item &&
        isString(item.guideId) &&
        isString(item.checklistItemId) &&
        typeof item.isChecked === "boolean",
    );
  }
  if (value.region && typeof value.region === "object") {
    state.region = {
      province: isString(value.region.province) ? value.region.province : "",
      city: isString(value.region.city) ? value.region.city : "",
    };
  }
  return state;
}

export function createLocalState(storage = globalThis.localStorage, now = () => new Date().toISOString()) {
  function read() {
    try {
      const raw = storage?.getItem(STORAGE_KEY);
      return raw ? sanitizeState(JSON.parse(raw)) : freshState();
    } catch {
      return freshState();
    }
  }

  function write(state) {
    try {
      storage?.setItem(STORAGE_KEY, JSON.stringify(sanitizeState(state)));
      return true;
    } catch {
      return false;
    }
  }

  function toggleFavorite(guideId) {
    const state = read();
    const existing = state.favorites.findIndex((item) => item.guideId === guideId);
    if (existing >= 0) state.favorites.splice(existing, 1);
    else state.favorites.unshift({ guideId, favoritedAt: now() });
    write(state);
    return existing < 0;
  }

  function recordHistory(guideId) {
    const state = read();
    state.history = [
      { guideId, lastOpenedAt: now() },
      ...state.history.filter((item) => item.guideId !== guideId),
    ].slice(0, 50);
    write(state);
  }

  function setChecklist(guideId, checklistItemId, isChecked) {
    const state = read();
    const rest = state.checklist.filter(
      (item) => !(item.guideId === guideId && item.checklistItemId === checklistItemId),
    );
    state.checklist = [{ guideId, checklistItemId, isChecked: Boolean(isChecked) }, ...rest];
    write(state);
  }

  function setRegion(province, city) {
    const state = read();
    state.region = { province: String(province ?? "").trim(), city: String(city ?? "").trim() };
    write(state);
    return state.region;
  }

  function clear() {
    try {
      storage?.removeItem(STORAGE_KEY);
      return true;
    } catch {
      return false;
    }
  }

  return { read, write, toggleFavorite, recordHistory, setChecklist, setRegion, clear };
}

export { DEFAULT_STATE };
