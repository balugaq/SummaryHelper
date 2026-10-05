# SummaryHelper

> [!IMPORTANT]
> 该附属已废弃，不再维护。[JustEnoughGuide](https://github.com/balugaq/JustEnoughGuide) 提供了更多高级功能（如其中的`/jeg timings`测量更为精确），建议替换，[点击前往下载 JustEnoughGuide](https://builds.guizhanss.com/balugaq/JustEnoughGuide)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

一个 [Slimefun](https://github.com/Slimefun/Slimefun4) 附属插件：读取 Slimefun Profiler 的统计数据，帮助服主快速定位、传送、排查卡服的 Slimefun 方块。

- 作者：balugaq
- 前置：[Slimefun](https://github.com/SlimefunGuguProject/Slimefun4)（硬依赖）
- 运行环境：Paper 1.18+（`api-version: 1.18`），**Java 20+**

## 功能一览

| 子命令 | 说明 |
| --- | --- |
| `getTimings` | 汇总当前一轮 Profiler 数据，输出耗时最高的 **Top 20** Slimefun 方块（世界;X:Y:Z、方块名、耗时），鼠标悬停查看完整列表 |
| `tpHighestLagBlock` | 直接传送到最卡的那个 Slimefun 方块，并报告坐标、方块名、耗时 |
| `findNearestSlimefunBlock` | 在身边 ±10 范围内找最近的 Slimefun 方块并传送；范围内没有时退化为全世界找最近的一个（控制台则只报告坐标） |
| `tpChunk <chunkX> <chunkZ> [world]` | 传送到指定区块（区块坐标，自动 ×16），可指定世界，带世界边界校验 |
| `task start/stop <times>` | 压测工具：周期性把世界内所有非玩家实体上下传送 `times` 次制造卡顿，用于本地测试（`stop` 需任务 id） |
| `reload` | 重载插件（重新读取配置、重注册命令与监听器） |

主命令：`/summaryhelper`，别名 `/sh`，例如 `/sh getTimings`。**仅 OP 可用**（无独立权限节点）。

## 安装

1. 服务端安装 [Slimefun](https://github.com/SlimefunGuguProject/Slimefun4)；
2. 将 `SummaryHelper-x.jar` 放入 `plugins/` 目录；
3. 重启服务器。

自动更新：仅当版本号以 `Build` 开头（CI 构建）且 `config.yml` 中 `auto-update: true` 时，通过 GuizhanUpdater 从本仓库拉取更新。

## 构建

```bash
mvn clean package
```

产物为 `target/SummaryHelper-<version>.jar`（shade 后的成品；`original-*.jar` 不是可部署的）。

依赖全部为 provided，只有 `MorePersistentDataTypes` 会被 shade 进 jar。

## 配置

`plugins/SummaryHelper/config.yml`：

| 键 | 默认 | 说明 |
| --- | --- | --- |
| `auto-update` | `false` | 是否允许自动更新（仅 `Build` 版本生效） |
| `debug` | `false` | 调试模式 |

## 工作原理

- `SlimefunTickDoneListener` 定时轮询 Slimefun `Profiler` 的 `ticksPassed` 字段（反射），一轮统计结束（tick 数变化）时，把 `Profiler.timings` 解包为 `Map<Location, Long>`；
- 命令产生的查询先以 `CachedRequest` 进入主类请求队列，等下一轮 Profiler 数据就绪后统一消费（每 tick 最多处理 4000 个请求），避免读到半成品数据；
- 位置由 Profiler 内部的打包 long（`position`）解出 x/y/z，再配合 `world` 字段还原 `Location`。

因此命令输出的耗时**来自 Slimefun 最近一轮完整统计**，不是实时值。

## 许可

[MIT](LICENSE) © 2024-2026 balugaq
