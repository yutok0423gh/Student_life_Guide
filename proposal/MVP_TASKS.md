# MVP_TASKS.md

# 初级成年人入门手册 — MVP Implementation Plan

## 1. Execution Rule

開發必須遵循：

> **實作一項 → 驗證一項 → 再進入下一項**

多模組不得同時大範圍展開。

每完成一個 Phase，回報：

```text
Implemented

Files Changed

Tests

Test Result

Known Issues
```

如果測試失敗，先修復當前 Phase，不進下一 Phase。

---

# 2. P0 Priority

P0 功能：

- Android Project Foundation
- Navigation
- Theme
- Data Model
- Local JSON Loader
- Room
- Guide
- Home
- Category
- Search
- Favorites
- History
- Region
- Emergency
- Source
- Offline

---

# 3. Phase 0 — Repository Inspection

## Goal

在修改任何程式碼之前確認實際專案狀態。

## Tasks

- [ ] 讀取 repository root
- [ ] 讀取 `settings.gradle` / `settings.gradle.kts`
- [ ] 讀取 root `build.gradle` / `build.gradle.kts`
- [ ] 讀取 app module Gradle
- [ ] 確認實際 package identifier
- [ ] 確認 `minSdk`
- [ ] 確認 `compileSdk`
- [ ] 確認 `targetSdk`
- [ ] 確認現有 Compose 狀態
- [ ] 確認現有 Navigation
- [ ] 確認現有 dependency
- [ ] 確認是否已存在 Room / Hilt / DataStore

## Verification

輸出：

```text
Current project structure
Current package identifier
Current SDK values
Current dependencies
Conflicts with specification
```

如果專案尚未建立，明確報告。

不得猜 package identifier。

---

# 4. Phase 1 — Foundation

## Goal

建立可啟動且具有基本架構的 Android App。

## Tasks

### Project

- [ ] Kotlin
- [ ] Jetpack Compose
- [ ] Material 3
- [ ] Hilt
- [ ] Navigation Compose
- [ ] Room
- [ ] DataStore
- [ ] kotlinx.serialization
- [ ] Coroutines

### Theme

- [ ] Material 3 Theme
- [ ] Typography
- [ ] Basic spacing conventions
- [ ] Light theme
- [ ] Dark theme compatibility baseline

### Navigation

建立：

- [ ] Home
- [ ] Search
- [ ] Favorites
- [ ] Profile

並建立非 Bottom Navigation Route：

- [ ] Category
- [ ] Guide Detail
- [ ] Emergency
- [ ] Region Picker
- [ ] Stage Picker

## Verification

- [ ] App builds
- [ ] App launches
- [ ] 四個 Bottom Navigation tab 可切換
- [ ] Back navigation 正常
- [ ] 不存在 blocking crash

只有全部通過才進 Phase 2。

---

# 5. Phase 2 — Data Foundation

## Goal

建立正式 Content Model 與 Local persistence。

## Tasks

### Guide Models

依 `DATA_SCHEMA.md` 建立：

- [ ] Guide
- [ ] Region
- [ ] Step
- [ ] ChecklistItem
- [ ] Source

注意：

`warnings` 與 `sections` object schema 尚未固定。

不得自行猜測結構。

如果 UI 暫時不需要，保持可解析空 Array 的方式。

### Room

建立：

- [ ] Guide persistence
- [ ] Source persistence
- [ ] Step persistence
- [ ] Checklist definition persistence
- [ ] Favorite persistence
- [ ] Checklist state persistence
- [ ] Reading history persistence

### DataStore

建立：

- [ ] onboarding state
- [ ] user region
- [ ] user stage

但正式 DataStore key 名稱若尚未定義，先提出再實作。

### JSON

- [ ] assets content folder
- [ ] parser
- [ ] validator
- [ ] importer
- [ ] content version mechanism

## Verification

### Unit Tests

- [ ] Valid Guide parses
- [ ] Invalid Guide does not crash
- [ ] Invalid `categoryId` rejected
- [ ] Invalid `region.level` rejected
- [ ] Duplicate Guide ID detected
- [ ] Duplicate Step ID detected
- [ ] Duplicate Checklist ID detected
- [ ] `verifiedAt` validation
- [ ] Import into Room works

### Manual

- [ ] Airplane mode launch
- [ ] Bundled Guide appears

---

# 6. Phase 3 — Guide Feature

## Goal

可以完整閱讀一篇 Guide。

## Tasks

### Category

- [ ] Category list
- [ ] Category detail
- [ ] Guide list by category

Category ID 固定：

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

### Guide Detail

順序：

- [ ] Header
- [ ] Quick Answer
- [ ] Steps
- [ ] Checklist
- [ ] Warnings placeholder / supported UI
- [ ] Sections placeholder / supported UI
- [ ] Sources

### Checklist

- [ ] Toggle
- [ ] Persist
- [ ] Restart persistence

### Source

- [ ] Official source card
- [ ] Source metadata
- [ ] Open source URL

## Verification

- [ ] Category → Guide works
- [ ] Guide order correct
- [ ] Checklist state persists
- [ ] Source can open
- [ ] Offline Guide readable
- [ ] Large font does not break main content

---

# 7. Phase 4 — Home

## Goal

建立核心首頁。

## Required Order

1. Greeting
2. Region Selector
3. Search Bar
4. Emergency Entry
5. 「我現在……」
6. 根據你的階段
7. 八大分類
8. 最近閱讀

## Tasks

- [ ] Greeting
- [ ] Region selector entry
- [ ] Search entry
- [ ] Emergency card
- [ ] Scenario cards
- [ ] Stage recommendation area
- [ ] Category grid/list
- [ ] Recent guides

### Initial Scenario Labels

- [ ] 生病了
- [ ] 掛科了
- [ ] 被騙了
- [ ] 東西丟了
- [ ] 想租房
- [ ] 想找實習
- [ ] 想辭職
- [ ] 遇到欠薪
- [ ] 銀行卡出問題
- [ ] 不知道怎麼辦

## Verification

- [ ] Home opens
- [ ] All primary cards clickable
- [ ] Recent history appears after reading Guide
- [ ] Region state displays correctly

---

# 8. Phase 5 — Search

## Goal

使用自然語言找到 Guide。

## Search Fields

只搜尋：

- `title`
- `summary`
- `keywords`
- `aliases`

## Ranking

優先：

1. Title
2. Alias
3. Keyword
4. Summary

## Tasks

- [ ] Search UI
- [ ] Debounced or explicit search
- [ ] Repository search
- [ ] Ranking
- [ ] Empty query state
- [ ] No result state

## Required Test Example

Guide：

```text
房東不退押金怎麼辦？
```

Aliases：

```text
房東不退錢
租房押金
押金被扣
退租不退押金
```

測試 query：

```text
房東不給錢
```

需要能找到相關 Guide。

## Verification

- [ ] Title match
- [ ] Alias match
- [ ] Keyword match
- [ ] Summary match
- [ ] No result UI
- [ ] Offline search

---

# 9. Phase 6 — Favorites & History

## Favorites

Tasks：

- [ ] Favorite button
- [ ] Favorite Room state
- [ ] Favorites list
- [ ] Empty state
- [ ] Restart persistence

## History

Tasks：

- [ ] Record Guide open
- [ ] Update `lastOpenedAt`
- [ ] Recent list
- [ ] Same Guide does not create incorrect duplicates

## Verification

- [ ] Favorite persists after restart
- [ ] Unfavorite works
- [ ] History ordering correct
- [ ] Recent Guide appears on Home

---

# 10. Phase 7 — Region

## Goal

支援使用者手動選擇生活地區。

## Tasks

- [ ] Province picker
- [ ] City picker
- [ ] Skip option
- [ ] Persist selection
- [ ] Display on Home
- [ ] Display Guide region

## Rules

- 不要求 Location Permission
- 不自動使用 GPS
- 不猜城市

## Verification

- [ ] Select province
- [ ] Select city
- [ ] Restart persistence
- [ ] Skip works

---

# 11. Phase 8 — User Stage

## Tasks

支援：

- [ ] 高中畢業
- [ ] 大一
- [ ] 大二
- [ ] 大三
- [ ] 大四
- [ ] 研究生
- [ ] 應屆畢業
- [ ] 剛工作

注意：

正式 enum identifier 尚未定義。

Coding AI 不得自行決定正式 identifier。

實作前先確認。

## Use

MVP 僅用於：

- 首頁內容排序
- Stage recommendation

不得影響法律或政策判斷。

---

# 12. Phase 9 — Emergency

## Required Scenarios

- [ ] 被詐騙
- [ ] 銀行卡被盜
- [ ] 手機丟失
- [ ] 身份證丟失
- [ ] 發生交通事故
- [ ] 有人受傷
- [ ] 遇到暴力
- [ ] 被偷拍或騷擾

## UI Rules

- 大字體
- 高對比
- 少文字
- 大 touch target
- 行動步驟優先
- 離線可用
- 不展示廣告
- 不展示不相關推薦

## Verification

- [ ] Home one-step access
- [ ] Airplane mode works
- [ ] Large font works
- [ ] No irrelevant modules shown

---

# 13. Phase 10 — Onboarding

## Tasks

First page：

```text
欢迎来到初级成年人入门手册

第一次獨立生活，
很多事情沒人教你。

我們希望告訴你：
遇到事情時，下一步該做什麼。
```

Then：

- [ ] Stage selection
- [ ] Region selection
- [ ] Skip
- [ ] Persist onboarding complete

## Verification

- [ ] First launch shows onboarding
- [ ] Skip works
- [ ] Second launch does not repeat unexpectedly

---

# 14. Phase 11 — Initial Content

至少完成 25 篇：

- [x] 第一次住宿舍要準備什麼
- [x] 第一次租房要注意什麼
- [x] 房東不退押金怎麼辦
- [x] 身份證丟了怎麼辦
- [x] 手機丟了怎麼辦
- [x] 銀行卡丟了怎麼辦
- [x] 銀行卡被凍結怎麼辦
- [x] 轉錯帳怎麼辦
- [x] 第一次去醫院怎麼掛號
- [x] 大學生醫保怎麼用
- [x] 異地看病怎麼辦
- [x] 掛科了怎麼辦
- [x] 補考和重修有什麼區別
- [x] 想轉專業怎麼辦
- [x] 想休學怎麼辦
- [x] 第一次找實習要注意什麼
- [x] 實習協議是什麼
- [x] 實習不發工資怎麼辦
- [x] 第一次簽勞動合同
- [x] 公司拖欠工資怎麼辦
- [x] 三方協議是什麼
- [x] 五險一金是什麼
- [x] 被電信詐騙了怎麼辦
- [x] 網購被坑怎麼維權
- [x] 怎麼報警

## Content Rule

涉及政策與法律時：

- [x] 官方來源
- [x] Source metadata
- [x] `verifiedAt`
- [x] Region
- [x] 不確定內容明確標示未核驗

不得由 AI 自行創造政策答案。

---

# 15. Phase 12 — QA

## Automated

- [ ] Unit tests
- [ ] UI tests
- [ ] Room persistence tests
- [ ] Search tests
- [ ] JSON validation tests

## Manual

- [ ] Fresh install
- [ ] Upgrade install
- [ ] Airplane mode
- [ ] App restart
- [ ] Large font
- [ ] Dark theme compatibility
- [ ] Empty favorites
- [ ] Empty history
- [ ] Search no results
- [ ] Invalid JSON
- [ ] Emergency flow
- [ ] Rotation / configuration change where relevant

---

# 16. MVP Definition of Done

以下條件全部滿足才算完成：

- [ ] App 可以正常安裝
- [ ] 無需登入
- [ ] 首頁正常顯示
- [ ] 八大分類正常工作
- [x] 至少 25 篇 Guide
- [ ] 搜尋正常
- [ ] Alias Search 正常
- [ ] Guide Detail 正常
- [ ] Checklist 可以保存
- [ ] 收藏可以保存
- [ ] 閱讀歷史可以保存
- [ ] 地區可以選擇
- [ ] Emergency 頁正常
- [x] 官方來源可以查看
- [ ] 完全離線可以閱讀核心 Guide
- [ ] App 重啟後 Local State 不丟失
- [ ] 不存在 Blocking Crash

---

# 17. Coding AI Rules

Coding AI 必須：

1. 先讀取實際 repository；
2. 不猜 identifier；
3. 不猜 package name；
4. 不猜 JSON key；
5. 不改 Category ID；
6. 不改 Bottom Navigation；
7. 不加入 Backend；
8. 不加入 Login；
9. 不加入廣告；
10. 不加入第三方 Tracking；
11. 不加入自由聊天 AI；
12. 不生成未核驗政策；
13. 修 Bug 前先定位 Root Cause；
14. 修改後跑相關測試；
15. 每 Phase 回報結果。

---

# 18. Standard Phase Report

每完成一個 Phase，使用以下格式：

```text
Phase:
<phase name>

Implemented:
- ...

Files Changed:
- ...

Tests:
- ...

Test Result:
PASS / FAIL

Known Issues:
- ...

Next:
<next phase only if current phase passes>
```
