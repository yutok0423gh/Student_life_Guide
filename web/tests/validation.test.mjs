import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";
import { validateGuide, validateGuideCollection } from "../src/data.js";

const validGuide = JSON.parse(
  await readFile(new URL("../../app/src/main/assets/content/guides/guide_first_rental.json", import.meta.url), "utf8"),
);

test("Android 正式内置指南符合网页共用 Guide Schema", () => {
  assert.deepEqual(validateGuide(validGuide), { valid: true, errors: [] });
});

test("缺少固定字段时拒绝单篇内容", () => {
  const invalid = structuredClone(validGuide);
  delete invalid.quickAnswer;
  const result = validateGuide(invalid);
  assert.equal(result.valid, false);
  assert.ok(result.errors.some((error) => error.includes("quickAnswer")));
});

test("非法分类、重复 Checklist id 与日期格式均会被识别", () => {
  const invalid = structuredClone(validGuide);
  invalid.categoryId = "other";
  invalid.verifiedAt = "2026/08/29";
  invalid.checklist.push(structuredClone(invalid.checklist[0]));
  const result = validateGuide(invalid);
  assert.equal(result.valid, false);
  assert.ok(result.errors.some((error) => error.includes("categoryId")));
  assert.ok(result.errors.some((error) => error.includes("Checklist id")));
  assert.ok(result.errors.some((error) => error.includes("verifiedAt")));
});

test("内容集合跳过坏文章并继续保留好文章", () => {
  const invalid = structuredClone(validGuide);
  invalid.id = "";
  const result = validateGuideCollection([validGuide, invalid]);
  assert.deepEqual(result.validGuides.map((guide) => guide.id), ["guide_first_rental"]);
  assert.equal(result.rejected.length, 1);
});

test("集合内 Guide id 必须唯一", () => {
  const result = validateGuideCollection([validGuide, structuredClone(validGuide)]);
  assert.equal(result.validGuides.length, 1);
  assert.equal(result.rejected.length, 1);
  assert.ok(result.rejected[0].errors.some((error) => error.includes("重复")));
});

test("未定 object schema 的 warnings 与 sections 目前只接受空数组", () => {
  const guide = structuredClone(validGuide);
  guide.warnings = [{ futureShape: true }];
  guide.sections = [{ futureShape: true }];
  const result = validateGuide(guide);
  assert.equal(result.valid, false);
  assert.ok(result.errors.some((error) => error.includes("warnings 目前只能为空数组")));
  assert.ok(result.errors.some((error) => error.includes("sections 目前只能为空数组")));
});
