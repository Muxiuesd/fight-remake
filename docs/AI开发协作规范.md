# AI 开发协作规范（guitar 项目专供 AI 协作阅读）

> 本文档汇总了项目开发者（人类）在协作中明确要求的**开发规范、设计模式与细节约定**，
> 供后续接入的其他 AI / 协作者快速理解本项目的地基约定，避免踩坑。
> 代码存在歧义或需改动时，优先参考本文档。

---

## 一、工作流规范（过程约定）

### 1. 修复后必审查
- 每修复一个问题、每改完一次代码，**必须自动审查一遍该修复**，直到彻底修好且不会引入新问题为止。
- 审查维度：**边界情况、竞态条件、错误处理（失败路径）、副作用（是否引入新问题）、相关调用方受影响情况**。

### 2. 问题清单同步
- 项目根目录 `docs/问题清单.md` 是问题的权威记录。
- **每修复一个问题，必须同步更新该清单**：条目状态标 `✅` + 在头部"会话进展"追加记录（触发现象、根因、修复内容、影响面）。
- 新会话开始时若被提醒"先读 docs/问题清单.md"，应当先读。

### 3. 防误判规则（重要教训）
- **数值/数学/算法类问题必须先验证再改**：写采样测试或数学推导确认范围/行为后再动手。
- **用户未反馈异常的问题默认不是 bug**：若功能表现正常，对"可疑代码"的分析必须能找到实际触发场景或数值证据，否则不做修改。
- **修改后必须能解释"为什么旧行为是错的"**：解释不了就不改。

### 4. 动手前先给方案
- 较大的改动（尤其是用户说"先不修改代码"时）：先阅读相关代码 → 思考方案 → 详细列出方案，等待用户确认后再实施。
- 用户说"开始实施方案"才动手。

---

## 二、代码与架构约定（基础）

### 1. 包结构职责
- `game.muxiuesd.bedrockcore`：**底层可复用框架**（与具体游戏无关）。
- `ttk.muxiuesd`：**游戏强相关逻辑**。
- **禁止反向依赖**：`bedrockcore`（框架）不应依赖 `ttk`（游戏）。若框架层需要某个能力（如鼠标坐标），不要直接引 ttk 的工具类。

### 2. 编码风格
- 类名/方法名/变量名清晰明了；**多写注释**（字段、方法、类都要有详细说明）。
- **字段尽量私有化** + getter/setter；setter 尽量写条件，防止非法值。
- 一个类里不要随意写内部类，除非强相关。
- 需要大量 new 的类用对象池（`Pool`/`FightPool`），仔细编写回收逻辑。
- **改动后留意清理未使用的 import**（移除代码后常残留，如仅用于被删逻辑的 import）。

### 3. 注册体系
- `Identifier` 值语义（equals/hashCode 基于 id 字符串）。
- 注册后不可改 identifier/id。
- 同类型注册 id 不可重复；方块与其方块物品共享 identifier。
- "唯一实例"元素直接注册实例（Item/Block）；"多实例"元素注册工厂（EntityProvider/BlockEntityProvider）或自带工厂方法。
- **实体以 `EntityProvider` 作为注册与 identifier 的单一数据源**；实体持有 provider，`Entity.setIdentifier` 在 provider 注入后抛异常（防改）。

### 4. 系统分类
- `GameSystem`：整个程序生命周期运行（单例，如 GUISystem/InputHandleSystem）。
- `WorldSystem`：只在游戏游玩阶段运行（每世界一个，如 PlayerSystem/ChunkSystem/EventSystem）。

### 5. 物品行为来源
- `Item.Type` 枚举已删除；**物品行为完全由 `getBehaviour()` 覆写返回注册的静态字段决定**（如 `ItemStackBehaviours.SWORD`/`RANGED_WEAPON`/`COMMON`），与类型解耦。

---

## 三、UI 组件设计模式

### 1. UIButton 点击：方案 B（按下→松开触发）
- `UIButton` 及子类（`FightUIButton`/`UITextButton`/`FightUITextButton`/`UIButtonListItem` 等）的**左键点击**采用**方案 B**：
  - **按下帧**：只记录"待确认"状态 `pendingClick=true` + 保存按下坐标，**不立即触发 clickEvent**。
  - **每帧 update 检测**：松开且鼠标仍在按钮上 → 触发 clickEvent + 播放音效；按住但鼠标移出 → 取消；不可见/禁用 → 取消。
  - 右键等其他按键仍由子类自行判断。
- **其他组件（如快捷栏 SlotUI）保持方案 A（按下即发）**，不受影响。
- **按下贴图状态机**：按住 = 按下贴图、悬停 = 悬停贴图、移开 = 普通贴图。
- **防御点**：组件移除时（`removeComponent`）要取消 `pendingClick`，防止状态残留/复用误触发。

### 2. UI 状态管理流程
- `UIScreen.update` 每帧：开头 `setMouseOver(false)` 清标记 → 遍历组件做**命中检测**（`rectangle(getX(), getY(), w, h).contains(鼠标GUI坐标)`）→ 命中则 `setMouseOver(true)` + `mouseOver(interactGrid)`。
- 鼠标坐标来源：`Util.getMouseUIPosition()`（GUICamera unproject，**GUI 相机 y 向上、原点屏幕中心**）。
- 组件坐标：`getX()/getY()` 是**相对父面板**的坐标；`getAbsX()/getAbsY()` 是累计父偏移的绝对坐标。
- 命中检测用相对坐标（对顶层组件正确）；面板内的子组件由 `UIPanel.mouseOver` 用面板内部坐标命中。

### 3. 玩家输入门控（HUD 上鼠标/键盘区别对待）
- 目标：**鼠标悬停 HUD 的 UI 组件（如快捷栏）时**——
  - 禁用**鼠标操作**：左键使用、右键防御、中键切换物品（`PlayerSystem.handleInput` 中这些受 `!mouseOverUI()` 门控）。
  - 禁用**与世界交互**（`WorldInputHandleSystem` 中 `curScreen == HUD && !mouseOverUI()` 门控）。
  - **不禁用键盘操作**：移动（WASD）、丢弃（Q）、数字键切换——这些只受 `curScreen == HUD` 门控，不受 `mouseOverUI()` 影响。
- 实现要点：把 `handleInput` 拆成"鼠标操作块（+ mouseOverUI 门控）"和"键盘操作块（仅 HUD 门控）"，移动/丢弃/数字切换放键盘块。
- 滚轮切换快捷栏（`WorldInputHandleSystem.scrolled`）不检查 mouseOverUI。

---

## 四、物品系统细节

### 0. 战利品表设计模式（LootGroup / LootEntry / LootGenerator）
- **分组在构建战利品表时定型，不参与运行**：`LootGroup` 是组容器（组内条目**互斥**，组名仅占位标识），构建时确定结构；`LootGenerator` 运行时不重新分组。
- `LootEntry`：持掉落模板 `ItemStack` + 权重 + 可选数量区间；`of()` 工厂构建（支持 Item/ItemStack × 固定/随机数量）。`get()` 返回 `itemStack.copy(amount)`。
- 抽取：每组独立抽取 `rollCount` 次，组内按权重累加随机命中；`allowDuplicates=false` 时用 `Iterator.remove()` 去重（**不能用 for-each 内 remove，会 CME**）。
- **数量下限可为 0**：`LootEntry.get()` 随机到 0 时返回 `ItemStack.VOID`（表示不掉落），生成处跳过空堆叠——不要直接 `copy(0)`（`ItemStack.copy` 内部 `Math.max(amount,1)` 会把 0 抬升为 1）。
- 触发：实体死亡事件查 `Registries.ENTITY_DEATH_LOOT_TABLE` → `lootTable.generate`。

### 1. 浅拷贝策略（方案 A，重要）
`JsonPropertiesMap.copy()` 的属性值副本策略（已写入 `JsonPropertiesMap.java` doc）：
- **只有实现了 `ShallowCopyable` 的类**才调用其 `copy()` 生成**独立副本**（现仅 `CatsHolder`，因其有可变 map 须隔离）。
- **未实现 `ShallowCopyable` 的值直接共享引用对象**（基本类型不可变、`AudioHolder` 无状态引用、`BlockSounds` 全局单例、`Entity` 实体引用本应共享）。这是**规定语义，不是别名隐患**，不要当 bug 改。

### 2. `AudioHolder` 是"无状态引用"
- 只持有 `Identifier` + `FileHandle`，**没有需要隔离的可变数据**。
- **不要**让它实现 `ShallowCopyable` / 提供 `copy()`（会生成无意义新实例，曾导致物品合并判定出错）。
- 保留基于 id 的 `equals/hashCode` 作健壮性。

### 3. 物品堆叠合并原则
- **"持有相同属性值的同类物品可堆叠，属性值不同的不可堆叠"**。
- 判定链：`Inventory.addItem` → `ItemStack.equals` → `getItem()` 相同 + `Property.equals` → `JsonPropertiesMap.equals`（getCount 相同 + 每项 `Objects.equals` 值语义）。

### 4. 物品使用状态（ITEM_ON_USING）与 useTimer
- `ITEM_ON_USING` 统一反映"物品正在使用"。
  - 武器的 `ITEM_ON_USING` 由 `useTimer` 的 CD 生命周期自动维护：使用成功 `setOnUsing(true)`，`ItemStack.update` 检测 `useTimer` 冷却完成自动 `setOnUsing(false)`。
  - 鱼竿（无 `WEAPON_USE_SAPN` → 无 useTimer）由自身手动维护（抛竿/收杆）。
- **`useTimer` 初始化为已 ready**：`new Timer<>(span, span)`（curSpan=maxSpan）。未使用的物品可立即使用/切换；使用后才 `setCurSpan(0)` 启动 CD。
- **`isUsing()` 判断"使用中"**：`onUsing()` 为 true，**或** useTimer 存在且 `curSpan < maxSpan`。
  - **陷阱**：不能用 `!useTimer.isReady()` 判断——`Timer.isReady()` 到点会**归零 curSpan**（副作用），在只读判断场景（如切换检查）调用会污染 useTimer 状态。
- 切换手持物品的统一入口是 `LivingEntity.setHandIndex`；使用中的物品不可切换（静默拦截）。存档初始化直接赋 `handIndex` 字段，不走 `setHandIndex`，不被拦截。

### 5. 武器耐久（命中才扣）
- 近战武器（剑）**只有击中目标**才扣耐久，空挥不扣：`Sword.use` 返回"是否命中"（`hitAnything`）。
- 挥手动画（`swingHand`）无论是否命中都表现（空挥也挥手），只是不扣耐久。
- 远程武器保持"发射即消耗"（子弹命中异步，`use` 无法即时判断）。

---

## 五、坐标与坐标系细节（易踩坑）

### 1. GUI（UI）坐标系
- `GUICamera`：`setToOrtho(false,...)` → **y 轴向上**，原点屏幕中心，视口基准 512×512。
- `Util.getMouseUIPosition()` = GUI 坐标。`Util.getMouseWindowPos()` = 窗口像素坐标（相对窗口中心，**单位是像素**，y 已反转向上）。

### 2. 世界渲染坐标系
- `PlayerCamera`：y 轴向上，原点世界中心。
- **渲染手/物品角度必须用世界坐标**，不要混用窗口像素坐标（原 bug：`getMouseWindowPos()` 与 `itemContext` 世界坐标混入同一 atan2 → 玩家偏离原点越远指向越错）。
- 正确：用 `Util.getMouseWorldPosition()` + **实体中心** `getCenterPos()` 为基点计算角度，与 `Player.getDirection()` 一致。

### 3. libgdx 绘制细节
- `SpriteBatch.draw(region, x, y, w, h)` 的 `y` 是**矩形底部**（矩形向上延伸），配合 y-up 投影。
- `BitmapFont.draw` 的 `y` 是**文本基线**，字形从基线向上延伸（`getAscent()`）+ 少量向下（`getDescent()` 为负值）。
- 行高用 `font.getLineHeight()`（保证正值），**不要**用 `ascent + descent`（descent 为负会缩小）。
- 计算文本基线让其居中于方框：基线 = 框顶 - padding - ascent。

---

## 六、事件系统

- `EventBus` 是**全局静态**，`world` 事件由 `EventSystem`（WorldSystem）在初始化时订阅。
- **事件实例需要能正确取消订阅**：`EventBus.unsubscribe(eventType, event)` 按实例引用移除。
- `EventSystem` 用 `Map<String, Set<Event>>` 记录本世界订阅的"事件类型 → 实例"，并覆写 `dispose()` 统一清理——世界销毁时自动退订，防止重进世界累积重复订阅导致事件双触发（曾导致河豚掉多个战利品）。
- `Event` 接口无 equals/hashCode，按引用匹配。

---

## 七、渲染管线

- `RenderProcessorManager` 管理渲染处理器，`unregister` 从 `processors` 和 `orderList` 移除。
- 世界渲染处理器（含 DAYNIGHT_SHADER）随 `MainGameScreen.dispose() → unregisterWorldRenderProcessors()` 正确注销，不会污染再次进入。
- **`GUIRenderProcessor` 是全局常驻处理器**（renderOrder 10000），不随世界注销。**退出世界时必须 `setMainGameScreen(null)`** 清引用，否则主菜单每帧对已 dispose 世界执行光照后处理（曾导致"夜晚退出回主菜单背景呈夜晚亮度"）。

---

## 八、其他已确认的细节与经验

- **信息面板（InfoPanel，仿 MC F3）**：按 I 键开关（`KeyBindings.InfoPanelToggle`）；FPS 每秒结算平均帧率（自累计 `frameCount/elapsedTime`，**不用** `getFramesPerSecond()` 瞬时值）；内存信息实时刷新（`Runtime.getRuntime()`，每秒或每帧）。**面板只展示不参与交互 → `setEnabled(false)`**，避免干扰鼠标命中检测。面板用字体真实度量（`getLineHeight`）计算行高包裹文字。
- **方块破坏/行走脚下粒子**：粒子贴图从方块贴图裁剪子区域（`setSummonRegion(region)` + **用完复位 null**，因发射器是共享单例）；粒子运动可组合 `ParticleMotionComp`（`PmcAirFriction`/`PmcSizeTrans`/`PmcRollRotate` 等）。
- **水生生物移动**：`canSwim()` 的实体（如河豚）在 `EntitySystem.calculateEntityCurSpeed` 中**跳过方块摩擦与空气摩擦**（水中无阻力游动，不被水的高摩擦误减速）。
- **信息面板/其他 UI 细节**：`Text` 支持 `{index}` 占位符 + `set(index, value)` 注入；多语言文本在 `assets/lang/*.json` 以 `text.xxx` 键定义。