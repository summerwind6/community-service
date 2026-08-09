# 社区便民服务申请与进度管理系统 - 小组协作开发规范

## 📋 项目概述
- **项目名称**: 社区便民服务申请与进度管理系统（政企简化版）
- **技术栈**: SpringBoot 3.2.x + Vue3 + MySQL 8.0
- **开发周期**: 4周（28天）
- **团队规模**: 多人协作开发

---

## 一、Git分支管理规范

### 1.1 分支结构
```
main (主分支 - 生产环境)
  └── dev (开发分支 - 测试环境)
       ├── feature/张三/auth (个人功能分支)
       ├── feature/李四/user
       ├── feature/王五/item
       └── ...
```

### 1.2 分支命名规则
- **主分支**: `main` （仅用于发布生产版本）
- **开发分支**: `dev` （日常开发集成分支）
- **功能分支**: `feature/{姓名}/{模块名}`
  - ✅ 正确示例: `feature/张三/login`, `feature/李四/user-manage`
  - ❌ 错误示例: `feat`, `my-branch`, `test`

### 1.3 分支操作流程

#### 每日工作流程
```bash
# 1. 从dev拉取最新代码
git checkout dev
git pull origin dev

# 2. 创建个人功能分支
git checkout -b feature/张三/auth

# 3. 开发完成后，提交代码
git add .
git commit -m "【权限模块】新增登录认证接口"

# 4. 合并到dev分支
git checkout dev
git merge feature/张三/auth --no-ff -m "Merge: 合并张三的登录功能"

# 5. 推送到远程
git push origin dev

# 6. 删除本地功能分支（可选）
git branch -d feature/张三/auth
```

#### 每周发布流程（周五下午）
```bash
# 1. 切换到main分支
git checkout main
git pull origin main

# 2. 合并dev到main
git merge dev --no-ff -m "Release: v1.0.0 周版本合并"

# 3. 打标签
git tag -a v1.0.0 -m "Release version 1.0.0"

# 4. 推送main和标签
git push origin main --tags
```

### 1.4 合并频率要求
- **个人→dev**: 每日至少合并1次（下班前必须合并）
- **dev→main**: 每周五下午16:00统一合并
- **紧急修复**: 可随时申请合并，需组长审批

### 1.5 禁止事项
- ❌ 禁止直接在main分支提交代码
- ❌ 禁止直接在dev分支开发新功能
- ❌ 禁止推送未经测试的代码到dev/main
- ❌ 禁止强制推送（force push）到共享分支

---

## 二、代码命名规范

### 2.1 后端Java命名规范

#### 包命名（全小写+点分隔）
```
com.community.config      # 配置类
com.community.controller   # 控制器
com.community.service      # 服务层
com.community.service.impl # 服务实现
com.community.mapper       # 数据访问层
com.community.entity.po    # 实体对象
com.community.entity.dto   # 数据传输对象
com.community.entity.vo    # 视图对象
com.community.common       # 公共工具
com.community.shiro        # Shiro相关
```

#### 类命名（大驼峰 PascalCase）
```java
// 实体类
SysUser.java, ApplicationForm.java, AuditRecord.java

// Controller类
AuthController.java, UserController.java, ItemController.java

// Service接口
SysUserService.java, ItemService.java

// Service实现类
SysUserServiceImpl.java, ItemServiceImpl.java

// 配置类
ShiroConfig.java, SwaggerConfig.java, CorsConfig.java

// 工具类
PasswordUtil.java, Result.java, JwtUtil.java
```

#### 方法命名（小驼峰 camelCase）
```java
// 查询方法
getUserById(), getUserList(), findByUsername()

// 新增方法
save(), insert(), create(), add()

// 更新方法
update(), modify(), edit()

// 删除方法
deleteById(), remove(), delete()

// 布尔判断方法
isValid(), hasPermission(), exists()
```

#### 变量命名（小驼峰 camelCase）
```java
String username;
List<SysUser> userList;
Integer pageNum;
Boolean isSuccess;
```

#### 常量命名（全大写+下划线）
```java
public static final String TOKEN_HEADER = "Authorization";
public static final int MAX_LOGIN_ATTEMPTS = 5;
public static final String DEFAULT_PASSWORD = "123456";
```

#### 数据库字段命名（下划线 snake_case）
```sql
user_name, create_time, update_time, is_deleted
```

### 2.2 前端Vue3命名规范

#### 文件命名（小写+连字符 kebab-case）
```
views/
├── login/
│   └── login-view.vue          ✅ 正确
├── dashboard/
│   └── dashboard-view.vue      ✅ 正确
components/
├── layout.vue                  ✅ 正确
└── user-table.vue              ✅ 正确
```
❌ 错误: `LoginView.vue`, `UserTable.VUE`, `myComponent.vue`

#### 组件名称（大驼峰 PascalCase）
```vue
<script setup>
// 组件内部使用PascalCase
import UserTable from './user-table.vue'
import LayoutHeader from './layout-header.vue'
</script>

<template>
  <!-- 模板中使用kebab-case -->
  <user-table />
  <layout-header />
</template>
```

#### JavaScript变量和方法（小驼峰 camelCase）
```javascript
const userInfo = ref({})
const isLoading = ref(false)
const tableData = reactive([])

function handleSubmit() { }
function loadData() { }
function handleSearch() { }
const formatStatus = () => { }
```

#### CSS类名（BEM方法论或小驼峰）
```css
/* BEM命名 */
.user-card { }
.user-card__header { }
.user-card__title { }

/* 或小驼峰 */
.statCard { }
.statCard-title { }
```

#### 路由路径（小写+连字符）
```javascript
{
  path: '/system/user',        // ✅ 正确
  path: '/service-item',       // ✅ 正确
  name: 'ServiceItem'          // ✅ PascalCase
}
```

#### API接口命名（资源名词）
```javascript
// api/auth/auth.js
export function login(data) { }      // 登录
export function logout() { }         // 登出
export function getUserInfo() { }    // 获取用户信息

// api/user/user.js
export function getUserList(params) { }  // 获取用户列表
export function addUser(data) { }        // 新增用户
export function updateUser(data) { }     // 更新用户
```

---

## 三、提交信息规范

### 3.1 提交格式
```
【{模块}】{功能描述}
```

### 3.2 模块分类标识
| 标识 | 模块 | 适用范围 |
|------|------|----------|
| 权限 | 认证授权 | 登录、登出、Shiro配置 |
| 用户 | 用户管理 | 用户CRUD、个人信息 |
| 角色 | 角色管理 | 角色分配、权限设置 |
| 服务 | 服务项目 | 服务项增删改查 |
| 申请 | 申请管理 | 申请单处理、审核 |
| 统计 | 数据统计 | 报表、图表 |
| 系统 | 系统配置 | 全局配置、公共组件 |
| 文档 | 项目文档 | README、注释更新 |
| 修复 | Bug修复 | 问题修复 |
| 重构 | 代码重构 | 优化、性能提升 |

### 3.3 提交示例
```bash
✅ 【权限模块】新增登录认证接口和Token生成逻辑
✅ 【用户模块】完成用户列表分页查询功能
✅ 【服务模块】添加服务项目的表单验证规则
✅ 【申请模块】修复审核状态流转Bug
✅ 【系统模块】优化全局异常处理器
✅ 【重构】提取公共分页组件为可复用组件

❌ 修改了bug
❌ 更新代码
❌ fix
❌ test commit
```

### 3.4 提交前检查清单
- [ ] 代码已通过编译（无语法错误）
- [ ] 已删除console.log调试代码
- [ ] 遵循命名规范
- [ ] 关键代码已添加注释
- [ ] 无敏感信息（密码、密钥等）
- [ ] 提交信息符合格式要求

---

## 四、冲突避免指南（核心重点）

### 4.1 高风险文件列表（多人修改易冲突）

以下文件需要**特别协调**，禁止同时修改：

#### 后端高风险文件
```
backend/src/main/resources/application.yml     # 配置文件
backend/pom.xml                                 # 依赖管理
backend/src/main/java/com/community/config/     # 所有配置类
backend/src/main/java/com/community/common/     # 公共返回类、异常类
```

#### 前端高风险文件
```
frontend/src/router/index.js                    # 路由配置
frontend/src/store/user.js                      # 状态管理
frontend/src/utils/request.js                   # Axios拦截器
frontend/src/components/Layout.vue              # 公共布局
frontend/package.json                           # 依赖管理
frontend/vite.config.js                         # 构建配置
```

### 4.2 冲突预防策略

#### 策略1: 频繁同步
```bash
# 每天开始工作时执行
git checkout dev
git pull origin dev
git checkout feature/你的分支
git merge dev
```

#### 策略2: 小步提交
```bash
# 每30分钟-1小时提交一次，不要堆积大量改动
git add .
git commit -m "【模块】完成某小功能的第1部分"
```

#### 策略3: 功能隔离
```bash
# 不同人负责不同模块，减少同一文件修改
张三 → auth模块 (controller/auth/, views/login/)
李四 → user模块 (controller/user/, views/system/)
王五 → item模块 (controller/item/, views/service/)
赵六 → apply模块 (controller/apply/, views/apply/)
```

#### 策略4: 修改前沟通
在群内声明要修改的文件：
> "@所有人 我今天要修改 application.yml 和 router/index.js"

### 4.3 冲突解决流程

#### Step 1: 发现冲突
```bash
git pull origin dev
# 输出: CONFLICT (content): Merge conflict in xxx.java
```

#### Step 2: 手动解决冲突
```java
<<<<<<< HEAD
// 你当前的代码
public String getName() {
    return this.name;
}
=======
// 合并进来的代码
public String getRealName() {
    return this.realName;
}
>>>>>>> feature/xxx/branch
```

**保留需要的代码，删除标记符：**
```java
public String getRealName() {
    return this.realName;
}
```

#### Step 3: 标记为已解决
```bash
git add <冲突文件>
git commit -m "【修复】解决XX文件的合并冲突"
```

#### Step 4: 验证
```bash
# 编译检查
mvn compile  # 后端
npm run build  # 前端

# 本地测试
# 启动后端服务，验证功能正常
```

### 4.4 冲突严重时的应急方案
如果冲突无法解决：
1. **放弃本次合并**
   ```bash
   git merge --abort
   ```

2. **重新基于最新dev创建分支**
   ```bash
   git checkout dev
   git pull origin dev
   git checkout -b feature/你的分支-v2
   # 手动将你的改动复制过来
   ```

3. **寻求帮助**
   - 联系组长或代码原作者
   - 在群里说明冲突情况
   - 共享屏幕一起解决

---

## 五、目录结构规范（强制统一）

### 5.1 后端目录结构
```
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/community/
│   │   │   ├── CommunityApplication.java    # 启动类
│   │   │   ├── config/                     # 配置层
│   │   │   │   ├── ShiroConfig.java
│   │   │   │   ├── SwaggerConfig.java
│   │   │   │   ├── MyBatisPlusConfig.java
│   │   │   │   └── CorsConfig.java
│   │   │   ├── controller/                 # 控制器层（按模块分包）
│   │   │   │   ├── auth/
│   │   │   │   ├── user/
│   │   │   │   ├── item/
│   │   │   │   ├── apply/
│   │   │   │   └── stat/
│   │   │   ├── service/                    # 服务层
│   │   │   │   ├── XxxService.java         # 接口
│   │   │   │   └── impl/
│   │   │   │       └── XxxServiceImpl.java # 实现
│   │   │   ├── mapper/                     # 数据访问层
│   │   │   ├── entity/                     # 实体层
│   │   │   │   ├── po/                     # 持久化对象
│   │   │   │   ├── dto/                    # 数据传输对象
│   │   │   │   └── vo/                     # 视图对象
│   │   │   ├── common/                     # 公共工具
│   │   │   │   ├── Result.java
│   │   │   │   ├── BusinessException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── shiro/                      # Shiro安全
│   │   │       ├── CustomRealm.java
│   │   │       └── PasswordUtil.java
│   │   └── resources/
│   │       └── application.yml             # 配置文件
│   └── test/                               # 测试代码
└── sql/
    └── init.sql                            # 数据库初始化脚本
```

### 5.2 前端目录结构
```
frontend/
├── package.json
├── vite.config.js
├── index.html
├── src/
│   ├── main.js                             # 入口文件
│   ├── App.vue                             # 根组件
│   ├── api/                                # API接口（按模块分包）
│   │   ├── auth/
│   │   ├── user/
│   │   ├── item/
│   │   ├── apply/
│   │   └── stat/
│   ├── views/                              # 页面（按模块分包）
│   │   ├── login/
│   │   ├── dashboard/
│   │   ├── system/
│   │   ├── service/
│   │   ├── apply/
│   │   └── stat/
│   ├── components/                         # 公共组件
│   │   ├── Layout.vue
│   │   └── ...
│   ├── router/                             # 路由配置
│   │   └── index.js
│   ├── store/                              # 状态管理
│   │   └── user.js
│   ├── utils/                              # 工具类
│   │   ├── request.js
│   │   └── common.js
│   └── assets/                             # 静态资源
│       ├── styles/
│       │   └── index.scss
│       └── images/
└── public/                                 # 公共静态资源
```

---

## 六、注释规范

### 6.1 类注释
```java
/**
 * 系统用户控制器
 * 处理用户相关的HTTP请求，包括登录、注册、信息管理等
 * 
 * @author 张三
 * @version 1.0.0
 * @since 2026-05-11
 */
@RestController
@RequestMapping("/user")
public class UserController {
}
```

### 6.2 方法注释
```java
/**
 * 用户登录认证
 * 
 * @param loginDTO 登录请求参数（包含用户名和密码）
 * @return Result<LoginVO> 登录成功返回token和用户信息
 * @throws BusinessException 当用户名或密码错误时抛出
 */
@PostMapping("/login")
public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
    // 实现登录逻辑
}
```

### 6.3 关键代码行注释
```java
// 使用MD5加盐加密密码，盐值为随机UUID
String hashedPassword = PasswordUtil.encrypt(password, salt);

// 查询用户时排除已删除记录
wrapper.eq(SysUser::getDeleted, 0);
```

### 6.4 SQL注释
```sql
-- 查询所有启用状态的角色（用于下拉选择框）
SELECT id, role_name, role_code 
FROM sys_role 
WHERE status = 1 AND deleted = 0;

-- 管理员账号由 sql/create_admin.example.sql 的本地副本创建，禁止提交或使用公开默认密码。
```

---

## 七、开发环境要求

### 7.1 必需软件
- **JDK**: 17+ （推荐17.0.10）
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Node.js**: 18+ （推荐18.20 LTS）
- **IDE**: IntelliJ IDEA 2023+ / VS Code
- **Git**: 2.40+

### 7.2 IDE配置建议
- **编码格式**: UTF-8
- **缩进风格**: 4空格（不使用Tab）
- **行尾符号**: LF（Unix格式）
- **保存时自动格式化**: 开启

### 7.3 编辑器配置文件（.editorconfig）
```ini
root = true

[*]
charset = utf-8
indent_style = space
indent_size = 4
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.md]
trim_trailing_whitespace = false
```

---

## 八、代码审查 Checklist

### 8.1 自查清单（提交前必做）
- [ ] 代码可以正常编译运行
- [ ] 没有TODO、FIXME、HACK等未完成标记
- [ ] 没有硬编码的魔法数字（使用常量替代）
- [ ] 没有敏感信息泄露（密码、API Key等）
- [ ] 异常处理完善，不会导致程序崩溃
- [ ] 日志输出合理（不过多也不过少）
- [ ] SQL语句使用了预编译（防SQL注入）
- [ ] 前端输入做了校验（XSS防护）

### 8.2 互审要点
- [ ] 命名是否符合规范
- [ ] 是否存在重复代码（应抽取为公共方法）
- [ ] 是否有性能问题（N+1查询、大量数据加载等）
- [ ] 业务逻辑是否正确
- [ ] 是否有安全隐患

---

## 九、常见问题FAQ

### Q1: 如何处理依赖版本不一致？
A: 统一使用pom.json和package.json中定义的版本，禁止私自升级。

### Q2: 可以使用AI辅助编程吗？
A: 可以，但必须人工审查生成的代码，确保质量和安全性。

### Q3: 数据库变更如何同步？
A: 所有DDL变更必须写入SQL脚本，并在群内通知其他成员执行。

### Q4: 如何申请修改公共配置文件？
A: 在群内提出申请，确认无人正在修改后再进行操作。

### Q5: 遇到无法解决的Bug怎么办？
A: 先在群里说明问题现象，附上错误日志截图，寻求帮助。

---

## 十、联系方式与支持

- **项目负责人**: 组长姓名
- **技术支持群**: 微信群/钉钉群名称
- **代码仓库地址**: Git仓库URL
- **Swagger文档**: http://localhost:8080/api/swagger-ui.html
- **前端地址**: http://localhost:3000

---

**最后更新时间**: 2026-05-11  
**文档版本**: v1.0.0  
**适用范围**: 全体开发组成员
