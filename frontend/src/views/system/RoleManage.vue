<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" icon="Plus" @click="openDialog(null)">新增角色</el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="120" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="openPermDialog(row)">分配权限</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 角色新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
      <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码（如 ADMIN）" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roleForm.description" placeholder="请输入角色描述" type="textarea" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="roleForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveRole">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px">
      <el-tree
        ref="permTreeRef"
        :data="permTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermIds"
        :props="{ label: 'permName', children: 'children' }"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingPerm" @click="handleSavePerm">保存权限</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getRoleList, addRole, updateRole, deleteRole, getRolePermissions, assignRolePermissions } from '@/api/auth/role'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const savingPerm = ref(false)
const roleFormRef = ref()
const permTreeRef = ref()
const currentRoleId = ref()
const checkedPermIds = ref([])

const roleForm = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const roleRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const permTree = [
  { id: 1, permName: '仪表盘', children: [] },
  { id: 2, permName: '系统管理', children: [
    { id: 3, permName: '用户列表' },
    { id: 4, permName: '用户新增' },
    { id: 5, permName: '用户修改' },
    { id: 6, permName: '用户详情' },
    { id: 7, permName: '角色列表' }
  ]},
  { id: 8, permName: '服务管理', children: [
    { id: 9, permName: '服务列表' },
    { id: 10, permName: '服务新增' },
    { id: 11, permName: '服务修改' },
    { id: 12, permName: '服务删除' },
    { id: 13, permName: '服务详情' }
  ]},
  { id: 14, permName: '申请管理', children: [
    { id: 15, permName: '申请列表' },
    { id: 16, permName: '申请审核' }
  ]},
  { id: 17, permName: '统计分析', children: [
    { id: 18, permName: '趋势分析' },
    { id: 19, permName: '分布统计' }
  ]}
]

async function loadData() {
  loading.value = true
  try {
    const res = await getRoleList()
    tableData.value = res.data || []
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    Object.assign(roleForm, row)
  } else {
    isEdit.value = false
    Object.assign(roleForm, { id: null, roleName: '', roleCode: '', description: '', status: 1 })
  }
  dialogVisible.value = true
}

async function handleSaveRole() {
  if (!roleFormRef.value) return
  await roleFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await updateRole(roleForm)
        ElMessage.success('更新成功')
      } else {
        await addRole(roleForm)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch {
      // handled by interceptor
    } finally {
      saving.value = false
    }
  })
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该角色吗？', '确认', { type: 'warning' }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

async function openPermDialog(row) {
  currentRoleId.value = row.id
  try {
    const res = await getRolePermissions(row.id)
    checkedPermIds.value = (res.data || []).map(p => p.id)
  } catch {
    checkedPermIds.value = []
  }
  permDialogVisible.value = true
}

async function handleSavePerm() {
  savingPerm.value = true
  try {
    const checked = permTreeRef.value.getCheckedKeys()
    await assignRolePermissions(currentRoleId.value, { permIds: checked })
    ElMessage.success('权限分配成功')
    permDialogVisible.value = false
  } catch {
    // handled by interceptor
  } finally {
    savingPerm.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
