const SCHOOL_CATALOG_URL = new URL(
  "../content/schools.json?catalog=2026-06-17",
  import.meta.url,
);

const COMMON_SCHOOL_ALIASES = Object.freeze({
  北大: "北京大学",
  人大: "中国人民大学",
  北航: "北京航空航天大学",
  北理工: "北京理工大学",
  北师大: "北京师范大学",
  哈工大: "哈尔滨工业大学",
  中科大: "中国科学技术大学",
  上交: "上海交通大学",
  浙大: "浙江大学",
  西交: "西安交通大学",
  武大: "武汉大学",
  华科: "华中科技大学",
  川大: "四川大学",
  厦大: "厦门大学",
});

export const EMPTY_SCHOOL_CATALOG = Object.freeze({
  version: 1,
  updatedAt: "",
  publishedAt: "",
  sourceName: "中华人民共和国教育部",
  sourceTitle: "全国高等学校名单",
  sourceUrl: "https://www.moe.gov.cn/jyb_xxgk/s5743/s5744/202606/t20260618_1441074.html",
  scope: "全国普通高等学校（不含港澳台）",
  schools: Object.freeze([]),
});

function cleanText(value, maxLength = 120) {
  return typeof value === "string" ? value.trim().slice(0, maxLength) : "";
}

function normalizeSearchTerm(value) {
  return cleanText(value, 80).normalize("NFKC").toLocaleLowerCase("zh-CN").replaceAll(/\s+/g, "");
}

function isSubsequence(query, target) {
  let queryIndex = 0;
  for (const character of target) {
    if (character === query[queryIndex]) queryIndex += 1;
    if (queryIndex === query.length) return true;
  }
  return false;
}

function sanitizeSchool(item) {
  if (!item || typeof item !== "object" || Array.isArray(item)) return null;
  const code = cleanText(item.code, 20);
  const name = cleanText(item.name, 100);
  const province = cleanText(item.province, 40);
  const city = cleanText(item.city, 40);
  const level = cleanText(item.level, 20);
  if (!code || !name || !province || !city || !["本科", "专科"].includes(level)) return null;
  return Object.freeze({ code, name, province, city, level, isPrivate: item.isPrivate === true });
}

export function validateSchoolCatalog(payload) {
  if (!payload || typeof payload !== "object" || Array.isArray(payload)) return EMPTY_SCHOOL_CATALOG;
  const schools = Array.isArray(payload.schools) ? payload.schools.map(sanitizeSchool).filter(Boolean) : [];
  const uniqueCodes = new Set(schools.map((school) => school.code));
  if (!schools.length || uniqueCodes.size !== schools.length) return EMPTY_SCHOOL_CATALOG;
  return Object.freeze({
    version: Number(payload.version) || 1,
    updatedAt: cleanText(payload.updatedAt, 10),
    publishedAt: cleanText(payload.publishedAt, 10),
    sourceName: cleanText(payload.sourceName, 80) || EMPTY_SCHOOL_CATALOG.sourceName,
    sourceTitle: cleanText(payload.sourceTitle, 100) || EMPTY_SCHOOL_CATALOG.sourceTitle,
    sourceUrl: cleanText(payload.sourceUrl, 300) || EMPTY_SCHOOL_CATALOG.sourceUrl,
    scope: cleanText(payload.scope, 100) || EMPTY_SCHOOL_CATALOG.scope,
    schools: Object.freeze(schools),
  });
}

export async function loadSchoolCatalog(fetchImpl = globalThis.fetch) {
  try {
    const response = await fetchImpl(SCHOOL_CATALOG_URL);
    if (!response.ok) return EMPTY_SCHOOL_CATALOG;
    return validateSchoolCatalog(await response.json());
  } catch {
    return EMPTY_SCHOOL_CATALOG;
  }
}

export function findSchoolByCode(catalog, code) {
  const normalizedCode = cleanText(code, 20);
  if (!normalizedCode) return null;
  return catalog?.schools?.find((school) => school.code === normalizedCode) ?? null;
}

export function findSchoolByName(catalog, name) {
  const normalizedName = normalizeSearchTerm(name);
  if (!normalizedName) return null;
  return catalog?.schools?.find((school) => normalizeSearchTerm(school.name) === normalizedName) ?? null;
}

export function searchSchools(catalog, query, preferredRegion = {}, limit = 8) {
  const normalizedQuery = normalizeSearchTerm(query);
  if (normalizedQuery.length < 2) return [];
  const preferredProvince = cleanText(preferredRegion.province, 40);
  const preferredCity = cleanText(preferredRegion.city, 40);
  const aliasTarget = COMMON_SCHOOL_ALIASES[normalizedQuery] ?? "";

  return (catalog?.schools ?? [])
    .map((school) => {
      const name = normalizeSearchTerm(school.name);
      let score = 0;
      if (school.name === aliasTarget) score = 140;
      else if (name === normalizedQuery) score = 120;
      else if (name.startsWith(normalizedQuery)) score = 100;
      else if (name.includes(normalizedQuery)) score = 80;
      else if (isSubsequence(normalizedQuery, name)) score = 50 + Math.max(0, 20 - name.length);
      else return null;
      if (preferredProvince && school.province === preferredProvince) score += 12;
      if (preferredCity && school.city === preferredCity) score += 6;
      return { school, score };
    })
    .filter(Boolean)
    .sort((left, right) => right.score - left.score || left.school.name.localeCompare(right.school.name, "zh-CN"))
    .slice(0, Math.max(1, Math.min(Number(limit) || 8, 20)))
    .map((item) => item.school);
}
