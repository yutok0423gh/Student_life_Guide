import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";
import {
  EMPTY_SCHOOL_CATALOG,
  findSchoolByCode,
  findSchoolByName,
  searchSchools,
  validateSchoolCatalog,
} from "../src/schools.js";

const rawCatalog = JSON.parse(
  await readFile(new URL("../content/schools.json", import.meta.url), "utf8"),
);
const catalog = validateSchoolCatalog(rawCatalog);

test("教育部普通高校目录完整且标识码唯一", () => {
  assert.equal(catalog.updatedAt, "2026-06-17");
  assert.equal(catalog.schools.length, 2952);
  assert.equal(new Set(catalog.schools.map((school) => school.code)).size, 2952);
  assert.equal(catalog.schools.filter((school) => school.level === "本科").length, 1412);
  assert.equal(catalog.schools.filter((school) => school.level === "专科").length, 1540);
});

test("可以按正式名称、简称字符和学校标识码查找", () => {
  assert.equal(findSchoolByCode(catalog, "4111010003")?.name, "清华大学");
  assert.equal(findSchoolByName(catalog, "  北京大学 ")?.code, "4111010001");
  assert.ok(searchSchools(catalog, "北大").some((school) => school.name === "北京大学"));
});

test("生活地区只影响排序，不会隐藏外地学校", () => {
  const results = searchSchools(catalog, "航空职业学院", { province: "广东省", city: "深圳市" }, 12);
  assert.equal(results.length, 12);
  assert.equal(results[0].province, "广东省");
  assert.ok(results.some((school) => school.province !== "广东省"));
});

test("损坏目录会安全回退且短查询不返回大列表", () => {
  assert.equal(validateSchoolCatalog({ schools: [{ code: "", name: "坏数据" }] }), EMPTY_SCHOOL_CATALOG);
  assert.deepEqual(searchSchools(catalog, "大"), []);
});
