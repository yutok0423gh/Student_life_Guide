import { cp, mkdir, rm } from "node:fs/promises";
import { basename, dirname, resolve, sep } from "node:path";
import { fileURLToPath } from "node:url";

const scriptDirectory = dirname(fileURLToPath(import.meta.url));
const webRoot = resolve(scriptDirectory, "..");
const dist = resolve(webRoot, "dist");

if (basename(dist) !== "dist" || !dist.startsWith(`${webRoot}${sep}`)) {
  throw new Error(`拒绝清理非预期目录：${dist}`);
}

await rm(dist, { recursive: true, force: true });
await mkdir(dist, { recursive: true });

for (const filename of ["index.html", "styles.css", "manifest.webmanifest", "sw.js"]) {
  await cp(resolve(webRoot, filename), resolve(dist, filename));
}
for (const directory of ["src", "content", "icons"]) {
  await cp(resolve(webRoot, directory), resolve(dist, directory), { recursive: true });
}

console.log(`网页版已构建到 ${dist}`);
