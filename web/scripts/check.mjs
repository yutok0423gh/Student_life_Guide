import { access, readFile, readdir } from "node:fs/promises";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { validateGuideCollection } from "../src/data.js";
import { validateSchoolCatalog } from "../src/schools.js";

const scriptDirectory = dirname(fileURLToPath(import.meta.url));
const webRoot = resolve(scriptDirectory, "..");
const projectRoot = resolve(webRoot, "..");
const sourceDirectory = resolve(projectRoot, "app", "src", "main", "assets", "content", "guides");

const requiredFiles = [
  "index.html",
  "styles.css",
  "manifest.webmanifest",
  "sw.js",
  "content/guides.json",
  "content/schools.json",
  "icons/icon.svg",
  "icons/maskable.svg",
  "src/app.js",
  "src/data.js",
  "src/router.js",
  "src/regions.js",
  "src/schools.js",
  "src/search.js",
  "src/storage.js",
  "src/views.js",
];

for (const relativePath of requiredFiles) await access(resolve(webRoot, relativePath));

const bundledGuides = JSON.parse(await readFile(resolve(webRoot, "content", "guides.json"), "utf8"));
const validation = validateGuideCollection(bundledGuides);
if (validation.rejected.length) {
  const details = validation.rejected.map((item) => `${item.id}: ${item.errors.join("；")}`).join("\n");
  throw new Error(`网页内容校验失败：\n${details}`);
}

const schoolCatalog = validateSchoolCatalog(
  JSON.parse(await readFile(resolve(webRoot, "content", "schools.json"), "utf8")),
);
if (schoolCatalog.schools.length !== 2952) {
  throw new Error(`学校目录数量不正确：期望 2952 所，实际 ${schoolCatalog.schools.length} 所`);
}
if (schoolCatalog.updatedAt !== "2026-06-17") {
  throw new Error("学校目录数据日期不正确");
}

const sourceFiles = (await readdir(sourceDirectory))
  .filter((filename) => filename.toLowerCase().endsWith(".json"))
  .sort((left, right) => left.localeCompare(right, "en"));
const sourceGuides = [];
for (const filename of sourceFiles) {
  sourceGuides.push(JSON.parse(await readFile(resolve(sourceDirectory, filename), "utf8")));
}
sourceGuides.sort((left, right) => String(left.id).localeCompare(String(right.id), "en"));
if (JSON.stringify(sourceGuides) !== JSON.stringify(bundledGuides)) {
  throw new Error("web/content/guides.json 与 Android bundled content 不一致，请先运行 npm run sync-content");
}

const indexHtml = await readFile(resolve(webRoot, "index.html"), "utf8");
const styles = await readFile(resolve(webRoot, "styles.css"), "utf8");
const modules = await Promise.all(
  ["app.js", "data.js", "regions.js", "router.js", "schools.js", "search.js", "storage.js", "views.js"].map((filename) =>
    readFile(resolve(webRoot, "src", filename), "utf8"),
  ),
);
if (/(?:src|href)\s*=\s*["']https?:\/\//i.test(indexHtml)) {
  throw new Error("index.html 含外部资源，无法保证完整离线运行");
}
if (/url\(\s*["']?https?:\/\//i.test(styles)) {
  throw new Error("styles.css 含外部资源，无法保证完整离线运行");
}
if (modules.some((source) => /\bfrom\s*["']https?:\/\//i.test(source))) {
  throw new Error("JavaScript 模块含远程 import");
}

const manifest = JSON.parse(await readFile(resolve(webRoot, "manifest.webmanifest"), "utf8"));
if (manifest.name !== "初级成年人入门手册" || manifest.lang !== "zh-CN") {
  throw new Error("PWA manifest 名称或语言不正确");
}
if (!String(manifest.start_url).startsWith("./") || !String(manifest.scope).startsWith("./")) {
  throw new Error("PWA 路径必须使用相对地址，才能部署在子目录");
}

console.log(`检查通过：${validation.validGuides.length} 篇内容、${schoolCatalog.schools.length} 所学校、${requiredFiles.length} 个必需文件、无远程运行时资源。`);
