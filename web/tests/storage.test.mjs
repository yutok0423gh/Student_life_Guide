import assert from "node:assert/strict";
import test from "node:test";
import { createLocalState, STORAGE_KEY } from "../src/storage.js";

class MemoryStorage {
  values = new Map();

  getItem(key) {
    return this.values.has(key) ? this.values.get(key) : null;
  }

  setItem(key, value) {
    this.values.set(key, String(value));
  }

  removeItem(key) {
    this.values.delete(key);
  }
}

test("损坏的本地 JSON 会安全回退为空状态", () => {
  const backend = new MemoryStorage();
  backend.setItem(STORAGE_KEY, "{broken");
  const state = createLocalState(backend).read();
  assert.deepEqual(state.favorites, []);
  assert.deepEqual(state.region, { province: "", city: "" });
});

test("收藏使用 guideId 并可切换", () => {
  const backend = new MemoryStorage();
  const storage = createLocalState(backend, () => "2026-08-30T00:00:00.000Z");
  assert.equal(storage.toggleFavorite("guide_a"), true);
  assert.deepEqual(storage.read().favorites, [
    { guideId: "guide_a", favoritedAt: "2026-08-30T00:00:00.000Z" },
  ]);
  assert.equal(storage.toggleFavorite("guide_a"), false);
  assert.deepEqual(storage.read().favorites, []);
});

test("历史去重并把最后打开的文章置顶", () => {
  const backend = new MemoryStorage();
  let tick = 0;
  const storage = createLocalState(backend, () => `2026-08-30T00:00:0${tick++}.000Z`);
  storage.recordHistory("guide_a");
  storage.recordHistory("guide_b");
  storage.recordHistory("guide_a");
  assert.deepEqual(storage.read().history.map((item) => item.guideId), ["guide_a", "guide_b"]);
});

test("清单状态使用 guideId 与 checklistItemId 组合定位", () => {
  const backend = new MemoryStorage();
  const storage = createLocalState(backend);
  storage.setChecklist("guide_a", "check_same", true);
  storage.setChecklist("guide_b", "check_same", false);
  assert.deepEqual(
    storage.read().checklist.map((item) => [item.guideId, item.checklistItemId, item.isChecked]),
    [
      ["guide_b", "check_same", false],
      ["guide_a", "check_same", true],
    ],
  );
});

test("地区可保存在本机且不包含阶段字段", () => {
  const backend = new MemoryStorage();
  const storage = createLocalState(backend);
  storage.setRegion(" 广东省 ", " 深圳市 ");
  const state = storage.read();
  assert.deepEqual(state.region, { province: "广东省", city: "深圳市" });
  assert.equal(Object.prototype.hasOwnProperty.call(state, "stage"), false);
});

test("清除只移除本站命名空间数据", () => {
  const backend = new MemoryStorage();
  backend.setItem("unrelated", "keep");
  const storage = createLocalState(backend);
  storage.toggleFavorite("guide_a");
  assert.equal(storage.clear(), true);
  assert.equal(backend.getItem(STORAGE_KEY), null);
  assert.equal(backend.getItem("unrelated"), "keep");
});
