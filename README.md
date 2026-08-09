# 社区便民服务申请与进度管理系统

面向社区服务事项的申请、审核、进度查询与统计管理系统。项目采用前后端分离架构：后端为 Spring Boot，前端为 Vue 3。

## 功能概览

- 居民、服务事项、申请单及审核记录管理
- 居民公开申请、进度查询、材料上传与服务评价
- 预约、通知、办事指南、超时预警和数据报表
- RBAC 权限管理、系统配置和在线会话管理

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.2、MyBatis-Plus、Shiro、MySQL 8、Redis |
| 前端 | Vue 3、Vite、Element Plus、Pinia、ECharts |

## 目录说明

```text
backend/     后端服务
frontend/    前端应用
sql/         数据库初始化、升级和管理员创建脚本
docs/        接口测试、协作规范和功能分工设计
```

## 快速启动

### 1. 环境要求

- JDK 17+、Maven 3.8+
- MySQL 8.0+
- Redis（后端会话存储）
- Node.js 18+

### 2. 初始化数据库

新部署时执行完整初始化脚本。该脚本会重建 `community_service` 中的业务表，请勿用于已有数据的数据库。

```bash
mysql -u root -p < sql/init.sql
```

初始化脚本不包含可登录的默认管理员。复制 `sql/create_admin.example.sql` 为本地文件，替换用户名和强密码占位符后执行：

```bash
mysql -u root -p community_service < sql/create_admin.local.sql
```

已有旧数据库请先备份，再执行一次升级脚本：

```bash
mysql -u root -p community_service < sql/upgrade_20260809.sql
```

### 3. 启动后端

复制 `backend/src/main/resources/application-local.example.yml` 为 `application-local.yml`，填写数据库密码、Redis 密码（如有）和至少 32 字节的 JWT 密钥。该本地文件已被 Git 忽略。

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：`http://localhost:8080/api`

Swagger：`http://localhost:8080/api/swagger-ui.html`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址：`http://localhost:3000`；居民办事入口：`http://localhost:3000/resident/apply`。

## 数据库脚本说明

| 文件 | 用途 |
| --- | --- |
| `sql/init.sql` | 新环境完整初始化：建库、建表、基础角色、权限、服务事项和系统配置 |
| `sql/create_admin.example.sql` | 创建独立超级管理员的模板；请复制并替换占位符后执行 |
| `sql/upgrade_20260809.sql` | 旧数据库升级到当前结构；包含预警字段、权限修复和旧演示账号禁用 |

## 文档

| 文档 | 说明 |
| --- | --- |
| `docs/API测试用例.md` | 接口联调与验收用例 |
| `docs/TEAM_COLLABORATION_GUIDE.md` | 团队协作与提交规范 |
| `docs/新增功能设计文档.md` | 新增功能说明与成员分工 |

## 开发约定

- 不要提交 `application-local.yml`、真实密码、JWT 密钥或本地创建的管理员脚本。
- 数据库结构变更应提供独立升级脚本，并在合并前同步给团队成员。
- 提交前至少执行后端测试和前端构建：`mvn test`、`npm run build`。
