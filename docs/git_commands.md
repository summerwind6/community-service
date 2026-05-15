# 本项目开发过程 Git 命令记录

> 项目：社区便民服务申请与进度管理系统  
> 仓库：https://gitee.com/summerwind266/community-service  
> 分支策略：`dev` 开发分支 → `main` 主分支

---

## 一、远程仓库与分支

```bash
# 查看远程仓库地址
git remote -v

# 查看所有分支（本地 + 远程）
git branch -a

# 查看当前分支 & 工作区状态
git status

# 从远程拉取最新信息（不合并）
git fetch origin

# 查看某分支的提交历史（简洁模式）
git log origin/dev --oneline -5
git log main --oneline -3
git log --oneline -5
```

**解析**：`fetch` 只下载远程更新到 `origin/xxx` 引用，不影响本地代码。`git log --oneline` 以单行摘要展示提交链，方便快速扫视历史。

---

## 二、拉取/合并代码

```bash
# 将远程 dev 分支的最新代码合并到当前分支（Fast-forward）
git merge origin/dev -m "合并远程dev分支的最新修改"

# 将 main 分支合并到 dev 分支（ort 策略）
git merge main -m "合并main分支的提交到dev"
```

**解析**：
- `Fast-forward`：当前分支落后于目标分支时，直接移动 HEAD 指针到最新提交，不产生新的 merge commit。
- `ort` 策略：两个分支有分叉时，Git 会自动生成一个 merge commit 记录合并操作。
- `-m`：为非交互式合并提供 commit message，避免弹出编辑器。

---

## 三、Git Worktree 管理（并行工作区）

```bash
# 列出所有 worktree
git worktree list

# 从 git 元数据中清理已删除的 worktree 记录
git worktree prune --expire=now

# 强制删除 worktree（谨慎）
git worktree remove .claude/worktrees/xxx --force
```

**解析**：
- `worktree`：在同一仓库下创建多个独立工作目录，各自在不同分支上开发，互不干扰。
- `prune --expire=now`：当 worktree 目录已不存在但 git 元数据仍有记录时，立即清除这些过时引用。
- `remove --force`：强制删除 worktree 及其分支，即使有未合并的提交。

---

## 四、分支管理

```bash
# 基于远程分支创建本地分支，并自动设置追踪
git checkout -b dev origin/dev

# 查看当前所在分支
git branch --show-current
```

**解析**：`checkout -b dev origin/dev` 等价于三步：创建本地 `dev` 分支 → 设置为追踪 `origin/dev` → 切换到该分支。

---

## 五、提交代码（6次独立提交）

```bash
# === 第一阶段：居民信息管理 ===
git add backend/src/main/java/com/community/controller/resident/ \
        backend/src/main/java/com/community/config/ShiroConfig.java \
        frontend/src/api/resident/ \
        frontend/src/views/resident/ \
        frontend/src/components/Layout.vue \
        frontend/src/router/index.js
git commit -m "【第一阶段】居民信息管理模块 — Controller CRUD + 前端页面"

# === 第二阶段：服务事项管理 ===
git add backend/src/main/java/com/community/controller/item/ItemController.java \
        frontend/src/api/item/item.js \
        frontend/src/views/service/ServiceItem.vue
git commit -m "服务事项管理完善：ItemController改为注入接口+分页筛选，ServiceItem.vue新增弹窗/删除/状态切换交互"

# === 第三阶段：申请审核流程 ===
git add backend/src/main/java/com/community/controller/apply/ApplyController.java \
        backend/src/main/java/com/community/service/ApplicationFormService.java \
        backend/src/main/java/com/community/service/impl/ApplicationFormServiceImpl.java \
        frontend/src/api/apply/apply.js \
        frontend/src/views/apply/ApplyList.vue
git commit -m "申请审核流程：submit生成applyNo+校验，review/approve状态流转+AuditRecord，前端详情弹窗+审核交互"

# === 第四阶段：进度查询 ===
git add backend/src/main/java/com/community/controller/apply/ApplyController.java \
        backend/src/main/java/com/community/service/ApplicationFormService.java \
        backend/src/main/java/com/community/service/impl/ApplicationFormServiceImpl.java \
        frontend/src/api/apply/progress.js \
        frontend/src/views/apply/ProgressQuery.vue \
        frontend/src/router/index.js
git commit -m "进度查询：后端/apply/progress手机号查申请（免登录），前端ProgressQuery.vue独立页面"

# === 第五阶段：数据统计 ===
git add backend/src/main/java/com/community/controller/stat/StatController.java \
        frontend/src/views/dashboard/DashboardView.vue \
        frontend/src/views/stat/StatAnalysis.vue
git commit -m "数据统计：StatController改为真实查询（仪表盘/趋势/分布），前端图表对接API数据"

# === Bug 修复：状态筛选类型冲突 ===
git add -A
git commit -m "修复申请管理状态筛选：PageDTO.status为Integer与String冲突，加InitBinder禁止绑定+Service接收String status"

# === 第六阶段：样式收尾 ===
git add frontend/src/assets/styles/index.scss \
        frontend/src/components/Layout.vue
git commit -m "收尾优化：全局样式统一background/font，Layout移除调试日志"

git add frontend/src/views/login/LoginView.vue \
        frontend/src/views/apply/ProgressQuery.vue
git commit -m "统一Login/ProgressQuery样式：浅灰背景+白卡片，登录页增加居民进度查询入口按钮"
```

**解析**：
- `git add <文件路径>`：将指定文件加入暂存区。优先精确指定文件而非 `git add .` 或 `git add -A`，避免误提交 `.env`、密钥等敏感文件。
- `git commit -m "..."`：创建提交。消息采用 `模块：具体变更 + 代码功能简述` 格式。
- `git add -A`：仅用于 bug 修复提交，已知所有变更内容。

---

## 六、推送到远程

```bash
# 推送 dev 分支到远程仓库
git push origin dev
```

**解析**：将本地 `dev` 分支的提交同步到 `origin/dev`。如果 fetch 后远程有新提交，push 会先要求 `git pull --rebase`。

---

## 七、开发节奏总结

| 序号 | 提交 | 阶段 |
|------|------|------|
| 1 | `e9c43a3` | 居民信息管理 CRUD |
| 2 | `cb76c76` | 服务事项管理完善 |
| 3 | `32ce1bd` | 申请提交与审核流程 |
| 4 | `b709080` | 进度查询 |
| 5 | `ee439f0` | 数据统计 |
| 6 | `2f411f3` | Bug 修复：状态筛选 |
| 7 | `10d423c` | 收尾：全局样式 + 调试清理 |
| 8 | `2135aaf` | 收尾：Login/Progress 样式统一 |

> **流程**：每阶段先本地开发 → 编译验证 → `git add` + `git commit` → 用户测试 → `git push origin dev`
