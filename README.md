# ***游戏：战斗——爽！***

[![Fork me on Gitee](https://gitee.com/muxiuesd12138/fight-remake/widgets/widget_2.svg)](https://gitee.com/muxiuesd12138/fight-remake)  
[![star](https://gitee.com/muxiuesd12138/fight-remake/badge/star.svg?theme=dark)](https://gitee.com/muxiuesd12138/fight-remake/stargazers)
[![fork](https://gitee.com/muxiuesd12138/fight-remake/badge/fork.svg?theme=dark)](https://gitee.com/muxiuesd12138/fight-remake/members)

##  一、游戏类型：
    - 2D无限地图
    - 射击
    - 战斗
    - 生存
    - 极限模式（角色死亡一切重来）
    - 类沙盒
---

## 二、游戏启动方式：

### 在游戏文件对应的目录下：

- 1、直接双击游戏文件。

- 2、在游戏文件所在的文件夹中使用命令行：
    ```bash
    java -jar fight-remake-version.jar
    ```
### 游戏启动后会在游戏文件同一级目录下生成名为mods的文件夹，把mod文件放进去即可在启动游戏时自动加载

---

## 三、游戏开发规范：

- ### 驼峰命名法
- ### 类和方法的注释说明要完全
  - 注释规范：
    ```java
    /**
    * 获取方块信息
    * @param Block 待获取信息的方块
    * @return 方块信息
    */
    public String getBlockInfo (Block Block) {
        //你的逻辑
        return blockInfo;
    } 
    ```
  
- ### 变量名称难以一眼看出作用的也要注释
  - 注释规范：
    ```java
    public String abc = "I love Rem!";  //这是文本
    ```

- ### 术语定义
  - Id的定义：
    ```text
       分隔符  这是名称（name）
         ↓     ↓
    fight:grass_block  -> 这一整串称为Id
    ~~~~~
      ↑
    这个是命名空间（namespace）
    ```

---

## 四、游戏Mod开发相关：

- ### Mod加载所用的引擎：Nashorn
- ### 编写Mod所需语言及语法：Javascript ECMAScript 5.1
- ### Mod API目前所能做的到的：
  - 游戏世界相关的事件
  - 输入检测
  - 加载mod的一些资源
  - mod库的导出与调用
  - 游戏元素的注册，如方块（已实现）、实体（已实现）、墙体（待实现）
- ### 后续计划更新的API：
  - 地形生成逻辑
  - 玩家相关
  - 游戏启动相关
  - ……
- ### Mod开发文档：
  - 计划编写中…… 
- ### Mod引擎后续更新计划：
  - 加载压缩的mod文件，如zip格式的
  - 加载不愿公开源码的mod文件，即加密过的mod文件
---

## 五、特别鸣谢：
- ### [zqll](https://gitee.com/zqll4) （优化游戏代码架构）
- ### [wabrara](https://gitee.com/wabrara) （解决草地渲染与着色器问题、解决区块问题、解决区块方块查找问题）
- ### LibGDX吧官方交流群的各位 （群号：1051955354）







