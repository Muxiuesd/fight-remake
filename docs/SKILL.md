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
