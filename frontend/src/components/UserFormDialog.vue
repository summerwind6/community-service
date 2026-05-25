<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑用户' : '新增用户'"
    width="520px"
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item v-if="!isEdit" label="密码" prop="password">
        <el-input v-model="form.password" type="password" show-password placeholder="请输入密码（6-20位）" />
      </el-form-item>
      <el-form-item v-if="!isEdit" label="角色" prop="roleId">
        <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%;">
          <el-option
            v-for="role in roleList"
            :key="role.id"
            :label="role.roleName"
            :value="role.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="form.realName" placeholder="请输入真实姓名" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { addUser, updateUser } from '@/api/user/user'
import { getRoleList } from '@/api/auth/role'

const emit = defineEmits(['success'])
const visible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editId = ref(null)
const formRef = ref()
const roleList = ref([])

const form = reactive({
  username: '',
  password: '',
  roleId: null,
  realName: '',
  phone: '',
  email: '',
  status: 1
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20位之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' }
  ],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

async function open(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.username = row.username
    form.realName = row.realName || ''
    form.phone = row.phone || ''
    form.email = row.email || ''
    form.status = row.status
  } else {
    isEdit.value = false
    editId.value = null
    form.username = ''
    form.password = ''
    form.roleId = null
    form.realName = ''
    form.phone = ''
    form.email = ''
    form.status = 1
    try {
      const res = await getRoleList()
      roleList.value = res.data || []
    } catch {
      roleList.value = []
    }
  }
  visible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateUser({ id: editId.value, realName: form.realName, phone: form.phone, email: form.email, status: form.status })
      } else {
        await addUser(form, { roleId: form.roleId })
      }
      visible.value = false
      emit('success')
    } catch {
      // error handled by interceptor
    } finally {
      submitting.value = false
    }
  })
}

function handleClosed() {
  formRef.value?.resetFields()
}

defineExpose({ open })
</script>
