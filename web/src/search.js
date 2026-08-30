function normalize(value) {
  return String(value ?? "")
    .normalize("NFKC")
    .toLocaleLowerCase("zh-CN")
    .replace(/[\s\p{P}\p{S}]+/gu, "");
}

function levenshteinDistance(left, right) {
  if (left === right) return 0;
  if (!left.length) return right.length;
  if (!right.length) return left.length;

  let previous = Array.from({ length: right.length + 1 }, (_, index) => index);
  for (let leftIndex = 1; leftIndex <= left.length; leftIndex += 1) {
    const current = [leftIndex];
    for (let rightIndex = 1; rightIndex <= right.length; rightIndex += 1) {
      const substitution = previous[rightIndex - 1] + (left[leftIndex - 1] === right[rightIndex - 1] ? 0 : 1);
      current[rightIndex] = Math.min(previous[rightIndex] + 1, current[rightIndex - 1] + 1, substitution);
    }
    previous = current;
  }
  return previous[right.length];
}

export function similarity(leftValue, rightValue) {
  const left = normalize(leftValue);
  const right = normalize(rightValue);
  if (!left || !right) return 0;
  if (left.includes(right) || right.includes(left)) return 1;
  return 1 - levenshteinDistance(left, right) / Math.max(left.length, right.length);
}

function scoreValue(value, query, weight) {
  const normalizedValue = normalize(value);
  if (!normalizedValue) return 0;
  if (normalizedValue === query) return weight + 30;
  if (normalizedValue.startsWith(query)) return weight + 20;
  if (normalizedValue.includes(query)) return weight + 12;

  const fuzzyScore = similarity(normalizedValue, query);
  return fuzzyScore >= 0.55 ? Math.round(weight * fuzzyScore) : 0;
}

export function scoreGuide(guide, rawQuery) {
  const query = normalize(rawQuery);
  if (!query) return 0;

  const scores = [
    scoreValue(guide.title, query, 100),
    ...guide.aliases.map((value) => scoreValue(value, query, 82)),
    ...guide.keywords.map((value) => scoreValue(value, query, 62)),
    scoreValue(guide.summary, query, 42),
  ];
  return Math.max(...scores);
}

export function searchGuides(guides, query) {
  if (!normalize(query)) return [];
  return guides
    .map((guide) => ({ guide, score: scoreGuide(guide, query) }))
    .filter((result) => result.score > 0)
    .sort((left, right) => right.score - left.score || left.guide.title.localeCompare(right.guide.title, "zh-CN"));
}
