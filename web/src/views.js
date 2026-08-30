import { CATEGORIES, getCategory } from "./data.js?app=2026-08-30-30";
import {
  PROVINCE_OPTIONS,
  REGION_DATA_SOURCE,
  REGION_DATA_UPDATED_AT,
  getCitiesForProvince,
} from "./regions.js?app=2026-08-30-30";
import { routeHref } from "./router.js";
import { searchGuides } from "./search.js";

export const SCENARIOS = Object.freeze([
  "生病了",
  "挂科了",
  "被骗了",
  "东西丢了",
  "想租房",
  "想找实习",
  "想辞职",
  "遇到欠薪",
  "银行卡出问题",
  "不知道怎么办",
]);

const STAGE_LABELS = Object.freeze([
  "高中毕业",
  "大一",
  "大二",
  "大三",
  "大四",
  "研究生",
  "应届毕业",
  "刚工作",
]);

export function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatDate(value) {
  const [year, month, day] = String(value).split("-");
  if (!year || !month || !day) return escapeHtml(value);
  return `${Number(year)}年${Number(month)}月${Number(day)}日`;
}

function regionLabel(region) {
  if (region.level === "CITY") return `${region.province} / ${region.city}`;
  if (region.level === "PROVINCE") return region.province;
  return "中国大陆";
}

function userRegionLabel(region) {
  if (region.city) return `${region.province || "未填写省份"} / ${region.city}`;
  if (region.province) return region.province;
  return "尚未设置地区";
}

function schoolProfileLabel(school) {
  if (!school?.name) return "尚未设置";
  return school.campus ? `${school.name} · ${school.campus}` : school.name;
}

function contentFootnote(rejectedCount) {
  return `<footer class="content-footnote">
    内容保存在安装包中，收藏、阅读记录与勾选状态只写入当前浏览器。${
      rejectedCount > 0 ? ` 本次有 ${rejectedCount} 篇格式不合规内容已安全跳过。` : ""
    }
  </footer>`;
}

function emptyState(title, description, actionLabel = "返回首页", href = "#/home") {
  return `<div class="empty-state">
    <strong>${escapeHtml(title)}</strong>
    <span>${escapeHtml(description)}</span>
    <a class="secondary-button" href="${escapeHtml(href)}">${escapeHtml(actionLabel)}</a>
  </div>`;
}

function guideRows(guides) {
  if (!guides.length) return "";
  return `<div class="guide-list">${guides
    .map((guide) => {
      const category = getCategory(guide.categoryId);
      return `<a class="guide-row" href="${routeHref(`/guide/${encodeURIComponent(guide.id)}`)}">
        <span>
          <strong>${escapeHtml(guide.title)}</strong>
          <small>${escapeHtml(guide.summary)} · ${escapeHtml(category?.name ?? guide.categoryId)}</small>
        </span>
        <span class="row-arrow" aria-hidden="true">→</span>
      </a>`;
    })
    .join("")}</div>`;
}

function searchForm(query = "", autofocus = false) {
  return `<form class="search-form" data-search-form role="search">
    <label class="search-field">
      <span class="visually-hidden">描述你遇到的事情</span>
      <input
        type="search"
        name="q"
        value="${escapeHtml(query)}"
        placeholder="搜索：挂科、租房、被骗、实习……"
        autocomplete="off"
        ${autofocus ? "autofocus" : ""}
      />
    </label>
    <button class="primary-button" type="submit">开始查找</button>
  </form>`;
}

function pageHeading(eyebrow, title, lede = "", actions = "") {
  return `<header class="page-heading">
    <div>
      <p class="eyebrow">${escapeHtml(eyebrow)}</p>
      <h1>${escapeHtml(title)}</h1>
      ${lede ? `<p class="lede">${escapeHtml(lede)}</p>` : ""}
    </div>
    ${actions}
  </header>`;
}

function homeView(context) {
  const { guides, localState, rejectedCount } = context;
  const guideById = new Map(guides.map((guide) => [guide.id, guide]));
  const recent = localState.history.map((item) => guideById.get(item.guideId)).filter(Boolean).slice(0, 4);

  return `<section class="page">
    <header class="hero">
      <p class="eyebrow">ACTION FIRST · 离线手册</p>
      <h1>你现在遇到了什么？</h1>
      <p class="lede">别先背知识。说出正在发生的事，我们先找下一步。</p>
      <div class="region-row">
        <span aria-hidden="true">⌖</span>
        <span>${escapeHtml(userRegionLabel(localState.region))}</span>
        <a href="#/region">设置地区</a>
      </div>
    </header>

    <div class="search-panel">${searchForm()}</div>

    <a class="emergency-card" href="#/emergency">
      <span class="emergency-mark" aria-hidden="true">!</span>
      <span><strong>情况紧急？先看立即行动</strong><small>人身危险、物品被盗、被骗或意外</small></span>
      <span aria-hidden="true">→</span>
    </a>

    <section class="section-block" aria-labelledby="scenario-title">
      <div class="section-heading"><h2 id="scenario-title">我现在……</h2><small>按你会说的话找</small></div>
      <div class="scenario-grid">
        ${SCENARIOS.map(
          (scenario) =>
            `<a class="scenario-chip" href="${routeHref("/search", { q: scenario })}">${escapeHtml(scenario)}</a>`,
        ).join("")}
      </div>
    </section>

    <section class="section-block" aria-labelledby="category-title">
      <div class="section-heading"><h2 id="category-title">八大分类</h2><small>按现实生活翻页</small></div>
      <div class="category-grid">
        ${CATEGORIES.map((category) => {
          const count = guides.filter((guide) => guide.categoryId === category.id).length;
          return `<a class="category-card" href="${routeHref(`/category/${category.id}`)}" style="--category-color:${category.color}">
            <strong>${escapeHtml(category.name)}</strong>
            <small>${escapeHtml(category.description)}</small>
            <span>${count} 篇手册</span>
          </a>`;
        }).join("")}
      </div>
    </section>

    <section class="section-block" aria-labelledby="recent-title">
      <div class="section-heading"><h2 id="recent-title">最近阅读</h2><small>仅保存在这台设备</small></div>
      ${recent.length ? guideRows(recent) : emptyState("还没有阅读记录", "打开任意一篇手册后，会出现在这里。", "浏览分类", "#/category/life")}
    </section>

    <div class="notice" style="margin-top:36px">
      当前收录 ${guides.length} 篇面向中国大陆的实用指南，均附官方来源和核验日期。规则可能调整，正式办理前请打开来源复核。
    </div>
    ${contentFootnote(rejectedCount)}
  </section>`;
}

function searchView(route, context) {
  const query = route.query.q?.trim() ?? "";
  const results = query ? searchGuides(context.guides, query) : [];
  return `<section class="page">
    ${pageHeading("SEARCH", "直接描述这件事", "不需要先知道专业名词；搜索只读取标题、摘要、关键词与自然语言别名。")}
    ${searchForm(query, !query)}
    <section class="section-block" aria-labelledby="search-results-title">
      <div class="section-heading">
        <h2 id="search-results-title">${query ? `“${escapeHtml(query)}”的结果` : "搜索建议"}</h2>
        <small>${query ? `${results.length} 篇匹配` : "例如：银行卡出问题"}</small>
      </div>
      ${
        query
          ? results.length
            ? guideRows(results.map((result) => result.guide))
            : emptyState(
                "暂时没有匹配的已核验手册",
                "可以换一种日常说法，或从八大分类浏览。当前内容库仍处于建设阶段。",
                "浏览分类",
                "#/home",
              )
          : `<div class="scenario-grid">${SCENARIOS.map(
              (scenario) =>
                `<a class="scenario-chip" href="${routeHref("/search", { q: scenario })}">${escapeHtml(scenario)}</a>`,
            ).join("")}</div>`
      }
    </section>
    ${contentFootnote(context.rejectedCount)}
  </section>`;
}

function favoritesView(context) {
  const guideById = new Map(context.guides.map((guide) => [guide.id, guide]));
  const favorites = context.localState.favorites
    .map((item) => guideById.get(item.guideId))
    .filter(Boolean);
  return `<section class="page">
    ${pageHeading("SAVED", "收藏", "常用步骤留在这台设备，不需要账号。")}
    ${favorites.length ? guideRows(favorites) : emptyState("还没有收藏", "在文章右上角点“收藏”，就能把它留在这里。", "去找一篇手册", "#/search")}
    ${contentFootnote(context.rejectedCount)}
  </section>`;
}

function categoryView(route, context) {
  const category = getCategory(route.params.id);
  if (!category) return notFoundView(context);
  const guides = context.guides.filter((guide) => guide.categoryId === category.id);
  return `<section class="page">
    <a class="back-link" href="#/home">← 返回八大分类</a>
    ${pageHeading("CATEGORY", category.name, category.description)}
    ${
      guides.length
        ? guideRows(guides)
        : emptyState(
            "这一页还在编写",
            "这里不会自动生成未经核验的建议。正式手册完成官方来源核验后才会出现。",
            "搜索其他问题",
            "#/search",
          )
    }
    ${contentFootnote(context.rejectedCount)}
  </section>`;
}

function safeSourceUrl(value) {
  try {
    const url = new URL(value);
    return url.protocol === "https:" || url.protocol === "http:" ? url.href : null;
  } catch {
    return null;
  }
}

function guideView(route, context) {
  const guide = context.guides.find((item) => item.id === route.params.id);
  if (!guide) return notFoundView(context);
  const category = getCategory(guide.categoryId);
  const isFavorite = context.localState.favorites.some((item) => item.guideId === guide.id);
  const checklistState = new Map(
    context.localState.checklist
      .filter((item) => item.guideId === guide.id)
      .map((item) => [item.checklistItemId, item.isChecked]),
  );
  const sortedSteps = [...guide.steps].sort((left, right) => left.order - right.order);

  const action = `<div class="guide-actions">
    <button class="secondary-button" type="button" data-action="toggle-favorite" data-guide-id="${escapeHtml(guide.id)}" aria-pressed="${isFavorite}">
      ${isFavorite ? "已收藏" : "收藏"}
    </button>
  </div>`;

  return `<article class="page">
    <a class="back-link" href="${routeHref(`/category/${guide.categoryId}`)}">← 返回${escapeHtml(category?.name ?? "分类")}</a>
    ${pageHeading("GUIDE", guide.title, guide.summary, action)}
    <ul class="meta-line">
      <li>${escapeHtml(category?.name ?? guide.categoryId)}</li>
      <li>${escapeHtml(regionLabel(guide.region))}</li>
      <li>核验：${formatDate(guide.verifiedAt)}</li>
      <li>版本 ${guide.version}</li>
    </ul>

    <section class="answer-panel" aria-labelledby="quick-answer-title">
      <h2 id="quick-answer-title">30 秒告诉我怎么办</h2>
      <p>${escapeHtml(guide.quickAnswer || "这篇手册的快速答案仍在编写，暂不提供未经核验的建议。")}</p>
    </section>

    <section class="section-block" aria-labelledby="steps-title">
      <div class="section-heading"><h2 id="steps-title">现在先做这些</h2><small>${sortedSteps.length} 个步骤</small></div>
      ${
        sortedSteps.length
          ? `<ol class="step-list">${sortedSteps
              .map(
                (step) => `<li class="step-item">
                  <span class="step-number" aria-label="第 ${step.order} 步">${step.order}</span>
                  <div><h3>${escapeHtml(step.title)}</h3><p>${escapeHtml(step.description)}</p></div>
                </li>`,
              )
              .join("")}</ol>`
          : `<div class="notice">操作步骤尚未完成核验，因此暂不展示。</div>`
      }
    </section>

    <section class="section-block" aria-labelledby="checklist-title">
      <div class="section-heading"><h2 id="checklist-title">需要准备</h2><small>勾选状态保存在本机</small></div>
      ${
        guide.checklist.length
          ? `<ul class="checklist">${guide.checklist
              .map(
                (item) => `<li class="check-item">
                  <label>
                    <input type="checkbox" data-checklist data-guide-id="${escapeHtml(guide.id)}" data-checklist-id="${escapeHtml(item.id)}" ${
                      checklistState.get(item.id) ? "checked" : ""
                    } />
                    <span>${escapeHtml(item.text)}</span>
                    ${item.required ? '<span class="required-tag">必需</span>' : ""}
                  </label>
                </li>`,
              )
              .join("")}</ul>`
          : `<p class="muted">这篇手册没有材料清单。</p>`
      }
    </section>

    ${
      guide.warnings.length
        ? `<section class="section-block"><h2>不要这样做</h2><div class="notice">Warning 的正式字段结构尚未确认，当前版本不会猜测并解析这部分内容。</div></section>`
        : ""
    }
    ${
      guide.sections.length
        ? `<section class="section-block"><h2>为什么？</h2><div class="notice">Sections 的正式字段结构尚未确认，当前版本不会猜测并解析这部分内容。</div></section>`
        : ""
    }

    <section class="section-block" aria-labelledby="sources-title">
      <div class="section-heading"><h2 id="sources-title">官方依据</h2><small>最后核验 ${formatDate(guide.verifiedAt)}</small></div>
      ${
        guide.sources.length
          ? `<ul class="source-list">${guide.sources
              .map((source) => {
                const url = safeSourceUrl(source.sourceUrl);
                const body = `<strong>${escapeHtml(source.sourceTitle)}</strong><small>${escapeHtml(source.sourceName)} · ${escapeHtml(source.sourceLevel)}</small>`;
                return `<li>${
                  url
                    ? `<a class="source-card" href="${escapeHtml(url)}" target="_blank" rel="noopener noreferrer">${body}</a>`
                    : `<div class="source-card">${body}<small>来源网址格式无效，已阻止打开</small></div>`
                }</li>`;
              })
              .join("")}</ul>`
          : `<div class="notice">此信息尚未完成官方来源核验。当前内容仅用于验证产品与离线阅读流程。</div>`
      }
    </section>
    ${contentFootnote(context.rejectedCount)}
  </article>`;
}

function emergencyView(context) {
  const topics = ["被骗了", "银行卡被盗", "手机丢失", "身份证丢失", "交通事故", "有人受伤", "遇到暴力", "被偷拍或骚扰"];
  return `<section class="page">
    <a class="back-link" href="#/home">← 返回首页</a>
    <div class="emergency-hero">
      <p class="eyebrow">EMERGENCY</p>
      <h1>先保证人身安全</h1>
      <p class="lede">如果危险正在发生，先离开危险环境，联系身边可信任的人或当地紧急服务。</p>
      <ol class="emergency-actions">
        <li><b>1</b><span>离开持续发生危险的位置，不要独自对抗。</span></li>
        <li><b>2</b><span>联系可信任的人，让对方知道你在哪里、发生了什么。</span></li>
        <li><b>3</b><span>在安全前提下保存时间、地点、交易或现场记录。</span></li>
      </ol>
    </div>
    <section class="section-block">
      <div class="section-heading"><h2>发生了哪一种情况？</h2><small>进入已核验内容搜索</small></div>
      <div class="scenario-grid">${topics
        .map((topic) => `<a class="scenario-chip" href="${routeHref("/search", { q: topic })}">${escapeHtml(topic)}</a>`)
        .join("")}</div>
    </section>
    <div class="notice" style="margin-top:30px">具体号码与处置流程尚待逐项核对中国大陆官方来源，当前版本不会凭经验补全。</div>
    ${contentFootnote(context.rejectedCount)}
  </section>`;
}

function regionView(context) {
  const { province, city } = context.localState.region;
  const selectedProvince = PROVINCE_OPTIONS.includes(province) ? province : "";
  const cities = getCitiesForProvince(selectedProvince);
  const selectedCity = cities.includes(city) ? city : "";
  const hasUnsupportedSavedRegion = Boolean(province || city) && (!selectedProvince || (city && !selectedCity));
  const selectionLabel = selectedCity
    ? `${selectedProvince} / ${selectedCity}`
    : selectedProvince || "尚未选择";
  const provinceOptions = PROVINCE_OPTIONS.map(
    (item) =>
      `<option value="${escapeHtml(item)}" ${item === selectedProvince ? "selected" : ""}>${escapeHtml(item)}</option>`,
  ).join("");
  const cityOptions = cities
    .map(
      (item) =>
        `<option value="${escapeHtml(item)}" ${item === selectedCity ? "selected" : ""}>${escapeHtml(item)}</option>`,
    )
    .join("");

  return `<section class="page">
    <a class="back-link" href="#/profile">← 返回“我的”</a>
    ${pageHeading("REGION", "你主要生活在哪里？", "地区只用于筛选适用政策；不会申请定位权限，也不会上传。")}
    <form class="form-stack" data-region-form>
      <label class="field">
        <span class="field-heading"><span>省级地区</span><small aria-hidden="true">01</small></span>
        <span class="select-shell">
          <select name="province" autocomplete="address-level1" data-region-province aria-describedby="province-help">
            <option value="">暂不选择省级地区</option>
            ${provinceOptions}
          </select>
        </span>
        <small id="province-help">覆盖中国大陆 31 个省级地区；可以留空</small>
      </label>
      <label class="field">
        <span class="field-heading"><span>城市 / 地级行政区</span><small aria-hidden="true">02</small></span>
        <span class="select-shell">
          <select name="city" autocomplete="address-level2" data-region-city aria-describedby="city-help" ${selectedProvince ? "" : "disabled"}>
            <option value="">${selectedProvince ? "暂不选择城市 / 地区" : "请先选择省级地区"}</option>
            ${cityOptions}
          </select>
        </span>
        <small id="city-help" data-region-city-help>${selectedProvince ? `只显示${escapeHtml(selectedProvince)}下的选项；城市可不选` : "选择省级地区后，这里会显示对应选项"}</small>
      </label>
      <div class="region-selection-summary" aria-live="polite">
        <span>当前选择</span>
        <strong data-region-summary-value>${escapeHtml(selectionLabel)}</strong>
      </div>
      ${hasUnsupportedSavedRegion ? '<div class="notice">之前保存的地区不在当前选项中，请重新选择后保存。</div>' : ""}
      <div><button class="primary-button" type="submit">保存在这台设备</button></div>
      <p class="region-data-note">选项来源：<a href="${escapeHtml(REGION_DATA_SOURCE)}" target="_blank" rel="noopener noreferrer">中国·国家地名信息库</a> · 数据截至 ${escapeHtml(REGION_DATA_UPDATED_AT)}</p>
    </form>
    <div class="privacy-box"><strong>为什么不用自动定位？</strong><p>这个产品不需要精确 GPS。手动选择足以判断省、市级内容，而且更容易理解与删除。</p></div>
  </section>`;
}

function schoolView(context) {
  const school = context.localState.school;
  const catalog = context.schoolCatalog;
  const hasSchool = Boolean(school.name);
  const locationLabel = [school.province, school.city].filter(Boolean).join(" / ");
  const detailLabel = [locationLabel, school.level].filter(Boolean).join(" · ");
  const sourceUrl = catalog.sourceUrl || "https://www.moe.gov.cn/jyb_xxgk/s5743/s5744/202606/t20260618_1441074.html";

  return `<section class="page">
    <a class="back-link" href="#/profile">← 返回“我的”</a>
    ${pageHeading("LOCAL SCHOOL", "把学校资料留在这台设备", "用于整理校园生活相关内容；不是学校账号认证，也不会上传。")}

    <article class="school-profile-card" aria-live="polite">
      <div class="school-card-topline"><span>校园资料卡</span><strong>仅本机</strong></div>
      <div class="school-card-name" data-school-card-name>${escapeHtml(school.name || "尚未设置学校")}</div>
      <dl>
        <div><dt>校区</dt><dd data-school-card-campus>${escapeHtml(school.campus || "未填写")}</dd></div>
        <div><dt>专业</dt><dd data-school-card-major>${escapeHtml(school.major || "未填写")}</dd></div>
        <div><dt>名单</dt><dd data-school-card-status>${school.code ? "教育部名单" : hasSchool ? "手动填写" : "等待设置"}</dd></div>
      </dl>
      <p data-school-card-meta>${escapeHtml(detailLabel || (hasSchool ? "本地手动填写" : "保存后只在此设备显示"))}</p>
    </article>

    <form class="form-stack school-form" data-school-form novalidate>
      <label class="field">
        <span class="field-heading"><span>学校名称</span><small>必填</small></span>
        <span class="school-combobox">
          <input
            type="search"
            name="schoolName"
            value="${escapeHtml(school.name)}"
            placeholder="输入至少两个字，例如：清华大学"
            maxlength="100"
            autocomplete="off"
            role="combobox"
            aria-autocomplete="list"
            aria-expanded="false"
            aria-controls="school-search-results"
            data-school-query
          />
          <input type="hidden" name="schoolCode" value="${escapeHtml(school.code)}" />
          <input type="hidden" name="schoolProvince" value="${escapeHtml(school.province)}" />
          <input type="hidden" name="schoolCity" value="${escapeHtml(school.city)}" />
          <input type="hidden" name="schoolLevel" value="${escapeHtml(school.level)}" />
          <span class="school-search-mark" aria-hidden="true">⌕</span>
        </span>
        <div id="school-search-results" class="school-search-results" role="listbox" data-school-results hidden></div>
        <small data-school-search-help>输入学校名称后，从教育部普通高校名单中选择；找不到时可直接保存手动填写的名称</small>
      </label>

      <label class="field">
        <span class="field-heading"><span>校区</span><small>可选</small></span>
        <input name="campus" value="${escapeHtml(school.campus)}" placeholder="例如：大学城校区" maxlength="80" autocomplete="off" data-school-campus />
        <small>同一学校有多个校区时再填写</small>
      </label>

      <label class="field">
        <span class="field-heading"><span>专业</span><small>可选</small></span>
        <input name="major" value="${escapeHtml(school.major)}" placeholder="例如：计算机科学与技术" maxlength="80" autocomplete="off" data-school-major />
        <small>只用于未来整理专业相关办事内容</small>
      </label>

      <div class="school-form-actions">
        <button class="primary-button" type="submit">保存在这台设备</button>
        ${hasSchool ? '<button class="text-action school-clear-action" type="button" data-action="clear-school">清除学校资料</button>' : ""}
      </div>
      <p class="region-data-note">学校选项：<a href="${escapeHtml(sourceUrl)}" target="_blank" rel="noopener noreferrer">${escapeHtml(catalog.sourceName || "中华人民共和国教育部")}《${escapeHtml(catalog.sourceTitle || "全国高等学校名单")}》</a> · ${catalog.schools.length ? `${catalog.schools.length} 所` : "名单暂不可用，可手动填写"} · 截至 ${escapeHtml(catalog.updatedAt || "2026-06-17")}</p>
    </form>

    <div class="privacy-box"><strong>“本地绑定”是什么意思？</strong><p>这里保存的是你自己填写的学校资料，不会连接学校系统，也不代表已经完成学生身份认证。不要填写学号、学校密码或身份证信息。</p></div>
  </section>`;
}

function stageView(context) {
  return `<section class="page">
    <a class="back-link" href="#/profile">← 返回“我的”</a>
    ${pageHeading("STAGE", "你现在处于哪个阶段？", "阶段仅用于内容排序，不影响你浏览全部手册。")}
    <ul class="stage-list">${STAGE_LABELS.map((label) => `<li>${escapeHtml(label)}</li>`).join("")}</ul>
    <div class="notice">这些中文选项已写入产品规范，但数据层 identifier 尚未由项目负责人确认。为避免保存无法迁移的数据，网页版暂不记录选择；确认标识后即可启用。</div>
    ${contentFootnote(context.rejectedCount)}
  </section>`;
}

function profileView(context) {
  const favoriteCount = context.localState.favorites.length;
  const historyCount = context.localState.history.length;
  return `<section class="page">
    ${pageHeading("LOCAL PROFILE", "我的", "无需注册。所有个性化数据都留在当前浏览器。")}
    <ul class="settings-list">
      <li><a href="#/region"><span><strong>所在地区</strong><br /><small>${escapeHtml(userRegionLabel(context.localState.region))}</small></span><span aria-hidden="true">→</span></a></li>
      <li><a href="#/school"><span><strong>我的学校</strong><br /><small>${escapeHtml(schoolProfileLabel(context.localState.school))}</small></span><span aria-hidden="true">→</span></a></li>
      <li><a href="#/stage"><span><strong>目前阶段</strong><br /><small>等待正式标识确认</small></span><span aria-hidden="true">→</span></a></li>
      <li><a href="#/favorites"><span><strong>收藏</strong><br /><small>${favoriteCount} 篇</small></span><span aria-hidden="true">→</span></a></li>
      <li class="settings-row"><span><strong>最近阅读</strong><br /><small>${historyCount} 篇，最多保留 50 条</small></span><span>本地</span></li>
      <li class="settings-row"><span><strong>内容包</strong><br /><small>${context.guides.length} 篇可用，${context.rejectedCount} 篇已跳过</small></span><span>离线</span></li>
    </ul>

    <section class="section-block">
      <div class="section-heading"><h2>安装与离线</h2><small>支持的浏览器可安装</small></div>
      <p class="lede">首次在线打开后，核心界面与随包内容会缓存到设备；断网也可以继续阅读。</p>
      <button class="secondary-button" type="button" data-action="install" data-install-button hidden>安装到设备</button>
    </section>

    <section class="section-block">
      <div class="section-heading"><h2>隐私</h2><small>无账号 · 无追踪</small></div>
      <div class="privacy-box">
        <p>网页版不设置后端登录，不接入广告或第三方统计，不收集姓名、证件号、手机号、银行卡资料、学校账号或精确位置。</p>
        <p>地区、学校资料、收藏、历史和清单状态保存在浏览器本地存储中。清除本站数据或更换浏览器后，这些记录会消失。</p>
        <button class="danger-button" type="button" data-action="clear-local-data">清除本机记录</button>
      </div>
    </section>
    ${contentFootnote(context.rejectedCount)}
  </section>`;
}

function notFoundView(context) {
  return `<section class="page">
    ${pageHeading("404", "这一页不存在", "链接可能已变化，或者对应内容没有通过格式校验。")}
    ${emptyState("回到手册目录", "你可以从首页重新选择场景或分类。")}
    ${contentFootnote(context.rejectedCount)}
  </section>`;
}

export function renderRoute(route, context) {
  switch (route.name) {
    case "home":
      return homeView(context);
    case "search":
      return searchView(route, context);
    case "favorites":
      return favoritesView(context);
    case "profile":
      return profileView(context);
    case "category":
      return categoryView(route, context);
    case "guide":
      return guideView(route, context);
    case "emergency":
      return emergencyView(context);
    case "region":
      return regionView(context);
    case "school":
      return schoolView(context);
    case "stage":
      return stageView(context);
    default:
      return notFoundView(context);
  }
}
