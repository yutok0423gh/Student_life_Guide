import { mkdir, readFile, readdir, writeFile } from "node:fs/promises";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const scriptDirectory = dirname(fileURLToPath(import.meta.url));
const webRoot = resolve(scriptDirectory, "..");
const sourceDirectory = resolve(webRoot, "..", "app", "src", "main", "assets", "content", "guides");
const outputDirectory = resolve(webRoot, "content");
const outputFile = resolve(outputDirectory, "guides.json");

const filenames = (await readdir(sourceDirectory))
  .filter((filename) => filename.toLowerCase().endsWith(".json"))
  .sort((left, right) => left.localeCompare(right, "en"));

const guides = [];
for (const filename of filenames) {
  const source = await readFile(resolve(sourceDirectory, filename), "utf8");
  try {
    guides.push(JSON.parse(source));
  } catch (error) {
    throw new Error(`无法解析 ${filename}: ${error.message}`);
  }
}

guides.sort((left, right) => String(left.id).localeCompare(String(right.id), "en"));
await mkdir(outputDirectory, { recursive: true });
await writeFile(outputFile, `${JSON.stringify(guides, null, 2)}\n`, "utf8");
console.log(`已同步 ${guides.length} 篇内容到 ${outputFile}`);
