# AGENTS.md — SummaryHelper 代理工作指南

给在本仓库工作的 AI 代理看的项目指南。改动前先读完，别凭通用经验猜。

## TL;DR

- **这是什么**：Minecraft Paper 插件、Slimefun 附属（`plugin.yml` 硬依赖 `Slimefun`）。核心能力：反射读取 Slimefun `Profiler` 的耗时统计，帮服主定位/传送/排查卡服方块。
- **构建命令**：`mvn clean package`（Maven 默认 goal 就是 `clean package`，直接 `mvn` 也行）。**没有测试套件**，编译通过即静态验收。
- **成品位置**：`target/SummaryHelper-<version>.jar`。`target/original-*.jar` 是 shade 前的半成品，**不可部署**，别拿错。
- **Java 版本**：编译目标 Java 20（`pom.xml` `java.version=20`），运行时也要求 Java 20+。类文件版本向下不兼容，别擅自降级或升级 source/target。
- **git 红线**：只把工作区留到可验收状态。不 commit、不 push、不改历史，提交权归主人，最多给出建议的提交信息。

## 项目结构

```
src/main/java/com/balugaq/summaryhelper/
├── implementation/SummaryHelper.java   # 主类：实例、请求队列、命令注册、GuizhanUpdater 自动更新
├── api/CachedRequest.java              # 查询请求（sender + 时间戳 + callback），挂在主类队列上
├── core/
│   ├── commands/
│   │   ├── MainCommand.java            # /summaryhelper（/sh）分发，仅 OP；SubCommand 按首参数匹配
│   │   ├── SubCommand.java             # 子命令抽象基类
│   │   └── list/                       # 六个子命令，见下表
│   ├── listeners/SlimefunTickDoneListener.java  # 核心：轮询 Profiler，解包 timings，消费请求队列
│   └── managers/ConfigManager.java     # config.yml 读取 + 缺失键自动补全
└── utils/                              # HoverUtil / ClipboardUtil / KeyUtil / PersistentUtil / ReflectionUtil / SlimefunItemUtil
src/main/resources/
└── plugin.yml                          # 注意：没有 config.yml 资源文件，见「坑」一节
```

新增子命令：继承 `SubCommand`，实现 `getIdentifier()`，并在 `SummaryHelper#onEnable` 的 `MainCommand` 构造列表里注册。

## 架构关键点（改核心逻辑前必读）

1. **请求队列 + 延迟消费**：命令不直接查数据，而是 `SummaryHelper.getInstance().addRequest(new CachedRequest(sender, callback))` 入队；`SlimefunTickDoneListener` 检测到 Slimefun `Profiler.ticksPassed` 变化（即一轮完整统计结束）时，反射解包 `Profiler.timings` 为 `Map<Location, Long>`，再逐个消费队列回调。单 tick 最多处理 4000 个请求。这是为了**只消费完整一轮的统计数据**，别破坏这个时序。
2. **位置解包**：Profiler 的 timings key 是内部对象，`position` 是打包 long——`x = position >> 38`、`y = position & 4095`、`z = position << 26 >> 38`。改这段要清楚位运算语义，别"顺手优化"。
3. **反射硬编码**：`SlimefunTickDoneListener` 依赖 Slimefun 内部字段名 `ticksPassed`、`timings`、`position`、`world`，以及 `StorageCacheUtils.getSfItem()`。升级 `pom.xml` 里的 Slimefun4 版本后，这些可能静默失效（返回 null / 空数据），必须人工回归 `getTimings`、`tpHighestLagBlock` 两个命令。
4. **reload 语义**：`SummaryHelper#reload()` 直接 `onDisable() + onEnable()`，会重新 `new SlimefunTickDoneListener()`（新增一个 `runTaskTimer` 任务，旧任务未取消）。目前是既有行为，改动前先确认主人是否接受语义变化。
5. **权限模型**：`MainCommand` 只判 `sender.isOp()`，没有 permission 节点。加新命令默认跟随此模型，除非主人另有要求。

## 命令与输出约定

- 游戏内输出目前是英文（如 "Teleported to..."），工具类里有两处中文（`ClipboardUtil` 的 "信息: "、主类更新失败日志）。新代码跟随周围上下文，别混着改语言。
- 悬停详情用 `HoverUtil.send()`，点击复制用 `ClipboardUtil.send()`，保持风格一致。
- `@SuppressWarnings("deprecation")` 在多处出现（bungee ChatColor、`SlimefunItem.getItemName()` 等），是有意为之，别为了消警告大规模重构。

## 坑（踩过的都记这）

- **`original-*.jar` 不是成品**，部署/shade 校验一律看 `SummaryHelper-<version>.jar`。
- **resources 下没有 `config.yml`**：`ConfigManager#setupDefaultConfig` 里 `plugin.getResource("config.yml")` 返回 null 直接 return，缺键补全逻辑实际不生效。若主人要新增配置项，需同时创建 `src/main/resources/config.yml`，否则数据目录里永远不会出现该键。
- **`TaskCommand` 有现存 bug**：`args.length < 2` 的分支之后直接读 `args[2]`，`/sh task start`（无 times 参数）会 `ArrayIndexOutOfBoundsException`。改它时注意 `stop` 也依赖 `args[2]`（任务 id），修边界要一起覆盖。
- **依赖仓库顺序**：Slimefun4 走 jitpack，GuizhanLibPlugin 走 codemc，别删 `pom.xml` 里任何一个 repository。
- **资源过滤**：`pom.xml` 对 `src/main/resources` 开了 filtering（`plugin.yml` 的 `${project.version}` 靠它替换），zip 被显式排除。新增含 `${...}` 字面量的资源文件会被误替换，注意。

## 验收清单（每次改动后自查）

- [ ] `mvn clean package` 编译通过、shade 正常（检查 `target/SummaryHelper-*.jar` 更新时间）。
- [ ] 改了涉及 Profiler/timings 的代码 → 提示主人需在实机回归 `getTimings` / `tpHighestLagBlock`。
- [ ] 改了命令 → 核对 `plugin.yml` 的 command 注册与 `onEnable` 的子命令列表是否同步。
- [ ] 未提交任何 git 操作。
