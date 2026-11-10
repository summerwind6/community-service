# 社区便民服务申请与进度管理系统

居民可公开提交申请、上传材料、查询进度和评价服务。社区与街道工作人员通过后台维护服务事项与办事指南、处理申请和审核通知。

## 架构与功能

| 模块 | 职责 |
| --- | --- |
| 前端 | Vue 3 与 Element Plus。提供居民办事入口、审核后台、预约管理和 Excel 报表 |
| 网关 | Spring Cloud Gateway 根据 Nacos 注册的服务名路由，保留前端的 `/api` 接口 |
| 核心服务 | Spring Boot 3.2.5、Shiro 和 MyBatis-Plus。负责权限、申请审核、实时进度、附件与通知，并用 Redis 缓存启用事项列表 |
| 指南服务 | 维护、发布并缓存公开指南，支持双实例部署 |
| 审核通知 | 审核和 Outbox 事件在同一个 MySQL 事务内提交。Kafka 异步投递，通知按事件 ID 去重 |

## 本地演示

需要 Docker Engine 和 Docker Compose v2。复制 `.env.example` 为 `.env`，填入独立的随机密码和服务凭据，JWT 密钥至少 32 字节。

```powershell
Copy-Item .env.example .env
# 填写 .env 后启动
docker compose up -d --build --wait
docker compose ps
```

- 前端入口 `http://localhost:3000`，居民申请页 `http://localhost:3000/resident/apply`。
- 网关 API 入口 `http://localhost:8080/api`。开发时可在 `frontend` 运行 `npm ci` 和 `npm run dev`，Vite 默认代理到此网关。
- MySQL、Redis、Nacos、Kafka、核心服务及两台指南实例通过 Compose 网络访问。Nacos 演示命名空间为 `community-demo`。

创建管理员时，复制 `sql/create_admin.example.sql` 为 `sql/create_admin.local.sql`，替换用户名和密码后，从 PowerShell 导入。

```powershell
Get-Content -Raw -Encoding UTF8 sql/create_admin.local.sql |
  docker compose exec -T mysql sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -uroot community_service'
```

## 开发构建

```bash
mvn clean package
cd frontend
npm ci
npm run build
```

后端需要 JDK 17 和 Maven 3.8 或以上。前端构建和演示统一使用 Node.js 22。三个可执行 JAR 分别位于 `backend/target`、`community-guide/target` 和 `community-gateway/target`。本地直接启动各 JAR 时，按服务的 `application.yml` 注入环境变量，依次启动 Nacos、MySQL、Redis、Kafka、核心、指南和网关。

| 目录 | 职责 |
| --- | --- |
| `backend` | 核心服务，负责 Shiro 权限、申请审核、实时进度、附件和通知 |
| `community-guide` | 办事指南维护、公开查询和 Redis 列表缓存 |
| `community-gateway` | Spring Cloud Gateway，按 Nacos 服务名路由 |
| `community-shared` | 接口数据结构、请求追踪和列表缓存 |
| `deploy` | 演示配置、探测脚本和服务初始化 |
| `sql` | 数据库初始化、增量迁移和管理员模板 |

## 测试与验收

- 在项目根目录执行 `mvn test` 运行后端测试。
- 启动独立演示环境并创建测试管理员后，按[业务回归步骤](docs/升级部署与验收.md#重跑业务回归)运行 `node --test deploy/regression.mjs`。脚本会创建测试申请、评价、预约和通知。
- 提交前通过了 9 项 Maven 测试、8 组业务回归和前端生产构建。无头 Chrome 验证了登录、居民入口和 19 个后台页面。
- 本地联调中，公开指南在 20 并发下的 MySQL 查询次数从原版 20 次降到冷缓存 1 次、热缓存 0 次。双实例下线切换、Redis 故障和 Kafka 停机补发的步骤与结果见[部署验收与故障恢复](docs/升级部署与验收.md)。

## 已有数据库升级

先备份数据库。旧环境如果尚未执行 [2026-08-09 迁移](sql/upgrade_20260809.sql)，先执行该脚本，再执行一次 [2026-11-10 迁移](sql/upgrade_20261110.sql)。后者新增审核 Outbox 与通知来源事件唯一索引。

新环境由 Compose 初始化数据库，或使用 `sql/init.sql` 手工建库。该脚本会重建业务表。已有环境完成迁移后，为指南账号授予 `guide` 表读写权限，为核心账号授予其余业务表读写权限，并在同一切换窗口迁移指南读写流量。部署凭据通过环境变量注入。

- [架构优化与微服务升级方案](docs/架构优化与微服务升级方案.md)
- [部署验收与故障恢复](docs/升级部署与验收.md)
