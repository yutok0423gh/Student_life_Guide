export const STORAGE_KEY = "adult-guide.web.v1";

const DEFAULT_STATE = Object.freeze({
  favorites: [],
  history: [],
  checklist: [],
  region: { province: "", city: "" },
  school: { code: "", name: "", province: "", city: "", level: "", campus: "", major: "" },
});

function freshState() {
  return {
    favorites: [],
    history: [],
    checklist: [],
    region: { province: "", city: "" },
    school: { code: "", name: "", province: "", city: "", level: "", campus: "", major: "" },
  };
}

function isString(value) {
  return typeof value === "string";
}

function cleanString(value, maxLength = 100) {
  return isString(value) ? value.trim().slice(0, maxLength) : "";
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
  if (value.school && typeof value.school === "object" && !Array.isArray(value.school)) {
    const name = cleanString(value.school.name, 100);
    state.school = name
      ? {
          code: cleanString(value.school.code, 20),
          name,
          province: cleanString(value.school.province, 40),
          city: cleanString(value.school.city, 40),
          level: cleanString(value.school.level, 20),
          campus: cleanString(value.school.campus, 80),
          major: cleanString(value.school.major, 80),
        }
      : freshState().school;
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

  function setSchool(school) {
    const state = read();
    state.school = school && typeof school === "object" ? school : freshState().school;
    write(state);
    return read().school;
  }

  function clear() {
    try {
      storage?.removeItem(STORAGE_KEY);
      return true;
    } catch {
      return false;
    }
  }

  return { read, write, toggleFavorite, recordHistory, setChecklist, setRegion, setSchool, clear };
}

export { DEFAULT_STATE };
