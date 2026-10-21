# 模块逻辑链索引

本目录把项目拆成 **14 个模块**，每个模块一份文档，写清三件事：

1. **它自己怎么运转**（逻辑链条，带 `文件:行`）；
2. **它和谁联动**（上游谁调它、下游它调谁、共享哪些数据）；
3. **哪些是真缺陷、哪些是刻意设计**（对照 `docs/问题清单.md` 与 `docs/项目逻辑链条与模块评审.md` 的 `D*`/`N*`/`B*` 编号）。

## 与另外两份文档的分工

| 文档 | 回答的问题 |
|---|---|
| `docs/架构说明.md` | 项目**是什么**：分层、启动、时间轴、注册约定（偏全局、偏结论） |
| `docs/项目逻辑链条与模块评审.md` | 每个模块的**优点与不足**，以及和问题清单的逐条对照 |
| `docs/模块逻辑/*.md`（本目录） | 每个模块**怎么一步步跑起来**、和别的模块**在哪一行交接** |

## 模块表

| # | 文件 | 覆盖范围 | 主要上游 | 主要下游 |
|---|---|---|---|---|
| 01 | [01-启动与框架层.md](01-启动与框架层.md) | `Lwjgl3Launcher`、`FightCore`、`GameCore`、屏幕切换、每帧三段 | 操作系统 / libGDX | 全部模块 |
| 02 | [02-注册与数据模型.md](02-注册与数据模型.md) | `Registries`、`Identifier`、`Blocks`/`Items` 注册、Property、`ICatData` | 01 启动 | 05/06/08/12 |
| 03 | [03-系统管理器与时间轴.md](03-系统管理器与时间轴.md) | `SystemManager`、`GameSystemManager`、`TimeSystem`、`TaskTimer` | 01/04 | 全部世界系统 |
| 04 | [04-世界与生命周期.md](04-世界与生命周期.md) | `World`、`MainWorld`、`WorldInfoTypes`、进入/退出世界 | 01/03 | 05/07/12 |
| 05 | [05-区块系统.md](05-区块系统.md) | `ChunkSystem`、`Chunk`、`ChunkLoadTask`/`UnloadTask`、生成器 | 03/04/07 | 06/07/12/13 |
| 06 | [06-方块墙体与附着物.md](06-方块墙体与附着物.md) | `Block`、`Wall`、`Attachment`/`Botany`、`BlockEntity`、放置与破坏 | 05/02/08 | 05/09/11/12 |
| 07 | [07-实体与玩家.md](07-实体与玩家.md) | `Entity`、`LivingEntity`、`Player`、`EntitySystem`、碰撞、状态效果 | 03/05/06 | 08/09/11/13 |
| 08 | [08-物品与交互.md](08-物品与交互.md) | `Item`、`ItemStack`、五类 behaviour、消耗品/装备/武器、`use`/`useOn` | 02/07/06 | 06/07/11 |
| 09 | [09-渲染管线.md](09-渲染管线.md) | `RenderPipe`、`RenderProcessorManager`、相机、`ChunkRenderSystem`、实体/粒子渲染 | 01/05/07/10 | 屏幕 |
| 10 | [10-UI与文本.md](10-UI与文本.md) | `GUISystem`、`UIScreen`/`UIPanel`、槽位、Tooltip、`Text`/`FI18N`/字体 | 01/07/08 | 07（鼠标门控） |
| 11 | [11-事件音频与粒子.md](11-事件音频与粒子.md) | `EventBus`、`EventHandler`、`SoundSystem`、`ParticleSystem`、光照入口 | 03/05/06/07 | 09 |
| 12 | [12-存档与序列化.md](12-存档与序列化.md) | `Codec`、`CodecChunk`、实体/玩家编解码、`RawObjectJsonConverter`、原子写盘 | 02/04/05/06/07 | 05/04 |
| 13 | [13-合成烧炼生成与寻路.md](13-合成烧炼生成与寻路.md) | 配方注册表、工作台/熔炉、刷怪、`FlowField`/`PathfindingSystem` | 03/05/07/08 | 07/12 |
| 14 | [14-mod资源与启动器.md](14-mod资源与启动器.md) | `ModLoader`/`ModLibManager`、`FightAssetManager`、`Lwjgl3Launcher` | 01/02 | 02/09 |

## 推荐阅读顺序

**想快速理解"一帧发生了什么"**：01 → 03 → 04 → 05 → 07 → 09。
**想改玩法**：06 → 08 → 07（方块/物品/实体三件套，全部从 `ChunkSystem` 或 `LivingEntity` 进出）。
**想改存档**：02 → 12 → 05（注册表决定能解出什么，`CodecChunk` 决定怎么解）。
**想改界面**：10 → 01（UI 在 `GUISystem.update` 里先于世界更新，会门控世界输入）。

## 记号约定

- `文件.java:行` —— 顶点到具体行，可直接 grep；行号以当前 `develop` 分支 `a3c6e91` 为准。
- `【清单 H23】` —— `docs/问题清单.md` 已有编号；`D*` 见评审文档 §1.7；`N*` 见 §4.3；`B*` 见 §3。
- 「**刻意设计**」= 注释里写明了意图且实现一致；「**代价**」= 这种设计必然带来的副作用，不是 bug。

## 维护约定

改代码时如果改动了模块之间的交接点（谁调谁、传什么、返回什么），**必须同时改对应模块文档的「联动」一节**，否则下一个人会按旧链条找错地方。
