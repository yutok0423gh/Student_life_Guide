import assert from "node:assert/strict";
import test from "node:test";
import { parseHash, routeHref } from "../src/router.js";

test("空 hash 回到首页", () => {
  assert.equal(parseHash("").name, "home");
});

test("路由参数与中文查询可往返", () => {
  const href = routeHref("/search", { q: "房东不给钱" });
  const route = parseHash(href);
  assert.equal(route.name, "search");
  assert.equal(route.query.q, "房东不给钱");
});

test("文章 id 会被解码且未知路径进入 404", () => {
  assert.equal(parseHash("#/guide/guide_first_rental").params.id, "guide_first_rental");
  assert.equal(parseHash("#/unknown").name, "notFound");
});
