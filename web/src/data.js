export const CATEGORY_IDS = Object.freeze([
  "education",
  "housing",
  "health",
  "finance",
  "career",
  "documents",
  "safety",
  "life",
]);

export const CATEGORIES = Object.freeze([
  { id: "education", name: "学校与学籍", description: "报到、学籍、挂科与毕业", color: "#3157d5" },
  { id: "housing", name: "住宿与租房", description: "宿舍、看房、合同与搬家", color: "#8b5ac7" },
  { id: "health", name: "医疗与保险", description: "挂号、就医、医保与买药", color: "#23866c" },
  { id: "finance", name: "钱与银行", description: "银行卡、转账、征信与预算", color: "#c98a17" },
  { id: "career", name: "实习与工作", description: "求职、合同、辞职与欠薪", color: "#d24e6b" },
  { id: "documents", name: "证件与办事", description: "身份证、档案、户口与居住证", color: "#267a9c" },
  { id: "safety", name: "安全与维权", description: "防骗、报警、骚扰与消费维权", color: "#f05a36" },
  { id: "life", name: "独立生活", description: "洗衣、做饭、快递与出行", color: "#64736b" },
]);

const REGION_LEVELS = new Set(["COUNTRY", "PROVINCE", "CITY"]);
const SOURCE_LEVELS = new Set(["NATIONAL", "PROVINCIAL", "CITY", "OTHER_OFFICIAL"]);
const DATE_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

function isPlainObject(value) {
  return value !== null && typeof value === "object" && !Array.isArray(value);
}

function isNonEmptyString(value) {
  return typeof value === "string" && value.trim().length > 0;
}

function isNullableString(value) {
  return value === null || typeof value === "string";
}

function hasUniqueIds(items) {
  const ids = items.map((item) => item?.id);
  return ids.every(isNonEmptyString) && new Set(ids).size === ids.length;
}

function isStringArray(value) {
  return Array.isArray(value) && value.every((item) => typeof item === "string");
}

export function validateGuide(guide) {
  const errors = [];
  if (!isPlainObject(guide)) return { valid: false, errors: ["Guide 必须是对象"] };

  const requiredKeys = [
    "id",
    "title",
    "summary",
    "categoryId",
    "scenarioIds",
    "keywords",
    "aliases",
    "region",
    "quickAnswer",
    "steps",
    "checklist",
    "warnings",
    "sections",
    "sources",
    "verifiedAt",
    "version",
  ];

  for (const key of requiredKeys) {
    if (!Object.prototype.hasOwnProperty.call(guide, key)) errors.push(`缺少字段 ${key}`);
  }

  if (!isNonEmptyString(guide.id)) errors.push("id 必须为非空字符串");
  if (!isNonEmptyString(guide.title)) errors.push("title 必须为非空字符串");
  if (typeof guide.summary !== "string") errors.push("summary 必须为字符串");
  if (!CATEGORY_IDS.includes(guide.categoryId)) errors.push("categoryId 不在固定分类中");
  if (!isStringArray(guide.scenarioIds)) errors.push("scenarioIds 必须为字符串数组");
  if (!isStringArray(guide.keywords)) errors.push("keywords 必须为字符串数组");
  if (!isStringArray(guide.aliases)) errors.push("aliases 必须为字符串数组");
  if (typeof guide.quickAnswer !== "string") errors.push("quickAnswer 必须为字符串");

  if (!isPlainObject(guide.region)) {
    errors.push("region 必须为对象");
  } else {
    if (!REGION_LEVELS.has(guide.region.level)) errors.push("region.level 不合法");
    if (!Object.prototype.hasOwnProperty.call(guide.region, "province")) errors.push("region 缺少 province");
    if (!Object.prototype.hasOwnProperty.call(guide.region, "city")) errors.push("region 缺少 city");
    if (!isNullableString(guide.region.province)) errors.push("region.province 必须为字符串或 null");
    if (!isNullableString(guide.region.city)) errors.push("region.city 必须为字符串或 null");
    if (guide.region.level === "COUNTRY" && (guide.region.province !== null || guide.region.city !== null)) {
      errors.push("COUNTRY 级内容的 province 和 city 必须为 null");
    }
    if (guide.region.level === "PROVINCE" && (!isNonEmptyString(guide.region.province) || guide.region.city !== null)) {
      errors.push("PROVINCE 级内容必须提供 province 且 city 为 null");
    }
    if (
      guide.region.level === "CITY" &&
      (!isNonEmptyString(guide.region.province) || !isNonEmptyString(guide.region.city))
    ) {
      errors.push("CITY 级内容必须提供 province 和 city");
    }
  }

  if (!Array.isArray(guide.steps)) {
    errors.push("steps 必须为数组");
  } else {
    if (!hasUniqueIds(guide.steps)) errors.push("Step id 必须在 Guide 内非空且唯一");
    for (const step of guide.steps) {
      if (!isPlainObject(step)) {
        errors.push("Step 必须为对象");
        continue;
      }
      if (!Number.isInteger(step.order)) errors.push(`Step ${step.id ?? "?"} 的 order 必须为整数`);
      if (!isNonEmptyString(step.title)) errors.push(`Step ${step.id ?? "?"} 的 title 不能为空`);
      if (typeof step.description !== "string") errors.push(`Step ${step.id ?? "?"} 的 description 必须为字符串`);
    }
  }

  if (!Array.isArray(guide.checklist)) {
    errors.push("checklist 必须为数组");
  } else {
    if (!hasUniqueIds(guide.checklist)) errors.push("Checklist id 必须在 Guide 内非空且唯一");
    for (const item of guide.checklist) {
      if (!isPlainObject(item)) {
        errors.push("ChecklistItem 必须为对象");
        continue;
      }
      if (!isNonEmptyString(item.text)) errors.push(`Checklist ${item.id ?? "?"} 的 text 不能为空`);
      if (typeof item.required !== "boolean") errors.push(`Checklist ${item.id ?? "?"} 的 required 必须为布尔值`);
    }
  }

  if (!Array.isArray(guide.warnings)) errors.push("warnings 必须为数组");
  else if (guide.warnings.length > 0) errors.push("Warning object schema 尚未确认，warnings 目前只能为空数组");
  if (!Array.isArray(guide.sections)) errors.push("sections 必须为数组");
  else if (guide.sections.length > 0) errors.push("Section object schema 尚未确认，sections 目前只能为空数组");

  if (!Array.isArray(guide.sources)) {
    errors.push("sources 必须为数组");
  } else {
    if (!hasUniqueIds(guide.sources)) errors.push("Source id 必须在 Guide 内非空且唯一");
    for (const source of guide.sources) {
      if (!isPlainObject(source)) {
        errors.push("Source 必须为对象");
        continue;
      }
      for (const key of ["sourceName", "sourceTitle", "sourceUrl", "sourceLevel", "verifiedAt"]) {
        if (!isNonEmptyString(source[key])) errors.push(`Source ${source.id ?? "?"} 的 ${key} 不能为空`);
      }
      if (!SOURCE_LEVELS.has(source.sourceLevel)) errors.push(`Source ${source.id ?? "?"} 的 sourceLevel 不合法`);
      if (!isNullableString(source.publishedAt)) errors.push(`Source ${source.id ?? "?"} 的 publishedAt 必须为字符串或 null`);
      if (typeof source.publishedAt === "string" && !DATE_PATTERN.test(source.publishedAt)) {
        errors.push(`Source ${source.id ?? "?"} 的 publishedAt 格式错误`);
      }
      if (typeof source.verifiedAt === "string" && !DATE_PATTERN.test(source.verifiedAt)) {
        errors.push(`Source ${source.id ?? "?"} 的 verifiedAt 格式错误`);
      }
    }
  }

  if (!isNonEmptyString(guide.verifiedAt) || !DATE_PATTERN.test(guide.verifiedAt)) {
    errors.push("verifiedAt 必须使用 YYYY-MM-DD");
  }
  if (!Number.isInteger(guide.version) || guide.version <= 0) errors.push("version 必须为正整数");

  return { valid: errors.length === 0, errors };
}

export function validateGuideCollection(value) {
  if (!Array.isArray(value)) return { validGuides: [], rejected: [{ id: "?", errors: ["根节点必须为数组"] }] };

  const validGuides = [];
  const rejected = [];
  const seenIds = new Set();

  for (const guide of value) {
    const result = validateGuide(guide);
    const id = typeof guide?.id === "string" ? guide.id : "?";
    if (seenIds.has(id)) result.errors.push(`Guide id ${id} 重复`);
    if (result.errors.length > 0) {
      rejected.push({ id, errors: result.errors });
      continue;
    }
    seenIds.add(id);
    validGuides.push(guide);
  }

  return { validGuides, rejected };
}

export async function loadGuides(fetchImpl = globalThis.fetch) {
  if (typeof fetchImpl !== "function") return { guides: [], rejected: [{ id: "?", errors: ["Fetch 不可用"] }] };
  try {
    const response = await fetchImpl("./content/guides.json?content=2026-08-30-25", { cache: "no-cache" });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const collection = validateGuideCollection(await response.json());
    for (const item of collection.rejected) {
      console.warn(`[内容跳过] ${item.id}: ${item.errors.join("；")}`);
    }
    return { guides: collection.validGuides, rejected: collection.rejected };
  } catch (error) {
    console.error("无法读取离线手册内容", error);
    return { guides: [], rejected: [{ id: "?", errors: [String(error?.message ?? error)] }] };
  }
}

export function getCategory(categoryId) {
  return CATEGORIES.find((category) => category.id === categoryId) ?? null;
}
