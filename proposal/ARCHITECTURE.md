# ARCHITECTURE.md

# 初级成年人入门手册 — Android Architecture

## 1. Architecture Goal

建立一個：

- Native Android
- Offline First
- Local Content First
- 易於測試
- 易於擴展
- 不依賴 Backend

的 Android App。

MVP 必須優先保證：

1. App 可以穩定啟動；
2. Guide 可以離線讀取；
3. 搜尋與收藏可以本地使用；
4. 地區、歷史、Checklist 狀態可持久化；
5. 無網路時核心功能仍可使用。

---

## 2. Technical Stack

固定方向：

| 項目 | 技術 |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| Navigation | Navigation Compose |
| Local Database | Room |
| Preferences | DataStore |
| JSON | kotlinx.serialization |
| Async | Kotlin Coroutines |
| Dependency Injection | Hilt |

除非專案負責人明確修改需求，不使用：

- Flutter
- React Native
- WebView App

---

## 3. Android SDK

初始要求：

```text
minSdk = 26
```

`targetSdk` 與 `compileSdk` 必須在建立專案時讀取實際 Android Studio / Gradle / SDK 環境後決定。

不得自行猜測不存在的 SDK 版本。

---

## 4. Package Identifier

正式 package identifier 尚未定義。

Coding AI 不得自行生成正式 package name。

開始實作前，如果專案中尚不存在 package identifier，必須向專案負責人確認。

---

## 5. High-Level Architecture

MVP 資料流：

```text
Bundled JSON
    ↓
Content Importer
    ↓
Room Database
    ↓
Repository
    ↓
ViewModel
    ↓
Jetpack Compose UI
```

使用者偏好：

```text
UI
 ↓
ViewModel
 ↓
DataStore
```

Checklist / Favorites / History：

```text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
Room
```

---

## 6. Layer Responsibilities

### 6.1 `data`

負責：

- Room Entity
- DAO
- DataStore
- JSON parsing
- Bundled Content Import
- Repository implementation

不得放：

- Compose UI
- Screen-specific ViewModel
- UI navigation logic

### 6.2 `domain`

負責：

- Domain Model
- Repository interface
- Use Case（需要時）

如果 MVP 規模較小，可以保持輕量，不需要為了形式而建立大量 Use Case。

### 6.3 `ui`

負責：

- Compose Screen
- Component
- Theme
- UI State

### 6.4 `navigation`

負責：

- Route 定義
- NavHost
- Screen navigation

### 6.5 `util`

僅存放真正跨模組且無法明確歸類的 utility。

不得把大量業務邏輯放進 `util`。

---

## 7. Feature Modules / Packages

功能區域：

- `home`
- `search`
- `guide`
- `favorite`
- `profile`
- `emergency`
- `region`

實際 package 結構必須基於現有專案結構建立。

如果專案已存在結構，先讀取現有檔案，不得直接覆蓋成另一套結構。

---

## 8. Recommended Project Structure

如果專案尚未建立，可採用：

```text
app/
  src/main/
    java/<package>/
      data/
        local/
        repository/
        content/
      domain/
        model/
        repository/
      ui/
        home/
        search/
        guide/
        favorite/
        profile/
        emergency/
        region/
        components/
        theme/
      navigation/
      util/
    assets/
      content/
```

注意：

- `<package>` 必須使用實際 package identifier；
- 不得自行猜測 `<package>`。

---

## 9. Navigation

Bottom Navigation 固定：

- Home
- Search
- Favorites
- Profile

推薦 route identifier：

```text
home
search
favorites
profile
```

Guide Detail、Category、Emergency、Region Picker 等頁面不屬於 Bottom Navigation Item。

實際 route 常數應集中管理。

不得在不同 Screen 中散落硬編碼 route string。

---

## 10. Suggested Screens

MVP Screen：

```text
HomeScreen
SearchScreen
FavoritesScreen
ProfileScreen
CategoryScreen
GuideDetailScreen
EmergencyScreen
RegionPickerScreen
StagePickerScreen
```

可選：

```text
OnboardingScreen
SourceDetailScreen
```

---

## 11. UI State

每個需要非同步資料的 Screen 應明確定義 UI state。

例：

```kotlin
data class GuideDetailUiState(
    val isLoading: Boolean,
    val guide: Guide? = null,
    val error: String? = null
)
```

實際 class 名稱如果專案中已存在，必須讀取後沿用，不得重複建立近似名稱。

---

## 12. Offline First

### 12.1 First Install

App 第一次啟動：

```text
Bundled JSON
    ↓
Validate
    ↓
Import into Room
    ↓
Mark content version
```

### 12.2 Subsequent Launch

如果本地內容版本已存在：

```text
Open Room
    ↓
Load data
```

MVP 不需要 Remote API。

---

## 13. Content Version

應保存 Content Version。

最低要求：

```text
contentVersion
```

內容更新時可以判斷是否需要重新 import。

如果目前資料模型中沒有對應持久化結構，Phase 1 實作時必須明確定義並測試。

不得偷偷依賴 App Version Code 代替 Content Version。

---

## 14. Room Responsibilities

Room 至少保存：

- Guide
- Source
- Step
- Checklist definition
- Favorite state
- Checklist completion state
- Reading history

也可以將 keywords / aliases 使用 converter 保存，具體方式需依實際 schema 實作。

---

## 15. DataStore Responsibilities

DataStore 保存：

- Onboarding completed
- User stage
- Province
- City
- UI preference（後續）
- 其他輕量設定

不得把大量 Guide content 放入 DataStore。

---

## 16. Search Architecture

MVP 搜尋資料源：

- title
- summary
- keywords
- aliases

搜尋流程：

```text
query
 ↓
normalize whitespace
 ↓
repository search
 ↓
rank results
 ↓
UI
```

不得使用需要網路的搜尋服務。

第一版不需要 Embedding 或 AI Search。

---

## 17. Search Ranking

最低排序邏輯：

1. Title exact / strong match
2. Alias match
3. Keyword match
4. Summary match

如果未實作複雜 ranking，至少保證 Title 與 Alias 優先。

---

## 18. Guide Detail

Guide Detail 順序固定：

1. Header
2. Quick Answer
3. Steps
4. Checklist
5. Warnings
6. Sections
7. Sources

不得把 Sources 或背景長文放到 Quick Answer 之前。

---

## 19. Checklist Persistence

Checklist 完成狀態至少依：

- `guideId`
- `checklistItemId`

唯一定位。

資料不得只依文字內容定位。

Checklist definition 改變時，不得以 index 進行對應。

---

## 20. Favorites Persistence

Favorite 最低資料：

- `guideId`
- `favoritedAt`

不得直接複製整篇 Guide 保存成 Favorite。

---

## 21. Reading History

History 最低資料：

- `guideId`
- `lastOpenedAt`

再次打開同一 Guide 時更新 `lastOpenedAt`。

首頁「最近閱讀」按照 `lastOpenedAt` 降序。

---

## 22. Region

使用者地區資料：

- Province
- City

不強制 GPS。

Region Picker 資料可以 MVP 內建。

政策內容依 Guide 自身 `region` 判斷適用範圍。

---

## 23. Emergency

Emergency 必須能在離線狀態使用。

Emergency content 不應依賴：

- API
- 登入
- 遠端 Feature Flag

Emergency Screen UI 原則：

- 低認知負擔
- 大點擊區域
- 少層級
- 立即操作優先

---

## 24. Error Handling

任何單一 Guide JSON 錯誤不得導致 App Crash。

期望流程：

```text
Parse Guide
 ↓
Valid?
 ├─ Yes → Import
 └─ No  → Log + Skip
```

Debug log 應包含：

- file
- guide id（如果已解析）
- error type
- error message

不得因一個壞 Guide 阻止其他 Guide 載入。

---

## 25. Logging

MVP Debug build 可以有詳細 log。

Release build 不得輸出：

- 敏感資料
- 使用者輸入全文
- 本地路徑中的不必要資訊

---

## 26. Testing Strategy

至少包含：

### Unit Test

- JSON parsing
- Invalid JSON skip
- Repository search
- Alias search
- Favorite persistence
- Checklist persistence
- History update
- Region preference

### UI / Instrumented Test

- Bottom Navigation
- Home → Guide
- Search → Guide
- Favorite toggle
- Checklist toggle
- Emergency open

### Manual QA

- Airplane mode
- App restart
- Large font
- Dark theme compatibility
- Empty favorites
- Empty history
- Search no result
- Invalid content asset

---

## 27. Accessibility

Compose 元件至少考慮：

- TalkBack content description
- Touch target
- Dynamic font size
- Contrast
- Scalable layout

不得以固定高度導致大字體裁切主要文字。

---

## 28. Privacy Architecture

MVP 不收集：

- 真實姓名
- 身份證
- 手機號
- 精確 GPS
- 銀行資料
- 學校帳號

MVP 不需要任何後端傳輸。

---

## 29. Analytics

第一版可以完全不接入 Analytics。

如果未來接入，僅允許匿名事件，例如：

```text
guide_open
search_query_submitted
guide_favorite
category_open
```

不得記錄敏感搜尋內容。

---

## 30. Dependency Rule

每新增一個第三方 dependency 前必須回答：

1. Android 官方方案是否已足夠？
2. 是否真的需要？
3. 是否會增加隱私風險？
4. 是否會增加 APK 體積或維護成本？
5. 是否有清楚 license？

不得為簡單功能引入大型 framework。

---

## 31. No Backend Rule

MVP 不建立：

- REST API
- Firebase Auth
- Supabase
- 自建 Server
- WebSocket
- Remote Database

如開發中認為需要 Backend，必須停止該部分並提出原因，由專案負責人決定。

---

## 32. No AI Rule

MVP 不加入自由聊天 AI。

不得：

- 自動生成法律結論
- 自動生成醫療建議
- 自動生成政策流程

未來 AI 只能基於已核驗內容 Retrieval 後回答。

---

## 33. Build & Verification Rule

每一個模組遵循：

> 實作一項 → 編譯 → 測試 → 驗證 → 再進下一項

不得一次建立大量未驗證 Screen 或 Data Layer。

每階段至少執行：

```text
build
relevant unit tests
relevant UI/manual validation
```

---

## 34. Architecture Definition of Done

Architecture 基礎完成的標準：

- App 可啟動
- Navigation 可用
- Hilt 可注入
- Room 可建立
- DataStore 可讀寫
- JSON 可解析
- Guide 可從 bundled content 匯入
- 錯誤 JSON 不造成 crash
- 無網路仍可讀 Guide
