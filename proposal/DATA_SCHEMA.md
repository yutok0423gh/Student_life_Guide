# DATA_SCHEMA.md

# 初级成年人入门手册 — Data Schema

## 1. Schema Rules

本文件定義 MVP Content Schema。

Coding AI 必須遵循：

1. 不得自行修改既有 JSON key；
2. 不得自行改變 key 大小寫；
3. 不得自行改成其他命名風格；
4. 不得根據相似字段猜測；
5. 若 schema 與實際程式碼衝突，先讀取實際程式碼並回報；
6. 如果缺少必要欄位，必須明確提出，不得自行創造近似欄位。

---

## 2. Category IDs

固定值：

```text
education
housing
health
finance
career
documents
safety
life
```

不得修改。

---

## 3. Guide Schema

Guide JSON：

```json
{
  "id": "guide_rent_deposit_not_returned",
  "title": "房東不退押金怎麼辦？",
  "summary": "退租後房東拒絕退還押金時的處理流程。",
  "categoryId": "housing",
  "scenarioIds": [
    "renting",
    "dispute"
  ],
  "keywords": [
    "租房",
    "押金",
    "房東"
  ],
  "aliases": [
    "房東不退錢",
    "押金不退",
    "退租押金"
  ],
  "region": {
    "level": "COUNTRY",
    "province": null,
    "city": null
  },
  "quickAnswer": "",
  "steps": [],
  "checklist": [],
  "warnings": [],
  "sections": [],
  "sources": [],
  "verifiedAt": "2026-08-29",
  "version": 1
}
```

---

## 4. Guide Fields

| Field | Type | Required | Description |
|---|---|---:|---|
| `id` | String | Yes | Guide 唯一識別符 |
| `title` | String | Yes | Guide 標題 |
| `summary` | String | Yes | 搜尋與列表摘要 |
| `categoryId` | String | Yes | 固定 Category ID |
| `scenarioIds` | Array<String> | Yes | 場景分類 |
| `keywords` | Array<String> | Yes | 搜尋 keyword |
| `aliases` | Array<String> | Yes | 自然語言別名 |
| `region` | Object | Yes | 適用地區 |
| `quickAnswer` | String | Yes | 30 秒答案 |
| `steps` | Array<Step> | Yes | 操作步驟 |
| `checklist` | Array<ChecklistItem> | Yes | 材料清單 |
| `warnings` | Array | Yes | 警告資訊 |
| `sections` | Array | Yes | 補充內容 |
| `sources` | Array<Source> | Yes | 官方來源 |
| `verifiedAt` | String | Yes | 最後核驗日期 |
| `version` | Integer | Yes | Guide content version |

空 Array 可以使用 `[]`，不得省略 key。

---

## 5. Identifier Rules

### `id`

必須：

- 唯一
- 穩定
- 不因標題修改而改變
- 不使用 index 作為長期 ID

推薦格式：

```text
guide_<topic>
```

例如：

```text
guide_rent_deposit_not_returned
```

已建立的 ID 不得在沒有 migration 設計的情況下修改。

---

## 6. Region Schema

```json
{
  "level": "COUNTRY",
  "province": null,
  "city": null
}
```

### `level`

固定允許：

```text
COUNTRY
PROVINCE
CITY
```

### COUNTRY

```json
{
  "level": "COUNTRY",
  "province": null,
  "city": null
}
```

### PROVINCE

```json
{
  "level": "PROVINCE",
  "province": "廣東省",
  "city": null
}
```

### CITY

```json
{
  "level": "CITY",
  "province": "廣東省",
  "city": "深圳市"
}
```

Coding AI 不得自行增加其他 level enum。

---

## 7. Step Schema

固定：

```json
{
  "id": "step_01",
  "order": 1,
  "title": "保存租賃合同",
  "description": "保存完整合同，包括附件和補充協議。"
}
```

字段：

| Field | Type | Required |
|---|---|---:|
| `id` | String | Yes |
| `order` | Integer | Yes |
| `title` | String | Yes |
| `description` | String | Yes |

排序依 `order`。

不得依 Array index 推定正式順序。

---

## 8. Checklist Schema

固定：

```json
{
  "id": "check_contract",
  "text": "租賃合同",
  "required": true
}
```

字段：

| Field | Type | Required |
|---|---|---:|
| `id` | String | Yes |
| `text` | String | Yes |
| `required` | Boolean | Yes |

Checklist completion state 不寫回 bundled JSON。

完成狀態應另存本地資料庫。

---

## 9. Source Schema

固定：

```json
{
  "id": "source_001",
  "sourceName": "",
  "sourceTitle": "",
  "sourceUrl": "",
  "sourceLevel": "NATIONAL",
  "publishedAt": null,
  "verifiedAt": "2026-08-29"
}
```

字段：

| Field | Type | Required |
|---|---|---:|
| `id` | String | Yes |
| `sourceName` | String | Yes |
| `sourceTitle` | String | Yes |
| `sourceUrl` | String | Yes |
| `sourceLevel` | String | Yes |
| `publishedAt` | String/null | Yes |
| `verifiedAt` | String | Yes |

---

## 10. Source Level

固定允許：

```text
NATIONAL
PROVINCIAL
CITY
OTHER_OFFICIAL
```

不得自行加入：

- BLOG
- SOCIAL
- MEDIA
- AI
- FORUM

如果未來需要非官方來源，需修改本 schema 文件後再實作。

---

## 11. Source Policy

涉及：

- 法律
- 政策
- 醫療行政
- 醫保
- 社保
- 稅務
- 公安
- 金融監管
- 教育行政

來源優先：

1. 中央政府官方網站
2. 國務院部委官方網站
3. 省級政府或官方部門
4. 市級政府或官方部門
5. 其他官方公共服務平台

不得把自媒體內容當作規則依據。

---

## 12. verifiedAt

格式固定：

```text
YYYY-MM-DD
```

例：

```text
2026-08-29
```

不得存：

```text
29/08/2026
Aug 29
2026.08.29
```

---

## 13. publishedAt

格式：

```text
YYYY-MM-DD
```

如果官方來源沒有明確發布日期：

```json
"publishedAt": null
```

不得猜測日期。

---

## 14. Warning

目前主 Guide Schema 定義 `warnings` 為 Array，但尚未固定 object schema。

在正式實作 Warning model 前，不得自行創造字段。

需要實作時，先由專案負責人確認 Warning schema，再更新本文件。

---

## 15. Sections

目前主 Guide Schema 定義 `sections` 為 Array，但尚未固定 object schema。

不得自行假設：

- heading
- title
- body
- content
- markdown

中的任一字段。

要實作 sections 時，必須先確認正式 schema。

---

## 16. Scenario IDs

`scenarioIds` 已定義為 `Array<String>`，但目前沒有固定全集。

Coding AI 可以讀取 Content 中實際存在的 scenario ID。

不得自行建立一套新的 scenario taxonomy 並修改現有內容。

如果需要新增 scenario ID，應先更新 Content 規格。

---

## 17. Search Fields

搜尋只使用已存在字段：

- `title`
- `summary`
- `keywords`
- `aliases`

不得把 Source URL 或其他字段默認加入搜尋。

---

## 18. Local Persistence Schema

### 18.1 Favorite

最低需要：

```text
guideId
favoritedAt
```

`guideId` 必須引用 Guide `id`。

### 18.2 Reading History

最低需要：

```text
guideId
lastOpenedAt
```

### 18.3 Checklist State

最低需要：

```text
guideId
checklistItemId
isChecked
```

不得使用 checklist text 作為唯一識別。

---

## 19. DataStore Keys

DataStore 至少需要表達：

- onboarding completed
- user stage
- province
- city

實際 DataStore key 名稱尚未固定。

如果專案內不存在，Coding AI 必須先提出 key 定義，再實作，不得假設已有名稱。

---

## 20. User Stage Values

產品允許：

- 高中畢業
- 大一
- 大二
- 大三
- 大四
- 研究生
- 應屆畢業
- 剛工作

但資料層 enum identifier 尚未定義。

Coding AI 不得自行決定正式 enum key。

實作前應先由專案負責人確認 identifier。

---

## 21. Content Validation

每篇 Guide import 前至少驗證：

- `id` 非空
- `id` 唯一
- `title` 非空
- `categoryId` 屬於固定 Category ID
- `region.level` 屬於固定值
- Step `id` 在 Guide 內唯一
- Checklist `id` 在 Guide 內唯一
- Source `id` 在 Guide 內唯一
- `verifiedAt` 格式正確
- `version` > 0

---

## 22. Invalid Content Handling

單篇 Guide 無效：

```text
Log
↓
Skip invalid guide
↓
Continue import
```

不得讓整體 App Crash。

---

## 23. Example Valid Guide Skeleton

```json
{
  "id": "guide_example",
  "title": "示例指南",
  "summary": "示例摘要。",
  "categoryId": "life",
  "scenarioIds": [],
  "keywords": [],
  "aliases": [],
  "region": {
    "level": "COUNTRY",
    "province": null,
    "city": null
  },
  "quickAnswer": "這是一段示例快速答案。",
  "steps": [
    {
      "id": "step_01",
      "order": 1,
      "title": "第一步",
      "description": "完成第一個操作。"
    }
  ],
  "checklist": [
    {
      "id": "check_example",
      "text": "示例材料",
      "required": false
    }
  ],
  "warnings": [],
  "sections": [],
  "sources": [],
  "verifiedAt": "2026-08-29",
  "version": 1
}
```

---

## 24. Schema Change Rule

任何 schema change 必須：

1. 先修改本文件；
2. 確認 backward compatibility；
3. 定義 migration；
4. 更新 parser test；
5. 更新 Room migration（如果需要）；
6. 再修改正式程式碼。

不得先改 code 再事後猜 schema。
