---
name: fight-remake-dev
description: fight_remake（Java17+libGDX 1.14.2 的 2D 游戏）项目的开发经验与规范。当涉及该项目的编译构建、地图/群系生成（height/BiomeSampler/BiomeBand/噪声）、区块系统（ChunkSystem）、存档（WorldInfo/CodecChunk）、出生点/玩家位置、实体与生物的新增/注册、渲染器分层与 Context 池化、贴图路径与资源加载、UI/坐标/物品/事件/渲染约定、headless 数值验证、问题清单维护时使用。
---

# fight_remake 开发经验

## 一、项目概况

- **技术栈**：Java 17 + libGDX 1.14.2，Gradle（`projectVersion=0.0.25-beta`）。核心代码在 `core/src/main/java/`。
- **包结构职责（强约束）**：
  - `game.muxiuesd.bedrockcore`：**底层可复用框架**（与具体游戏无关）。
  - `ttk.muxiuesd`：**游戏强相关逻辑**。
  - **禁止反向依赖**：框架层不得依赖游戏层；框架需要的能力不要直接引 ttk 的工具类。
- **这是 2D 世界**：区块 16×16，世界坐标是 x/y。`Chunk.heights` 是"地形类型标量"（决定生成什么方块），**不是 Z 坐标，不参与渲染/碰撞/物理**。
  - 高度的唯一生产者 = `MainWorldChunkGenerator`；唯一持久化 = `CodecChunk`；唯一消费 = `Biome.decideBlock(height)`。
- 启动链：`FightCore.create` → 主菜单/世界列表/`MainGameScreen`（单例常驻，切档复用它）。`MainGameScreen.show()` 每次进入都 `new MainWorld`。退出世界：`WorldInputHandleSystem` → `mainGameScreen.dispose()` → `world.dispose()`。

## 二、构建与测试

- 编译（**必须加 `--console=plain`**：不加时命令行输出被偶发 kill，原因未查明，别去试；offline 用本地缓存）：
  ```
  .\gradlew.bat :core:compileJava --offline --console=plain -q
  ```
- **Headless 数值测试**（验证算法/分布，不启动游戏）：
  - 测试类写到项目外（如 `%TEMP%\opencode\gdx_src`），编译用 `core\build\classes\java\main`。
  - 需要 gdx 时从 `~/.gradle/caches/modules-2/files-2.1/com.badlogicgames.gdx/` 找 `gdx`、`gdx-backend-headless`、`gdx-jnigen-loader`、`gdx-platform-*-natives-desktop` 组 classpath。
  - **陷阱**：涉及 `Blocks`/`Biomes` 静态初始化的代码会加载贴图资源，headless 下崩溃。验证群系逻辑时**复刻公式**（不实例化 `Blocks`/`Biomes`）来统计分布/尺寸。
  - 测试完删除临时测试文件。

## 三、核心架构约定

- **注册体系**（`Registries`/`RegistryKeys`）：
  - `Identifier` 值语义（equals/hashCode 基于 id 字符串）。
  - 注册后不可改 id；同注册表 id 不可重复；方块与其方块物品共享 identifier。
  - 唯一实例元素直接注册实例（Item/普通Block）；多实例注册工厂（`EntityProvider`/`BlockEntityProvider`）或自带工厂方法。
  - 实体以 `EntityProvider` 为注册与 identifier 单一数据源。
  - 注册阶段务必把 id 写回实例（如 `Biomes.register` 必须 `biome.setId(identifier)`，否则 `getId()` 为 null 致 NPE——本项目踩过）。
- **系统分类**：`GameSystem`（整个程序生命周期，单例）/ `WorldSystem`（仅游戏游玩阶段，每世界一个）。
- **系统初始化顺序（关键，易踩坑）**：`World.addSystem` → `SystemManager.addSystem` 往 `LinkedHashMap` 里存（`SystemManager.java:18` 注释明写"使用LinkedHashMap确保初始化的顺序为添加系统时的顺序"），`initAllSystems()`（`SystemManager.java:75-79`）遍历 `this.systems.values()` 逐个 `initialize()`，`MainGameScreen.java:69` 每次进世界调用。**注册顺序就是初始化顺序，改顺序 = 改初始化时序**。
  - 所有系统都只在这两处注册：`world/MainWorld.java:21-40`（世界系统）与 `FightCore.java:67-72`（全局系统 `InputHandleSystem`/`GUISystem`/`SpatialAudioSystem`）。
  - 当前世界系统的真实插入顺序：`TimeSystem` → `EventSystem` → `PlayerSystem` → `ChunkSystem` → `PathfindingSystem` → `EntitySystem` → `UndergroundEntityRenderSystem` → `GroundEntityRenderSystem` → `ParticleSystem` → `CameraFollowSystem` → `GroundEntityCollisionSystem` → `BulletCollisionSystem` → `WorldInputHandleSystem` → `SoundSystem` → `MonsterGenerationSystem` → `UndergroundCreatureGenSystem` → `LightSystem` → `TestSystem`。
  - 三条会被咬的时序：①`EventSystem` 最早 → 世界事件（进世界/玩家生成/实体死亡等）的订阅必须先挂上，后注册的系统才收得到；②`PlayerSystem` 在 `ChunkSystem` 前 → 玩家数据/位置先就绪，区块再按玩家位置加载；③`EntitySystem` 在 `ChunkSystem` 之后 → 实体依赖区块已加载。
  - **新增世界系统的插入位置**：渲染类系统排在两个实体渲染系统旁边；只在游玩期跑、且要用到实体/区块的系统，必须排在它依赖的系统**后面**（拿不准就按"被依赖者在前"排，不要插到 `ChunkSystem`/`EntitySystem` 之前）。
- **信息/设置**：`Fight` 里 `Info` 常量（`WORLD_SEED`/`SPAWN_X` 等），简单配置用 `Fight.XXX.getValue()`。
- **UI 文案**：`Text.ofText(Fight.ID("xxx"))` + `assets/lang/zh_cn.json` 的 `text.fight:xxx` 键；`Text` 支持 `{0}` 占位符 + `set(index,value)`。

## 四、地图/群系生成（本项目重点，多轮迭代）

### 核心数据流
```
纯噪声 → 逐格高度 height ∈ [0,256]
  → 区块级海陆：3×3 平滑区块中心高度 vs SEA_LEVEL
  → 逐格群系：lookupLandBiome(逐格 temp, 逐格 humid, height)   // 方式A：逐格采样 → 边界沿温湿等值线自然过渡
  → 方块：biome.decideBlock(height)（高度断点分带表）
  → 存档 CodecChunk 保存 height + biome id
```

### 关键常量（`Chunk.java` / `ChunkSystem.java`）
- `LowestHeight=0`、`HighestHeight=256`、`SEA_LEVEL=150`、`BEACH_MAX=156`、`GRASS_TOP=200`、`STONE_TOP=235`、`SNOWLINE=236`。
- `ChunkSystem.Slope=1000`：世界坐标→噪声坐标的分频；**Slope 越大，地形/大陆越大越平缓**（100→大陆仅几区块的碎片；1000→几十区块）。

### 避免"群系区块硬切"的核心经验
1. **逐格判定群系**（不要用"区块中心一次采样定整块"）：逐格 `sampleTemp/sampleHumidity/sampleHeight` → `lookupLandBiome` → `decideBlock`。温湿是连续噪声场 → 群系沿等值线自然渐变。实测相邻格群系跳变率仅 0.35%。
2. **温度与湿度必须解耦**：若同频同偏移会强正相关（曾 r=0.74）→ 无"高温干旱"气候 → **沙漠不出现**。解法：湿度大相位偏移（`HUMID_OFFSET=3000`）→ r≈0。**改温湿参数后必测相关系数与各群系占比**。
3. **频率不能过高**：高频分量（如 0.02，周期 50 格）把群系切成 2 区块碎片。低频（0.0006，周期约 100 区块）让群系成片大块。
4. **群系竞争策略**：特殊群系（雪原/沙漠/森林）用 `Biome.matchDegree(temp,humid)`（按配置的温/湿 range 算"区间饱和匹配度"）竞争；湿地/山地用高度绝对门槛；**平原只兜底、不参与 max 竞争**，否则宽范围群系吞掉所有特殊群系。
5. **统一高程带 + 群系覆盖（`BiomeBand`）**：断点 `BiomeBand.of(startHeight, block)`，语义"高度≥start 用此块，延续到下一断点"，天然无缝无重叠。`Biome.DEFAULT_BANDS` 为默认模板（沙→草→石→雪），特殊群系显式 `bands(...)`。
   - `decideBlock`：从高到低找第一个 `height≥start`，未命中回落 `surfaceBlock`。
   - 平原 `(150沙,157草,228石)` → 草 90.6%、石 0.4%；山地 `(150沙,157石,244雪)` → 石 85.1%、雪 14.9%。
6. **群系归属 vs 方块分离**：区块的 `chunk.setBiome(...)`（供存档/信息面板/canSpawn 的标签）可继续用区块级平滑高度判定；**实际方块走逐格**。

### 噪声经验（`WorldMapNoise`）
- 输出 [-1,1]，均值≈0；`getNorNoise(x,y,scl)` 内部乘 `scl`，传参**只乘一次频率**（曾双重乘导致地形几乎恒定）。
- `sampleHeight` 用 `noise(wx/Slope, wy/Slope)`。
- `generateGradient` 的 hash 用 int 乘法，大坐标可能溢出（低风险，必要时改 long）。
- 高度/温度/湿度分别用**不同 offset** 避免相关性。

### 其它
- 河流/湖泊：噪声带/斑块把高度压到海平面下。湿地：`wetlandStrength` + `isWetlandPondCell` 撒浅水塘。
- 出生点查找 `SpawnPointFinder`：螺旋 `initChunk` 隔离生成**不 addChunk**（避免探查区块写盘），检查 `hasLandCell`（非水格）即可（不依赖 `canSpawn`，海岛也能出生）。

## 五、区块系统经验（`ChunkSystem`，易踩坑）

- `getChunk` **只查 `activeChunks`**（不查 `_loadChunks`）；`addChunk` **无去重**（直接 `_loadChunks.add`）。
- `initChunk` 只生成不 add；`loadChunkBlocking` 会 `addChunk`。
- **重复添加陷阱**：同一区块被"两处加载"会产生两个实例。原则：**玩家初始区块统一由预加载 `update(-1.2f)` 加载一次**，其他流程（进世界设位置）不要重复加载。
- 加载/卸载走线程池（`ChunkLoadTask`/`ChunkUnloadTask`）；卸载有"任务已提交则跳过"防并发写同一文件。
- 玩家位置语义：
  - **初始出生点**（`SPAWN_X/Y`）：首次进入/死亡复活用。
  - **最后退出位置**：随玩家数据（`Player.CODEC` 含 x/y）保存；重新进入已游玩存档时保持。
  - `PlayerSystem.playerLoaded` 区分首进入（放出生点）/读档（保持最后位置）。

## 六、存档系统经验

- **`WorldInfoTypes.INT/LONG/FLOAT/STRING` 是静态全局单例**，且被注册表注册；每个 `World` 的 `WorldInfo.information` 只**引用**它们。`World.readWorldInfo()` 解码只 `put` 不 `clear` → 切档时若新档缺键，旧档残留会串档。**修复：读档前 `clear()` 这四个 map。**
- `WorldInfoTypes` 里的 key（`Fight.SPAWN_X/Y/WORLD_SEED/game_time` 等）随 `worldInfo.json` 读写。
- `Biome` 是注册表单例，序列化只存 id，反序列化用 `Biome.byId(id)` 反查。
- 区块存档 `CodecChunk` 存 blocks/walls/botany/heights/canSpawn/biome；高度范围校验用 `Chunk.LowestHeight/HighestHeight`。
- Codec 惯用法：`RawObject.ofString/ofMap/ofList`；解码子值时 `Codec.STRING.decode(Codec.wrap(value))`；`DataResult` 的 `result()`/`error()`；部分失败时保留已解码数据。

## 七、坐标系与 libgdx 绘制（易踩坑）

- `GUICamera`：`setToOrtho(false)` → **y 轴向上**，原点屏幕中心，视口基准 512×512。鼠标 GUI 坐标用 `Util.getMouseUIPosition()`。
- `PlayerCamera`：y 轴向上，原点世界中心。**渲染手/物品角度必须用世界坐标**（`getMouseWorldPosition()` + 实体中心 `getCenterPos()`），不要混用窗口像素坐标。
- 组件 `getX()/getY()` 是相对父面板；`getAbsX()/getAbsY()` 是绝对坐标。
- `SpriteBatch.draw(region,x,y,w,h)` 的 `y` 是**矩形底部**；`BitmapFont.draw` 的 `y` 是**文本基线**（字形向上）；行高用 `font.getLineHeight()`（**不要** `ascent+descent`，descent 为负）。
- 计算文本基线让其居中于框：基线 = 框顶 - padding - ascent。
- **`getDirection()` 的语义（别假设它等于速度）**：基类 `LivingEntity.getDirection()`（`world/entity/abs/LivingEntity.java:452-454`）就是 `return new Direction(getVelX(), getVelY());`，**每帧新建对象**，所以不要在一帧里反复调。大部分实体的朝向 = 速度朝向，**但 `Player` 覆写了**它 → `Util.getDirection(getCenterPos())`（`world/entity/player/Player.java:142-145`），即**鼠标方向**，与速度无关 → 任何"按朝向办事"的功能都必须接受"朝向的定义由实体自己管"。取朝向的调用点散布在近战挥砍方向、**远程子弹发射方向**（`world/item/weapon/RangedWeapon.java:51`）、手持物品旋转角、丢弃物品初速度等 **16 处以上**（用 `getDirection\s*\(` 全项目搜一遍就能拿到当前清单），**改动朝向语义前必须先搜全部调用点**。
- **水平镜像贴图只能靠换 UV**：`SpriteBatch` 的 9 参 `draw(TextureRegion,x,y,originX,originY,w,h,scaleX,scaleY,rotation)` 把 `u = region.u; u2 = region.u2;` **原样写进顶点，没有任何跟 scale/origin/rotation 相关的分支** → 调 `originX/scaleX` 只改四边形几何（例如 `originX=16, scaleX=-1` 只是把整体右移一个贴图宽度），**做不到镜像**。15 个 draw 重载里带 `flipX/flipY` 的 3 个首参都是 `Texture`，`TextureRegion` 的 5 个重载一个都没有，`Batch`/`SpriteBatch` 也没有 `setFlip`。可行做法：`new TextureRegion(region)` 复制一份 UV 后 `flip(true,false)`（拷贝构造复用同一张 `Texture`，不是第二张纹理；注意 `TextureRegion.flip` **返回 void**，不能链式 `new TextureRegion(r).flip(...)`）。本项目现成实现见 `interfaces/render/world/entity/EntityRenderer.java` 的 `StandardRenderer.getFlippedTextureRegion()` 与 `render/world/entity/LivingEntityRenderer.java` 的 `flipWhenFacing`。

## 八、UI / 物品 / 事件 / 渲染约定

- **UIButton 左键采用方案 B（按下→松开触发）**：按下帧只记 `pendingClick`+坐标，每帧检测"松开且仍在按钮上"才触发；按住移出/不可见/禁用则取消。其他组件（快捷栏 SlotUI 等）保持方案 A（按下即发）。组件移除时取消 `pendingClick` 防残留。
- **玩家输入门控**：鼠标悬停 HUD UI 时不处理鼠标操作与世界交互（`!mouseOverUI()` 门控）；但键盘操作（移动/丢弃/数字键）只受 `curScreen==HUD` 门控。
- **物品**：
  - **鼠标与物品的分工（2026-09-27 定案，改物品交互前先看这条）**：
    - **左键 = 使用物品本身**，不认目标：`PlayerSystem.handleInput` → `LivingEntity.useItem(World)` → `ItemStack.use(World, LivingEntity)` → `IItemStackBehaviour.use` → `Item.use(物品堆叠, 世界, 使用者)`。空手左键仍是破坏（`WorldInputHandleSystem` 里被 `handItemStack.isVoid()` 挡着，两者不冲突）。
    - **右键 = 交互**，空手与手持都算：空手走 `BlockEntity.interact(...)` / 拆墙；**手持物品走 `LivingEntity.useOn(World, Vector2)` → `ItemStack.useOn` → `IItemStackBehaviour.useOn` → `Item.useOn(物品堆叠, 世界, 使用者, 目标坐标)`**，目标坐标就是鼠标指向的世界坐标。接线在 `system/WorldInputHandleSystem.java` 的右键分支（原来那个 `//TODO 玩家手持物品交互` 已被填上）。
    - **`Item.useOn` 的默认实现是 `return false` 且不播任何音效**（接口里的 `IItemStackBehaviour.useOn` 也是 `default return false`，所以现有物品一个都没被影响）。**失败要静默**：用不上的时候既不能响也不能扣数量 —— 消耗品的"扣一个"完全由返回值驱动（`ConsumptionItemStackBehaviour` 里 `use` 与 `useOn` 共用同一个 `consume()`）。
    - **不需要同时覆写两个**：一个物品要么"使用自己"（覆写 `use`，如食物/剑/鱼竿/刷怪蛋/方块物品），要么"对着东西用"（覆写 `useOn`，如骨粉、种子）。**只覆写 `useOn` 就自动获得了"左键不消耗也不响"**（继承 `Item.use` 的 false），不用再写空的 `use`。
    - **右键点方块实体时方块实体优先**：`WorldInputHandleSystem` 里 `blockEntity.interactWithItem(...)` 返回 `SUCCESS` 就直接 `return`（那件物品已经被方块实体收下了，如放进熔炉），只有返回 `FAILURE` 才轮到物品自己的 `useOn()`。空手分支同理直接 `return`。
  - 行为由 `getBehaviour()` 覆写返回注册的静态行为决定（`Item.Type` 已删除）。
  - 浅拷贝策略：仅 `ShallowCopyable` 的类调 `copy()`（现仅 `CatsHolder`），其余共享引用（规定语义，非 bug）。
  - 堆叠条件：同类 + `Property.equals`（值语义）；`ItemStack.copy(0)` 会被 `Math.max(amount,1)` 抬升为 1，空掉落用 `ItemStack.VOID`。
  - `useTimer` 初始化为已 ready（`new Timer<>(span,span)`）；`isUsing()` 用 `curSpan<maxSpan` 判断（**不要** `!isReady()`，它有归零副作用）。使用中的物品不可切换（`setHandIndex` 拦截，存档初始化直接赋字段绕过）。
  - 近战武器命中才扣耐久（空挥不扣）；挥手动画照常。
  - 战利品：`LootGroup`(互斥组)/`LootEntry`(权重+数量区间)/`LootGenerator`(抽取)；去重用 `Iterator.remove()`（for-each 内 remove 会 CME）。
- **事件**：`EventBus` 全局静态；世界事件由 `EventSystem` 初始化时订阅，`dispose()` 统一退订（防重进世界累积重复订阅/事件双触发）。`Event` 无 equals/hashCode，按引用匹配。
- **渲染管线**：世界渲染处理器随 `MainGameScreen.dispose()→unregisterWorldRenderProcessors()` 注销；**`GUIRenderProcessor` 全局常驻，退出世界必须 `setMainGameScreen(null)`**（否则主菜单对已 dispose 世界做光照后处理）。

## 九、开发工作流规范（用户强调，务必遵守）

1. **较大的改动先给方案**：先读相关代码 → 思考方案 → 详细列出，等用户确认（用户说"开始实施方案/开始重构"才动手；说"先不修改代码"就只分析）。
2. **数值/数学/算法类问题必须先验证再改**：写采样测试或数学推导确认，不能凭直觉改（本项目多次踩坑：温湿相关性、噪声频率、Voronoi 均值偏移、群系斑块尺寸）。
3. **用户未反馈异常的问题默认不是 bug**：找不到实际触发场景或数值证据就不改。
4. **修改后必须能解释"为什么旧行为是错的"**，解释不了就不改。
5. **修复后必审查**：边界情况、竞态条件、错误处理（失败路径）、副作用、调用方受影响情况。
6. **每修复一个问题，同步更新 `docs/问题清单.md`**：条目状态 + 头部"会话进展"追加（触发现象、根因、修复内容、影响面）。
7. **动手前先读相关文件当前状态**（代码可能与记忆不符），不要臆想代码内容。
8. **改动后清理未使用的 import**；类/字段/方法多写中文注释。
9. 用中文与用户交流；不主动 commit（除非明确要求）。
10. Git 提交信息用中文，格式如 `新增：xxx` / `修复：xxx` / `修改：xxx` / `更新：xxx` / `测试性更新：xxx`。

## 十、常见陷阱速查

| 陷阱 | 教训 |
|------|------|
| 群系被切成 2 区块碎片 | 温湿高频分量太高，降频 |
| 沙漠完全不出现 | 温湿强正相关（同 freq/offset），加大湿度 offset 解耦 |
| 平原吞掉特殊群系 | 平原不应参与 max 竞争，只兜底 |
| 群系区块边界硬切 | 用逐格判定（方式A），不要区块级整块定群系 |
| 玩家脚下区块被添加两次 | `getChunk` 只查 activeChunks + `addChunk` 无去重，统一由预加载加载一次 |
| 切档数据串档 | `WorldInfoTypes` 静态 map 读档前没 clear |
| 重新进入回到出生点 | spawn 流程无条件 setPosition 覆盖了读档位置 |
| `getId()` NPE | 注册时没把 id 写回 Biome 实例 |
| 高度/群系几乎恒定 | `getNorNoise` 双重乘频率 |
| 大陆只有几区块 | `Slope` 太小（特征是 `Slope/16` 区块） |
| headless 测试崩溃 | 触碰了 `Blocks`（贴图），改用复刻公式 |
| 实体背包容量传 0 | `Backpack.setSize` 对 `<1` **只打错误日志然后 return，字段不赋值**，之后 `getItemStack(0)` 抛 `IndexOutOfBoundsException`；基类要求的最小合法容量是 **1** |
| 贴图被移动到子目录后运行时崩 | `register(name)` 这类简写重载拼的是 `item/{name}.png`，贴图一移动就命中 `AssetsLoader.singleLoad` 的 `IllegalStateException("资源加载失败: ...")`；**编译期查不出来**，用显式路径 `Fight.ItemTexturePath("foods/xxx.png")` |
| 想靠 `originX/scaleX` 把贴图镜像 | UV 是按 `region.u/u2` 原样写进顶点的，**做不到**；镜像只能换 UV（复制一份 `TextureRegion` 后 `flip(true,false)`） |
| 运行时替换贴图没效果 | `AssetsLoader.load` 按 id 缓存，改文件后必须重启游戏 |
| 改 `LivingEntity.getDirection()` 语义 | 它的调用点散布在挥砍方向、**远程子弹发射方向**、手持物品旋转角等 16 处，且 `Player` 覆写为鼠标方向；动它之前先搜全部调用点 |
| 新加的附着物"右键毫无反应" | 90% 是忘了在 `registry/AttachmentPlacements` 里登记放置规则——**没登记过的附着物一处都放不了**；剩下 10% 是登记的是白名单但没包含脚下的方块 |
| 土豆点耕地"有时能种有时不能" / 点了没反应 | 已经修过了：附着物的支撑方块就是**它自己那一格坐标处的方块**，点耕地就种在耕地那一格。**再出现这种症状先查放置规则表和 `getBlock(wx, wy)`（同一格，不是 y 减 1）拿到的方块**，别去怀疑输入链路（右键 → `useOn` → `CropItem.useOn` 这条链是通的，失败是**静默**的：不扣数量、不播音效、也不报错） |
| **左键**拿种子/骨粉点没反应 | 设计如此：种植与催熟都是"物品对着方块交互"，走 `useOn()`（右键）。左键走的是 `use()`，`CropItem`/`BoneMealItem` 都没覆写它 → 继承 `Item.use` 的 `return false`，**既不消耗也不出声** |
| 右键用骨粉没反应 | 目标格不是 `Botany` 实例，或那株已经 `isFullyGrown()`（到上限）——两种都是 `BoneMealItem.useOn` 返回 false，静默不消耗。**先确认鼠标指着的就是作物那一格**（与种植同一套 `fastRound` 取整），再看它是不是早满了 |
| 骨粉左键白扣一个 | 说明有人给物品覆写了 `use()` 却没实现有效果的分支；`Item.use()` 默认**播音效且返回 true**，而消耗品减一完全由返回值驱动（见第八章"鼠标与物品的分工"） |
| 附着物放置规则登记早了 | 规则要引用 `Blocks.XXX`，必须在 `Blocks.init()` 之后；`Blocks.READY` 与 `AttachmentPlacements.init()` 里的检查就是防这个——顺序错了 `Blocks.POTATO` 还是 null，`registerWhitelist` 只会报错然后把**唯一那条规则静默丢掉** |
| 附着物判定用方块对象当键 | 植物每格一个实例，`==` 比较在放置时必然查不到；规则表和 `ChunkSystem.getBlockKey` 都用 `Identifier` |
| 读档出来的植物"不再生长"或"挖掉它下面的方块就崩`实例从未添加过`" | 同一个根因：每格独享附着物的运行时登记（`blockInstances` + `TimeSystem` tick）**不随存档回来**，必须由 `ChunkSystem.addChunk` 遍历 `attachments` 列重新 `addBlockInstance()`。改动区块加载/卸载代码时先确认这一对还在 |
| `Botany` 的 `createSelf()` 忘了复制配置 | 现在由 `Botany.createInstance()` 统一复制 `droppedItem`/`identifier`，子类只写 `createSelf()`；**别去覆写 `createInstance()`** |

## 十一、新增一个生物 / 实体

以新增生物 Chicken 为例（`world/entity/creature/Chicken.java`，30 行；生物在 `world/entity/creature/`，敌人看 `world/entity/enemy/`）：

1. **类与构造**：`extends CreatureEntity<T>`，构造 `super(world, entityType, 生命值, 最大生命值, 背包容量)` → `setSize(DEFAULT_SIZE)` → `fastAddBodyHitBox()` → `setSpeed(移速)`。
   - **背包容量必须 ≥ 1**（`world/entity/Backpack.java:48-51`：`if (size < 1)` 只 `Log.error` 然后 `return`，字段不赋值）。"不需要背包"也传 1 并写注释；基类要求的最小容器就是这个数。
   - 尺寸通常声明成 `public static final Vector2 DEFAULT_SIZE`（碰撞箱与渲染尺寸同源）。
2. **行为基本不用写**：`CreatureEntity` 构造里已 `setFleeWhenHurt(true)`；基类默认 `randomWalkPath` **就是陆地偏好**（取随机点要求该处无墙且不是水）→ 陆生生物连它都不用覆写，只有水生才覆写成趋水（参照 `world/entity/creature/PufferFish.java`，它另外覆写 `canSwim()` 与 `getRenderLayer()`）。状态机（休息/随机游走）由 `EntitySystem.add()` 末尾的 `lazyInitialize()` 自动启动，不用手动 `initialize()`。
3. **注册**：`registry/Entities.java` 加 `register("chicken", EntityProvider.Builder.<Chicken>create(Chicken::new).setDefaultType(EntityTypes.CREATURE).setRenderer(...).setCodec(LivingEntity.CODEC).build())`；`canBeSaved` 默认 true（进存档）。
   - **贴图路径拼接规则**（错了编译期查不出来，运行时才崩）：实体 = `Fight.EntityTexturePath(relativePath)` → `assets/texture/entity/{relativePath}`（渲染器构造收的是"去掉 `entity/` 前缀"的相对路径，如 `"chicken/chicken.png"` → `assets/texture/entity/chicken/chicken.png`）；物品 = `Fight.ItemTexturePath(relativePath)` → `assets/texture/item/{relativePath}`；`Items.register(name)` 简写拼 `item/{name}.png`；刷怪蛋用 `registerSpawnEgg("spawn_egg_chicken", Entities.CHICKEN)` → `item/spawn_eggs/{name}.png`。**贴图不在简写规则对应的目录里就必须写显式路径**。
   - 贴图运行时直读（`Gdx.files.internal`，不是资源清单）→ 不用登记，但**按 id 缓存，加了/换了贴图必须重启游戏**。`Resource.ofTextureRegion` 是延迟加载（构造只登记 id→路径，首次 `get()` 才加载）。
   - 音效同理：`Sounds` 里没有的类别沿用现成的（生物受击音效**不用自己挂**——`EventSystem.java:42` 把 `EventEnemyAttacked` 订阅到 `EventTypes.ENTITY_HURT`，`world/event/EventEnemyAttacked.java:18` 统一播 `Sounds.ENTITY_HURT_3`，音频文件是 `assets/audio/sound/entity/damage/hit_3.ogg`）。
4. **不用动的**：`screen/MainGameScreen.java` 的注册顺序（`EntityTypes.init()` :48 → `Entities.init()` :49 → `Items.init()` :51 → `ItemGroups.init()` :53 → `BlockDropLootTables.init()` :55 → `EntityDeathLootTables.init()` :56）已经全了；死亡掉落按实体 id 查表，改 `registry/EntityDeathLootTables.java` 注册 `LootGroup.of(...)` 即可，不需改 `MainGameScreen`。
   - **概率掉落**用权重表达：要"90% 掉 1 个"，就 `LootEntry.of(物品, 1, 1, 9f)` + `LootEntry.of(ItemStack.VOID, 1, 1, 1f)`；命中的 `VOID` 被 `world/loottable/entity/EntityDeathLootTable.java` 的 `if (itemStack.isVoid()) continue;` 跳过，**不会生成"空气物品实体"**。
5. **刷怪蛋 vs 自然刷新**：只要刷怪蛋就用 `Items.registerSpawnEgg(...)`；自然刷新是另一件事（要列进 `ItemGroups` 之外还得建生成工厂 + 挂到某个生成系统，见第三节的初始化顺序）。`world/item/consumption/SpawnEggItem.java` 的 `use()` 直接把实体放到鼠标世界坐标，**不做 canStand 校验**（既有行为，所有刷怪蛋共用）。

## 十二、渲染器分层与 Context 池化

- `EntityProvider.Builder.setRenderer(Supplier)` 是**注册期立即 `get()`**（`interfaces/world/entity/EntityProvider.java`），`renderer` 为 final 字段 → **每种实体只有一个渲染器实例**，允许在渲染器里缓存东西；但**不要让它每帧 new 对象**。
- 三层结构，按需覆写：
  - `interfaces/render/world/entity/EntityRenderer.java` 的 `StandardRenderer<T>`：一个 `bodyTextureRegion` + `draw(batch, entity, context)`（9 参 `batch.draw`）+ 空的 `drawShape`（实体是**贴图**还是**纯色形状**由子类决定）。
  - `render/world/entity/LivingEntityRenderer.java`：`super.draw` 之外画影子、受击变红、手持物品；`flipWhenFacing`（贴图水平镜像开关，默认 false）在这里。
  - 子类只做装配：`EnemyRenderer`（16 行）、`PlayerRenderer`（45 行，`super.draw` 后再画护盾）。
- **`Context` 是池化对象**，实体/物品/方块/WallRenderer/BlockEntityRenderer **各有独立的 `Context implements Pool.Poolable`（5 个，无公共基类）**。每帧流程是 `renderer.getContext(entity)` → `renderer.draw(batch, entity, context)` → `renderer.freeContext(context)`（`system/abs/EntityRenderSystem.java:31-46`，形状走 `:48-63`）。
  - **给 `Context` 加字段，必须同时加到 `reset()` 里复位**，否则上一帧/上一个实体（或另一张地图）的取值会泄漏到下一个实体，表现为随机闪一下。
- 渲染器与实体互不认识具体生物类：要区分外观就加**渲染器自己的开关/子类**，不要 `instanceof`；而"这个实体是否需要某能力"的开关放在渲染器实例上、由注册处配置。

## 十三、新增一个附着物 / 植物

附着物（`Attachment`）= "必须附着在下方方块之上、被破坏就破碎掉落物品"的方块；植物（`Botany`）是它的**有状态子类**。它们**单独占 `Chunk` 的一列**（`Chunk.attachments`），不进 `blocks` 数组 → **不参与碰撞与寻路，实体能穿过去**（想挡路得走 `Wall` 体系）。

以新增植物为例（现成参照 `world/block/instance/attachment/BotanyPotato.java`）：

1. **类**：植物 `extends Botany`，构造 `super(new Property())`；必须写四块——`tick(World, float)`（生长逻辑）、`public int maxGrowLevel()`（**抽象方法，不写编译不过**；取值 = 阶段贴图张数 − 1）、`protected Xxx createSelf()`（`return new Xxx()`）、其它什么都不用写。`tick` 里的上限守卫用 `isFullyGrown()`，别自己写 `getGrowLevel() >= 数字`（上限散写两处早晚不一致）。共享型装饰物直接 `extends Attachment`（不实现 `Tickable`，就不进 tick 列表）。
   - **不要覆写 `createInstance()`**：`Botany` 已经写好"出副本 + 从原型复制 `droppedItem`/`identifier`"；只覆写 `createSelf()`。
   - `Botany.createSelf()` 的返回类型是**协变的**（`protected abstract Botany createSelf()`），子类返回自己的类型即可，调用方不用强转。
2. **注册**：`registry/Blocks.java` 里
   - 植物：`registerBotany("potato", BotanyPotato::new, "potatoes_stage_0.png", ..., "potatoes_stage_3.png")` — 多个贴图按**生长等级从小到大**给，渲染器按 `getGrowLevel()` 选帧、超出就拿最后一帧（`AttachmentRenderer` 里 `Math.min(level, length - 1)`，所以上限写大了只会一直画最后一张、不会崩，但**上限的正确取值就是"贴图张数 − 1"**：土豆 4 张配 3、小麦 8 张配 7）。
   - 单贴图装饰物：`registerAttachment("torch", TorchAttachment::new, "torch.png")`。
   - 贴图根目录是 `assets/texture/blocks/attachment/crops/`（`Fight.AttachmentTexturePath("crops/xxx.png")` → `ATTACHMENT_TEXTURE_ROOT = BLOCK_TEXTURE_ROOT + "attachment/"`）；**方块**的贴图目录也是 `texture/blocks/`，贴图目录少写一层 `blocks/` 就会在启动时报资源加载失败。
3. **登记放置规则**（**必做，漏了就是"右键毫无反应"**）：在 `registry/AttachmentPlacements.java` 里登记，二选一，**一个附着物只能选一种方案**：
   - `registerWhitelist(Blocks.POTATO, Blocks.FARMLAND_DRY)` — 只能放在这些方块上；
   - `registerBlacklist(Blocks.SUNFLOWER, Blocks.WATER)` — 不能放在这些方块上，其他都能放。
   - 登记位置就在该类的 **`static { ... }` 静态块**里（与现有那行土豆规则放一起）；`init()` 只负责打日志，由 `MainGameScreen.show()` 在 `Blocks.init()` **之后**调用以触发类加载。**规则数据必须等方块注册完才能登记**（要引用 `Blocks.XXX`），所以别把它挪到 `Blocks` 之前——真挪错了会在启动时报"方块还没注册完就登记了附着物的放置规则"。
   - 判定：**空气方块一律不能附着**（无条件保底，空黑名单 = 除了空气哪都能放）；**没登记过的附着物一处都放不了**；方案记在内部的 `ATTACHMENT_RULE` 里，所以两张表**不存在"同时命中谁优先"**，换方案重复注册会报错并忽略；同方案可多次注册**累加**（重复方块自动去重）。
   - 规则**只在玩家放置时生效**，读档直接写进区块、不查表。
   - **"附着物的支撑方块"就是它自己那一格坐标处的方块**（**别理解成 y 减 1 那一格**）：`placeAttachment` 把鼠标坐标 `Util.fastRound` 成格坐标后，**附着物就放在这一格**，同时拿**这一格的方块**去查放置规则。所以白名单里写"耕地方块"就等于"鼠标点耕地就能种在耕地上"，附着物和耕地同处一格。放置成功后两者再无引用关系，格子里那个方块只是当初允许放置的依据；该格方块一变，这一格的附着物就该跟着没（`removeBlock`/`replaceBlock` 用**同一格坐标、不加 y 偏移**去 `destroyAttachment`）。
4. **掉落物**：`registry/Items.java` 加 `register("potato", Blocks.POTATO)`（命中原生作物重载，内部会 `crop.setDroppedItem(cropItem)`）。**共享型装饰物没有这个重载**，要自己写物品类并在注册后调 `attachment.setDroppedItem(...)`；不设就是被破坏后什么都不掉。
5. **玩家放置**：用 `CropItem`（`world/item/consumption/CropItem.java`）—— **种植是"物品对着方块交互"，所以它覆写的是 `useOn()`（右键）而不是 `use()`（左键）**：`useOn` 只做"`placeAttachment` 成功吗"，**不再硬编码任何方块**（能不能种由步骤 3 的规则表决定），失败不消耗物品、不播音效。**左键拿在手里点不会有任何反应、也不会消耗**（继承 `Item.use` 的 `false`）。左键破坏**天然优先于同格的方块**（`WorldInputHandleSystem` 的空手左键分支先查附着物）；同格方块被挖/被替换时，`ChunkSystem.removeBlock`/`replaceBlock` 会调 `destroyAttachment(round.x, round.y)`（**同一格坐标、不加 y 偏移**）把失去支撑的附着物一并破坏。**不用改 `MainGameScreen`。**
6. **存档**：走 `Attachment.CODEC`（每格状态由 `Botany.readCatData/writeCatData` 存进 `Cats`，键 `"growLevel"`）；JSON 键名是历史遗留的 `"botany"`，**别改**，改了旧存档读不出来。加新的每格状态就在 `readCatData`/`writeCatData` 里加一对键。
7. **区块挂载要重新登记（写新附着物时不用管，但改动 `ChunkSystem` 时务必记得）**：每格独享的附着物**只把数据存进区块**，它的"运行时登记"（`blockInstances` 那一条 + `TimeSystem` 的 tick）不会随存档回来。`ChunkSystem.addChunk` 遍历 `blocks` 列时**必须顺带遍历 `attachments` 列**并 `addBlockInstance()`，`removeChunk` 对称地只摘除、**不掉包**。漏掉加载侧的后果有两个而且都很隐蔽：读档出来的植物**不再生长**，以及**挖掉它下面的方块会崩**（`destroyAttachment → removeBlockInstance` 用 `id@instance.hashCode` 当键，查不到就报"实例从未添加过"，异常抛在世界更新链上会打断整帧）。

