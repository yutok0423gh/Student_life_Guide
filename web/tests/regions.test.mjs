import assert from "node:assert/strict";
import test from "node:test";
import {
  PROVINCE_OPTIONS,
  REGION_DATA_UPDATED_AT,
  getCitiesForProvince,
  isKnownRegion,
} from "../src/regions.js";

test("地区数据覆盖中国大陆 31 个省级地区", () => {
  assert.equal(PROVINCE_OPTIONS.length, 31);
  assert.equal(new Set(PROVINCE_OPTIONS).size, 31);
  assert.equal(PROVINCE_OPTIONS.includes("香港特别行政区"), false);
  assert.equal(PROVINCE_OPTIONS.includes("澳门特别行政区"), false);
  assert.equal(PROVINCE_OPTIONS.includes("台湾省"), false);
  assert.equal(REGION_DATA_UPDATED_AT, "2025-12-31");
});

test("各省的城市与地级行政区选项非空且不重复", () => {
  for (const province of PROVINCE_OPTIONS) {
    const cities = getCitiesForProvince(province);
    assert.ok(cities.length > 0, `${province} 应至少有一个选项`);
    assert.equal(new Set(cities).size, cities.length, `${province} 不应有重复选项`);
  }
});

test("省市联动返回对应的正式名称", () => {
  assert.ok(getCitiesForProvince("广东省").includes("深圳市"));
  assert.ok(getCitiesForProvince("内蒙古自治区").includes("锡林郭勒盟"));
  assert.ok(getCitiesForProvince("四川省").includes("阿坝藏族羌族自治州"));
  assert.deepEqual(getCitiesForProvince("北京市"), ["北京市"]);
  assert.deepEqual(getCitiesForProvince("不存在的省份"), []);
});

test("只接受空值或列表内的省市组合", () => {
  assert.equal(isKnownRegion("", ""), true);
  assert.equal(isKnownRegion("广东省", ""), true);
  assert.equal(isKnownRegion("广东省", "深圳市"), true);
  assert.equal(isKnownRegion("广东省", "杭州市"), false);
  assert.equal(isKnownRegion("", "深圳市"), false);
  assert.equal(isKnownRegion("火星省", "火星市"), false);
});
