import assert from "node:assert/strict";
import test from "node:test";
import { scoreGuide, searchGuides, similarity } from "../src/search.js";

function guide(overrides = {}) {
  return {
    id: "guide_rent_deposit_not_returned",
    title: "房东不退押金怎么办？",
    summary: "退租后房东拒绝退还押金时的处理流程。",
    keywords: ["租房", "押金", "房东"],
    aliases: ["房东不退钱", "押金不退", "退租押金"],
    ...overrides,
  };
}

test("自然语言近似表达“房东不给钱”能匹配别名", () => {
  const result = searchGuides([guide()], "房东不给钱");
  assert.equal(result.length, 1);
  assert.equal(result[0].guide.id, "guide_rent_deposit_not_returned");
});

test("标题命中高于别名命中", () => {
  const titleMatch = guide({ id: "title", title: "银行卡冻结", aliases: [] });
  const aliasMatch = guide({ id: "alias", title: "银行问题", aliases: ["银行卡冻结"] });
  const result = searchGuides([aliasMatch, titleMatch], "银行卡冻结");
  assert.deepEqual(result.map((item) => item.guide.id), ["title", "alias"]);
});

test("只在允许字段中匹配，其他字段不会影响得分", () => {
  const item = { ...guide(), title: "租房问题", summary: "", keywords: [], aliases: [], sourceUrl: "银行卡冻结" };
  assert.equal(scoreGuide(item, "银行卡冻结"), 0);
});

test("空白与标点不会阻断中文精确匹配", () => {
  assert.equal(similarity("房东 不退钱！", "房东不退钱"), 1);
});
