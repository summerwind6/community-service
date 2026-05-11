# 🚀 社区便民服务申请与进度管理系统 - 快速启动指南

## 📌 项目信息
- **项目名称**: 社区便民服务申请与进度管理系统（政企简化版）
- **默认账号**: `admin` / `admin123`
- **后端地址**: http://localhost:8080/api
- **前端地址**: http://localhost:3000
- **Swagger文档**: http://localhost:8080/api/swagger-ui.html

---

## ✅ 环境准备清单

### 必需软件（按顺序安装）

| 软件 | 版本要求 | 用途 | 下载地址 |
|------|----------|------|----------|
| JDK | 17+ | Java运行环境 | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 或 [Adoptium](https://adoptium.net/) |
| Maven | 3.8+ | 项目构建工具 | [Maven官网](https://maven.apache.org/download.cgi) |
| MySQL | 8.0+ | 数据库 | [MySQL官网](https://dev.mysql.com/downloads/) |
| Node.js | 18+ LTS | 前端运行环境 | [Node.js官网](https://nodejs.org/) |
| Git | 2.40+ | 版本控制 | [Git官网](https://git-scm.com/) |
| IDE | IDEA/VSCode | 开发工具 | [IntelliJ IDEA](https://www.jetbrains.com/idea/) / [VS Code](https://code.visualstudio.com/) |

### 可选软件
- **Redis** 6.2+ (缓存，可选)
- **Navicat/DBeaver** (数据库管理工具)
- **Postman/Apifox** (接口测试工具)

---

## 📦 第一步：获取项目代码

### 方式A: 从Git仓库克隆（推荐）
```bash
git clone <你的仓库地址>
cd 社区便民服务申请与进度管理系统
```

### 方式B: 直接下载ZIP
下载项目压缩包并解压到本地目录。

---

## 🗄️ 第二步：初始化数据库

### 1. 创建数据库
打开MySQL客户端（命令行或Navicat等工具），执行：

```sql
CREATE DATABASE community_service DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 导入初始化数据
使用MySQL客户端执行SQL脚本：

#### 命令行方式：
```bash
mysql -u root -p community_service < sql/init.sql
```

#### Navicat/DBeaver方式：
1. 连接到MySQL服务器
2. 选择 `community_service` 数据库
3. 右键 → 运行SQL文件 → 选择 `sql/init.sql`
4. 执行完成

### 3. 验证数据导入成功
```sql
USE community_service;
SHOW TABLES;  -- 应显示9张表

SELECT * FROM sys_user;  -- 应显示1条管理员记录
SELECT * FROM sys_role;   -- 应显示3条角色记录
```

---

## ⚙️ 第三步：配置后端项目

### 1. 打开项目
使用IntelliJ IDEA打开 `backend` 目录：
```
File → Open → 选择 backend 文件夹 → Open as Project
```

### 2. 配置Maven
```
File → Settings → Build, Execution, Deployment → Build Tools → Maven
- Maven home path: 选择你安装的Maven路径（或使用IDEA内置）
- User settings file: ~/.m2/settings.xml
- Local repository: ~/.m2/repository
```

### 3. 修改数据库配置（重要！）
编辑文件：`backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/community_service?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root          # 改成你的MySQL用户名
    password: your_password # 改成你的MySQL密码
```

### 4. 安装依赖
在IDEA右侧Maven面板中点击：
```
刷新按钮 (Reload All Maven Projects)
```
或者命令行：
```bash
cd backend
mvn clean install -DskipTests
```

### 5. 启动后端服务
方式一：IDEA中直接运行
- 找到 `CommunityApplication.java` 文件
- 右键 → Run 'CommunityApplication'

方式二：命令行启动
```bash
cd backend
mvn spring-boot:run
```

### 6. 验证后端启动成功
看到以下输出表示启动成功：
```
============================================
  社区便民服务申请与进度管理系统 启动成功！
  Swagger文档地址: http://localhost:8080/api/swagger-ui.html
============================================
```

### 7. 测试接口
浏览器访问：http://localhost:8080/api/swagger-ui.html
应该能看到API文档界面。

---

## 🎨 第四步：配置前端项目

### 1. 安装依赖
打开终端（CMD/PowerShell/Terminal），进入frontend目录：

```bash
cd frontend
npm install
```

> 如果网络慢，可以使用淘宝镜像：
> ```bash
> npm install --registry=https://registry.npmmirror.com
> ```

### 2. 启动开发服务器
```bash
npm run dev
```

看到以下输出表示启动成功：
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

### 3. 访问前端页面
浏览器打开：http://localhost:3000

应看到登录页面。

---

## 🔐 第五步：登录系统

### 使用默认账号登录
- **用户名**: `admin`
- **密码**: `admin123`
- （可选）勾选"记住我"

点击"登录"按钮，成功后跳转到仪表盘页面。

---

## 🧪 第六步：功能验证清单

### 后端接口测试（使用Swagger）
访问：http://localhost:8080/api/swagger-ui.html

测试以下核心接口：

✅ **认证模块**
- POST `/api/auth/login` - 用户登录
- GET `/api/auth/info` - 获取当前用户信息
- POST `/api/auth/logout` - 用户登出

✅ **用户管理**
- GET `/api/user/list` - 获取用户列表
- GET `/api/user/{id}` - 获取用户详情
- POST `/api/user` - 新增用户
- PUT `/api/user` - 更新用户

✅ **服务管理**
- GET `/api/item/list` - 获取服务项目列表
- POST `/api/item` - 新增服务项目

✅ **申请管理**
- POST `/api/apply` - 提交申请
- GET `/api/apply/list` - 获取申请列表

✅ **统计分析**
- GET `/api/stat/dashboard` - 仪表盘统计
- GET `/api/stat/trend` - 趋势分析

### 前端页面测试
✅ 登录页面正常显示和提交
✅ 登录成功后跳转到仪表盘
✅ 左侧菜单导航正常
✅ 各页面能正常加载数据
✅ ECharts图表正常渲染

---

## 👥 第七步：开始团队协作

### 1. 创建个人分支
```bash
git checkout dev
git pull origin dev
git checkout -b feature/你的姓名/模块名
```

示例：
```bash
git checkout -b feature/张三/user-manage
```

### 2. 开始开发
按照分配的模块进行开发，遵循《小组协作开发规范》。

### 3. 每日合并到dev
下班前必须合并：
```bash
git add .
git commit -m "【模块】今日完成的功能"
git checkout dev
git pull origin dev
git merge feature/你的分支 --no-ff
git push origin dev
```

### 4. 遇到问题？
查看详细规范文档：`docs/TEAM_COLLABORATION_GUIDE.md`

---

## ❓ 常见问题排查

### 问题1: Maven依赖下载失败
**解决方案**:
1. 检查网络连接
2. 配置阿里云镜像（修改settings.xml）：
```xml
<mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```
3. 重试 `mvn clean install`

### 问题2: 数据库连接失败
**错误提示**: `Communications link failure`

**解决方案**:
1. 确认MySQL服务已启动
2. 检查application.yml中的连接配置
3. 确认端口3306未被占用
4. 检查防火墙设置

### 问题3: 端口被占用
**错误提示**: `Port 8080 was already in use`

**解决方案**:
```bash
# Windows查看占用端口的进程
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F

# 或者修改application.yml的端口为8081
```

### 问题4: 前端npm install失败
**解决方案**:
```bash
# 清除缓存
npm cache clean --force

# 删除node_modules和package-lock.json
rm -rf node_modules package-lock.json

# 重新安装
npm install --registry=https://registry.npmmirror.com
```

### 问题5: 前端请求跨域错误
**解决方案**:
确认vite.config.js已配置代理：
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

### 问题6: Shiro登录失败
**可能原因**:
1. 密码未加密（需要MD5+盐值）
2. 数据库中admin密码不匹配

**解决方法**:
重新导入init.sql脚本，或手动更新密码：
```sql
UPDATE sys_user SET password = 'e10adc3949ba59abbe56e057f20f883e' WHERE username = 'admin';
```

### 问题7: Swagger无法访问
**检查项**:
1. 确认后端服务已启动
2. 访问地址是否正确：`http://localhost:8080/api/swagger-ui.html`
3. 检查Shiro配置是否放行了swagger路径

---

## 📚 相关文档

| 文档名称 | 路径 | 说明 |
|----------|------|------|
| 小组协作规范 | `docs/TEAM_COLLABORATION_GUIDE.md` | Git、命名、提交、冲突避免规范 |
| 数据库初始化脚本 | `sql/init.sql` | 完整表结构和初始数据 |
| 后端配置文件 | `backend/src/main/resources/application.yml` | 所有后端配置项 |
| 前端配置文件 | `frontend/vite.config.js` | Vite构建配置 |
| API接口文档 | http://localhost:8080/api/swagger-ui.html | 在线Swagger文档 |

---

## 🎯 下一步行动

### 对于开发者：
1. ✅ 完成本地环境搭建
2. ✅ 成功登录系统
3. 📖 阅读 `docs/TEAM_COLLABORATION_GUIDE.md`
4. 🔧 向组长领取开发任务
5. 🌿 创建个人功能分支开始开发

### 对于项目负责人：
1. ✅ 将代码推送到Git仓库
2. 👥 邀请成员加入仓库
3. 📋 分配开发任务给各成员
4. 📢 在群内公布仓库地址和文档位置

---

## 💡 开发小贴士

1. **保持频繁提交**: 每30分钟-1小时提交一次代码
2. **先沟通再动手**: 修改公共文件前先在群里声明
3. **写好注释**: 关键逻辑必须有注释说明
4. **及时同步**: 每天上班第一件事是pull最新代码
5. **遇到问题及时求助**: 不要卡在一个问题上超过2小时

---

## 📞 技术支持

- **文档问题**: 查看 `docs/TEAM_COLLABORATION_GUIDE.md`
- **环境问题**: 参考本文档"常见问题排查"章节
- **代码问题**: 在开发群内提问，附上错误日志截图
- **紧急联系**: 组长联系方式

---

**祝开发顺利！🎉**

*最后更新时间: 2026-05-11*
*文档版本: v1.0.0*
