# 初级成年人入门手册

面向中国大陆 16–25 岁学生与刚毕业年轻人的 Android 应用与离线网页版。它不是百科全书，而是把第一次独立处理事情时的下一步讲清楚。

## 当前进度

Phase 1 已完成：

- Kotlin + Jetpack Compose 原生工程
- 包名 `com.chujichengnianren.guide`
- 冷灰绿纸面、深墨色正文、蓝色行动色、橙色紧急色的品牌主题
- 带侧边索引标签的卡片组件与自适应深色主题
- 固定四项底部导航：首页、搜索、收藏、我的
- 分类、手册详情、紧急情况、地区选择、阶段选择等非底部路由
- Hilt、Room、DataStore、Navigation Compose、kotlinx.serialization 的工程接入
- 不声明网络权限；首版继续采用离线优先结构

Phase 2 数据基础已完成的部分：

- 严格匹配 `DATA_SCHEMA.md` 的 Guide、Region、Step、ChecklistItem、Source 模型
- 未定结构的 `warnings` 与 `sections` 只接受空数组
- JSON parser、validator、资产加载器和按 Guide 版本更新的 importer
- Guide、Step、Checklist definition、Source、Favorite、Checklist state、Reading history 七张 Room 表
- Guide Repository 与本地状态 Repository
- 启动时自动导入 `assets/content/guides/`，坏内容记录后跳过，不导致应用崩溃
- 20 项 JVM 测试，其中 5 项使用 Robolectric 内存 Room 数据库，1 项启动 Compose 并切换四个底栏

DataStore 的正式 key 名称与 user-stage identifier 仍按规范等待确认，因此尚未写死。首发 25 篇手册正文已经完成，覆盖证件、金融、医疗、住房、学业、就业与安全场景；每篇均附中国大陆官方来源与 `verifiedAt`，开发示例不进入内容包。

网页版已完成：

- 响应式 PWA：桌面为手册目录与彩色索引页签，手机为固定四项底部导航
- 首页场景、八大分类、搜索、文章详情、紧急入口、收藏、历史、清单、地区与“我的”
- 搜索严格限定为 `title`、`summary`、`keywords`、`aliases`，支持中文近似表达
- 地区、收藏、历史与清单状态保存在浏览器本地；无登录、后端、广告或第三方追踪
- Service Worker 缓存界面与随包内容，首次加载后可断网阅读
- 构建时从 Android `assets/content/guides/` 机械同步内容，不维护第二份手写正文
- 19 项 Node 测试与内容/远程依赖检查；已完成桌面、390 px 手机视口和断开本地服务器后的浏览器验证

网页版同样不会保存尚未确认 identifier 的用户阶段。网页与 Android 共用同一套 25 篇正式内容，目前包含 125 个操作步骤、117 个材料清单项和 44 条官方来源记录。

## 构建环境

- Android Gradle Plugin 9.2.0
- Gradle 9.4.1
- JDK 21（字节码目标 Java 17）
- compileSdk 36 / targetSdk 36 / minSdk 26
- Compose BOM 2026.06.00
- Dagger Hilt 2.59.2
- KSP 2.3.10

Compose 与 AndroidX Hilt 没有升到要求 API 37 的版本，因为当前机器只安装了 API 36/36.1 编译平台。升级 SDK 后应单独做 API 37 兼容回归，再一起更新依赖与 `targetSdk`。

## 本机构建

Android Studio 可直接打开项目。命令行构建时使用 Android Studio 自带 JBR：

```powershell
$env:JAVA_HOME = 'D:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Debug APK 输出到：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 网页版开发与构建

网页版不依赖第三方运行时包，要求 Node.js 22 或更新版本：

```powershell
cd web
npm test
npm run build
npm run dev
```

本地预览默认地址：

```text
http://127.0.0.1:4173/#/home
```

可直接部署的静态文件生成到：

```text
web/dist/
```

每次 `build` 都会先同步 Android bundled JSON 并校验 Schema、一致性、PWA 必需文件和远程运行时资源。若 4173 端口被占用，可临时指定：

```powershell
$env:PORT = '4174'
npm run dev
```

产品、架构、数据结构与任务拆分详见 [`proposal/`](proposal/)。
