# 社区便民服务申请与进度管理系统 - 快速启动指南

## 项目信息
- **项目名称**: 社区便民服务申请与进度管理系统（政企简化版）
- **默认账号**: `admin` / `admin123`
- **后端地址**: http://localhost:8080/api
- **前端地址**: http://localhost:3000
- **进度查询（免登录）**: http://localhost:3000/progress
- **Swagger文档**: http://localhost:8080/api/swagger-ui.html

---

## 环境准备

| 软件 | 版本 | 用途 |
|------|------|------|
| JDK | 17+ | Java 运行环境 |
| Maven | 3.8+ | 项目构建 |
| MySQL | 8.0+ | 数据库 |
| Node.js | 18+ LTS | 前端运行环境 |
| Git | 2.40+ | 版本控制 |

---

## 第一步：获取代码

```bash
git clone https://gitee.com/summerwind266/community-service.git
cd community-service
git checkout dev
```

---

## 第二步：初始化数据库

```sql
CREATE DATABASE community_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

```bash
mysql -u root -p community_service < sql/init.sql
```

---

## 第三步：启动后端

1. 修改 `backend/src/main/resources/application.yml` 中的数据库用户名密码
2. IDEA 运行 `CommunityApplication.java`，或：
```bash
cd backend
mvn spring-boot:run
```
3. 验证：http://localhost:8080/api/swagger-ui.html

---

## 第四步：启动前端

```bash
cd frontend
npm install
npm run dev
```
访问 http://localhost:3000

---

## 功能模块

### 管理后台（需登录）
| 模块 | 页面路由 | 说明 |
|------|---------|------|
| 仪表盘 | `/dashboard` | ECharts 图表统计 |
| 用户管理 | `/system/user` | 管理员增删改查 |
| 角色管理 | `/system/role` | 角色权限配置 |
| 系统配置 | `/system/config` | 基础参数设置 |
| 居民管理 | `/resident/list` | 居民信息 CRUD |
| 服务项目 | `/service/item` | 便民服务事项管理 |
| 申请管理 | `/apply/list` | 申请审核流程 |
| 统计分析 | `/stat/analysis` | 趋势/分布图表 |

### 公开页面（免登录）
| 页面 | 路由 | 说明 |
|------|------|------|
| 进度查询 | `/progress` | 手机号查询申请进度 |
| 管理员登录 | `/login` | 登录页含进度查询入口 |

---

## 后端 API 接口

### 认证管理 `/auth`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 登录 |
| POST | `/auth/logout` | 登出 |
| GET | `/auth/info` | 当前用户信息 |
| PUT | `/auth/change-password` | 修改密码 |

### 居民管理 `/resident`
| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/resident/list` | resident:list | 分页列表 |
| GET | `/resident/{id}` | resident:detail | 详情 |
| POST | `/resident` | resident:add | 新增 |
| PUT | `/resident` | resident:update | 编辑 |
| DELETE | `/resident/{id}` | resident:delete | 删除 |
| GET | `/resident/by-phone` | 无 | 手机号查询 |

### 服务事项 `/item`
| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/item/list` | 无 | 分页筛选 |
| GET | `/item/enabled` | 无 | 已启用列表 |
| GET | `/item/{id}` | item:detail | 详情 |
| POST | `/item` | item:add | 新增 |
| PUT | `/item` | item:update | 编辑 |
| PUT | `/item/{id}/status` | item:update | 启停切换 |
| DELETE | `/item/{id}` | item:delete | 删除 |

### 申请管理 `/apply`
| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| POST | `/apply` | 无 | 提交申请 |
| GET | `/apply/list` | apply:list | 分页筛选 |
| GET | `/apply/progress` | 无 | 手机号进度查询 |
| GET | `/apply/{id}` | 无 | 申请详情 |
| GET | `/apply/{id}/audit-records` | 无 | 审核记录 |
| POST | `/apply/{id}/review` | apply:audit | 初审 |
| POST | `/apply/{id}/approve` | apply:audit | 复审 |

### 数据统计 `/stat`
| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/stat/dashboard` | stat:dashboard | 仪表盘数据 |
| GET | `/stat/trend` | stat:trend | 近7天趋势 |
| GET | `/stat/distribution` | stat:distribution | 事项分布 |

---

## 常见问题

**端口被占用**：修改 `application.yml` 的 `server.port`，或 `vite.config.js` 的 proxy 目标地址。

**跨域错误**：确认 Vite 代理配置 `frontend/vite.config.js` 中 `/api` 指向 `http://localhost:8080`。

**登录失败**：确认数据库中 admin 密码正确（默认 `admin123` 的 MD5+盐值）。

**前端依赖安装慢**：
```bash
npm install --registry=https://registry.npmmirror.com
```

---

## 开发流程

```bash
git checkout dev
git pull origin dev
git checkout -b feature/姓名/模块名
# 开发...
git add <具体文件>
git commit -m "模块：变更说明"
# 测试通过后通知合并
```

---

## 文档索引

| 文档 | 路径 |
|------|------|
| 项目计划书 | `项目计划书.md` |
| 数据库脚本 | `sql/init.sql` |
| Git 命令记录 | `docs/git_commands.md` |
| 任务计划 | `task_plan.md` |

---

*最后更新: 2026-05-15*
