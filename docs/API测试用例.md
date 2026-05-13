# API 测试用例（Apifox）

> 基础URL: `http://localhost:8080/api`
> 默认账号: admin / admin123
> Swagger: http://localhost:8080/api/swagger-ui.html

---

## 前置条件
1. MySQL 已启动，执行过 `sql/init.sql`
2. Redis 已启动（或注释掉 Redis 配置）
3. 后端项目已启动在 8080 端口

---

## 1. 认证模块 `/auth`

### 1.1 登录 ✅ 核心

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "超级管理员",
      "phone": "13800138000",
      "email": "admin@community.com",
      "status": 1,
      "createTime": "2026-05-11 xx:xx:xx"
    }
  }
}
```

**断言:**
- code = 200
- data.token 不为空
- data.user.username = "admin"

---

### 1.2 登录失败（密码错误）

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "wrongpassword"
}
```

**预期响应:** code = 401

---

### 1.3 获取当前用户信息

```
GET /api/auth/info
Authorization: Bearer {{token}}
```

> ⚠️ 需要先执行登录，将返回的 token 填入 Authorization 头

**预期响应:** code = 200, data.username = "admin"

---

### 1.4 修改密码

```
PUT /api/auth/change-password
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "oldPassword": "admin123",
  "newPassword": "admin456"
}
```

**预期响应:** code = 200

> 测试后用 1.1 验证新密码有效，然后改回 admin123

---

### 1.5 更新个人资料

```
PUT /api/auth/profile
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "realName": "管理员001",
  "phone": "13900139000",
  "email": "newadmin@community.com"
}
```

**预期响应:** code = 200, data.realName = "管理员001"

---

### 1.6 登出

```
POST /api/auth/logout
Authorization: Bearer {{token}}
```

**预期响应:** code = 200

---

## 2. 用户管理 `/user`

> 全部需要 Authorization 头

### 2.1 用户列表

```
GET /api/user/list?pageNum=1&pageSize=10
Authorization: Bearer {{token}}
```

**断言:**
- code = 200
- data.records 数组长度 > 0
- data.total > 0

---

### 2.2 带搜索条件的列表

```
GET /api/user/list?pageNum=1&pageSize=10&keyword=admin&status=1
Authorization: Bearer {{token}}
```

---

### 2.3 用户详情

```
GET /api/user/1
Authorization: Bearer {{token}}
```

**断言:** code = 200, data.username = "admin"

---

### 2.4 新增用户

```
POST /api/user
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "username": "clerk01",
  "password": "clerk123",
  "realName": "社区受理员01",
  "phone": "13800000001",
  "email": "clerk01@community.com",
  "status": 1
}
```

**断言:** code = 200

---

### 2.5 更新用户

```
PUT /api/user
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "id": 2,
  "realName": "社区受理员张三",
  "phone": "13800000002",
  "email": "zhangsan@community.com",
  "status": 1
}
```

**断言:** code = 200

---

## 3. 角色管理 `/role`

> 全部需要 Authorization 头

### 3.1 角色列表

```
GET /api/role/list
Authorization: Bearer {{token}}
```

**断言:** code = 200, data 数组长度 = 3（ADMIN、CLERK、REVIEWER）

---

### 3.2 角色详情

```
GET /api/role/1
Authorization: Bearer {{token}}
```

---

### 3.3 新增角色

```
POST /api/role
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "roleName": "测试角色",
  "roleCode": "TEST",
  "description": "用于接口测试",
  "status": 1
}
```

---

### 3.4 更新角色

```
PUT /api/role
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "id": 4,
  "roleName": "测试角色-已修改",
  "roleCode": "TEST",
  "description": "更新后的描述",
  "status": 1
}
```

---

### 3.5 查询角色权限

```
GET /api/role/1/permissions
Authorization: Bearer {{token}}
```

**断言:** code = 200, data 数组包含多个权限对象

---

### 3.6 分配角色权限

```
PUT /api/role/1/permissions
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "permIds": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
}
```

---

### 3.7 删除角色

```
DELETE /api/role/4
Authorization: Bearer {{token}}
```

---

## 4. 服务项目管理 `/item`

### 4.1 服务项目列表（无需登录）

```
GET /api/item/list
```

---

### 4.2 服务项目详情

```
GET /api/item/1
Authorization: Bearer {{token}}
```

---

### 4.3 新增服务项目

```
POST /api/item
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "itemName": "临时救助申请",
  "itemCode": "TEMP_AID",
  "category": "救助帮扶",
  "description": "因突发事件导致生活困难的临时救助申请",
  "fee": 0,
  "processingDays": 5,
  "requiredDocs": "身份证、困难证明、社区证明",
  "status": 1
}
```

---

### 4.4 更新服务项目

```
PUT /api/item
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "id": 1,
  "itemName": "居住证明办理（更新）",
  "processingDays": 2
}
```

---

### 4.5 删除服务项目

```
DELETE /api/item/6
Authorization: Bearer {{token}}
```

---

## 5. 申请管理 `/apply`

### 5.1 提交申请

```
POST /api/apply
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "residentId": 1,
  "residentName": "张三",
  "serviceItemId": 1,
  "serviceItemName": "居住证明办理",
  "applyContent": "因工作调动需要开具居住证明",
  "status": "PENDING"
}
```

---

### 5.2 我的申请列表

```
GET /api/apply/my
Authorization: Bearer {{token}}
```

---

### 5.3 所有申请列表（管理员）

```
GET /api/apply/list
Authorization: Bearer {{token}}
```

---

### 5.4 审核申请（通过）

```
PUT /api/apply/audit/1
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "status": "APPROVED",
  "remark": "材料齐全，审核通过"
}
```

---

### 5.5 审核申请（驳回）

```
PUT /api/apply/audit/1
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "status": "REJECTED",
  "remark": "材料不全，请补充后再提交"
}
```

---

## 6. 统计分析 `/stat`

### 6.1 仪表盘数据

```
GET /api/stat/dashboard
Authorization: Bearer {{token}}
```

---

### 6.2 申请趋势

```
GET /api/stat/trend
Authorization: Bearer {{token}}
```

---

### 6.3 服务类型分布

```
GET /api/stat/distribution
Authorization: Bearer {{token}}
```

---

## 权限测试 🔒

| 测试场景 | 接口 | 预期结果 |
|----------|------|----------|
| 未登录访问用户列表 | `GET /api/user/list` | 401 或重定向 |
| 无权限角色操作 | 用 CLERK 角色登录后调 `GET /api/user/list` | 403 权限不足 |

---

## Apifox 配置步骤

1. 创建项目 → 选择"导入" → "手动创建"
2. 设置**环境变量**:
   - `baseUrl`: `http://localhost:8080/api`
   - `token`: 留空，登录后填入
3. 在 **Auth** 标签页设置全局 Header:
   - `Authorization`: `Bearer {{token}}`
4. 执行登录接口后，在"后置操作"中添加:
   ```javascript
   // 提取 token 到环境变量
   pm.environment.set("token", pm.response.json().data.token)
   ```
