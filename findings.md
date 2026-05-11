# 研究发现与关键信息

## 项目架构决策
1. **后端目录结构**: 严格遵循 `com.community` 包结构
2. **前端技术选型**: Vue3 + Vite构建工具（比Webpack更快）
3. **状态管理**: 使用Pinia（Vue3官方推荐）
4. **权限控制**: Shiro实现RBAC模型

## 技术要点
- SpringBoot 3.2.x 要求JDK 17+
- MyBatis-Plus 3.5.x 支持代码生成器
- Shiro 1.10.x 兼容SpringBoot 3
- Element UI 2.15.x 需要完整引入或按需加载

## 数据库设计要点
- 8张核心表：sys_user, sys_role, sys_permission, sys_role_perm, resident_info, service_item, application_form, audit_record
- 默认角色：超级管理员(ADMIN)、社区受理员(CLERK)、街道审核员(REVIEWER)
- 密码使用Shiro的Md5Hash加盐加密

## 协作规范重点
- Git分支：main → dev → feature/姓名/模块
- 每日合并到dev，每周合并到main
- 提交格式：【模块】功能描述
- 冲突避免：配置文件、路由文件、公共组件需协调修改

## 参考资料
- (待补充)
