# 14 mod·资源·启动器

> 覆盖：`Lwjgl3Launcher`/`Lwjgl3Application` 配置、`FightCore` 启动、`AssetsLoader`/`FightAssetManager`/`FightFileHandleResolver`、`Resource`/`NinePatchResource`、`ShaderScheduler`/`ShaderCompatibilityHelper`/`ShaderProgramFactory`、`mod/` 全部、框架层与游戏层的边界。
> 一句话职责：**把“资源从哪来、代码从哪加载、窗口怎么开”这三件事收在入口附近，其余模块只认 `Fight.*Path` 这类路径常量**。

## 1. 启动链

```
Lwjgl3Launcher.main
 └─ new Lwjgl3Application(new FightCore(), config)
     ├─ title / 1280×720 / vsync / 前景 FPS 限制
     └─ 工作目录 = assets/（lwjgl3/build.gradle 把 assets 拷到运行目录）
FightCore.create()
 ├─ coreInit()
 │   ├─ Fonts.init()                ← 字体
 │   ├─ FI18N.init()                ← 语言包
 │   ├─ EventTypes.init()           ← 事件类型字符串
 │   ├─ RegistrantGroup.init()      ← 注册器（决定注册顺序）
 │   ├─ ShaderScheduler.init()      ← 着色器调度
 │   ├─ RenderPipe.init()
 │   └─ GameSystemManager.init()
 ├─ 注册 GUI 渲染处理器（优先级 10000，最先注册、最后绘制）
 ├─ new StartMenuScreen / WorldsMenuScreen / MainGameScreen
 ├─ GameSystemManager.addSystem(InputHandleSystem / GUISystem / SpatialAudioSystem)
 ├─ GameSystemManager.initAllSystems()
 └─ setScreen(startMenuScreen)
```

**启动顺序的硬约束**：`Blocks.init()` 必须在 `AttachmentPlacements.init()` 之前（规则引用方块）；`Items.init()` 在 `Blocks` 之后（物品引用方块）；`MainGameScreen.show()` 里 17 个 `init()` 的顺序就是依赖顺序。

## 2. 应用循环（`GameCore`）

```
render()
 ├─ exchangeScreen()                  ← 切换 screen 在帧边界
 ├─ deltaTime = Gdx.graphics.getDeltaTime()   ← 未做上限钳制
 ├─ Perf.begin()
 ├─ GameSystemManager.update(delta)   ← 全局系统（输入/UI/音频）
 ├─ updateScreen(delta)               ← 当前 screen.render
 └─ RenderPipe.handleGameRender()
     └─ RenderProcessorManager.handleRender  ← 按优先级 100/200/300/400/10000
```

## 3. 资源

| 类型 | 位置 | 说明 |
|---|---|---|
| `AssetsLoader` | `.../resource/AssetsLoader.java` | 统一加载入口（纹理/音频/字体） |
| `FightAssetManager` | `.../resource/FightAssetManager.java` | libGDX `AssetManager` 子类，管理生命周期 |
| `FightFileHandleResolver` | `.../resource/FightFileHandleResolver.java` | 把逻辑路径映射到 `Internal` 文件 |
| `Resource`/`NinePatchResource` | `.../resource/` | 资源描述（路径 + 类型） |

**路径常量集中在 `Fight`**：`Fight.TexturePath`、`Fight.ItemTexturePath`、`Fight.BlockTexturePath`、`Fight.AttachmentTexturePath`、`Fight.EntityTexturePath`、`Fight.FontPath`、`Fight.LangPath`。新增资源只改常量，不散落字符串。

**着色器**：`ShaderScheduler` 负责排队编译（避免首帧卡顿）；`ShaderCompatibilityHelper` 处理 GL 版本差异；`ShaderProgramFactory` 产出 `ShaderProgram`。`GL32CMacIssueHandler` 处理 macOS GL 3.2 core 的兼容问题。

## 4. mod（当前为骨架）

```
mod/
 ├─ Mod               ← 接口：id / version / dependencies / onLoad
 ├─ ModContainer      ← 一个 mod 的元数据 + 实例
 ├─ ModDependency     ← 依赖声明与版本比较（SemanticVersion）
 ├─ ModLoader         ← 扫描 mods 目录 → 建容器 → 解析依赖 → 按拓扑序 onLoad
 ├─ ModNode           ← 依赖图节点
 ├─ ModLibManager     ← 第三方 jar 的类加载
 ├─ ModLibLoader      ← 从 mod 包里抽 lib
 ├─ EngineFactory     ← 给 mod 提供创建引擎对象的入口
 ├─ ModEventCaller    ← mod 生命周期事件
 └─ api/
     ├─ ModRegistrant   ← 注册方块/物品/实体
     ├─ ModAudioRegister
     ├─ ModFileLoader
     ├─ ModLibLoader
     └─ ModSupplier
```

**现状**：`docs/架构说明.md` 第十一章记为“骨架”，即接口与加载器都在，但没有真实 mod 分发；`F-L1` 指出 9 个框架层文件反向 import 游戏层（23 处），mod 若依赖框架层会顺带把游戏层拖进来。

**依赖方向**：
```
bedrockcore（框架）  ←—— 不得 import ——  ttk.muxiuesd（游戏）
ttk.muxiuesd  ──→ bedrockcore   ✅
mod/api       ──→ 两者都可用（由 EngineFactory 收口）
```

## 5. 与其他模块的联动

| 方向 | 对象 | 交接内容 |
|---|---|---|
| 上游 | — | 本模块是根，不依赖其他模块 |
| 下游 | §01 框架 | `FightCore` 实现 `GameCore`；`Lwjgl3Launcher` 只是壳 |
| 下游 | §02 注册 | `coreInit` 里 `RegistrantGroup.init()` 触发全部注册器 |
| 下游 | §09 渲染 | `RenderPipe.init()` + 处理器注册顺序决定绘制顺序 |
| 下游 | §10 UI | `GUISystem` 是全局系统；`FightCore` 设当前 screen |
| 下游 | §12 存档 | `Fight` 提供 `save/<世界名>/world/...` 路径 |

## 6. 刻意设计与代价

- **资源路径常量集中在 `Fight`**：改名只改一处。代价是常量类很大、且带静态初始化（`Fight` 里 `ID(String)` 用 `synchronized` 复用 `StringBuilder`）。
- **`coreInit` 里注册器顺序显式**：可读、可查依赖。代价是加一个注册器要记得插在正确位置。
- **mod 走 api 收口**：mod 不直接碰内部类。代价是 api 不全时 mod 做不了复杂事（当前即如此）。
- **框架层/游戏层分离**：可复用。代价是 `F-L1` 的 9 个文件已经破坏了它。

## 7. 已知问题（详见评审文档）

| 编号 | 问题 | 位置 |
|---|---|---|
| `F-L1` | 9 个框架层文件反向 import 游戏层（23 处）→ 分层成环 | `bedrockcore/` |
| `H12` | `FightCore.dispose` 先 dispose 音频引擎再 dispose 世界音效 → use-after-free | `FightCore.dispose` |
| `B5` | `SystemManager.dispose` 不摘渲染任务 | `SystemManager.java:101-103` |
| `F-C?` | `RenderPipe.init()` 与处理器注册的相对顺序无断言 | `FightCore.create` |
| — | `mod` 无实际加载路径：`ModLoader.getInstance()` 全项目**零调用**，`EngineFactory` 是空壳（见 §11 章） | `mod/` |

## 8. 改动指引

- 新增资源：在 `Fight` 加路径常量 → `AssetsLoader` 加载 → 用时取 `FightAssetManager`。
- 新增注册器：实现 `Registrant` → 在 `RegistrantGroup.init()` 里按依赖顺序插入。
- 新增屏幕：继承 `GameScreen`（或 `UIScreen`），在 `FightCore.create()` 里建、用 `setScreen` 切。
- 碰框架层前：先确认没有 import `ttk.muxiuesd`（`F-L1` 的红线）。
