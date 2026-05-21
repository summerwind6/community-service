<template>
  <el-dialog
    v-model="visible"
    title="新增申请"
    width="560px"
    @closed="handleClosed"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="选择居民" prop="residentId">
        <el-select
          v-model="form.residentId"
          filterable
          placeholder="请选择居民（可输入姓名搜索）"
          style="width: 100%;"
          @change="onResidentChange"
        >
          <el-option
            v-for="r in residentList"
            :key="r.id"
            :label="`${r.name} — ${r.phone || '无手机号'}`"
            :value="r.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="服务项目" prop="serviceItemId">
        <el-select
          v-model="form.serviceItemId"
          placeholder="请选择服务项目"
          style="width: 100%;"
          @change="onItemChange"
        >
          <el-option
            v-for="item in itemList"
            :key="item.id"
            :label="`${item.itemName}（${item.processingDays || 7}天办结）`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="申请内容" prop="applyContent">
        <el-input
          v-model="form.applyContent"
          type="textarea"
          :rows="4"
          placeholder="请描述申请内容（如申请原因、具体情况等）"
        />
      </el-form-item>

      <el-descriptions v-if="selectedItem" :column="2" size="small" border style="margin-top:8px">
        <el-descriptions-item label="服务类别">{{ selectedItem.category }}</el-descriptions-item>
        <el-descriptions-item label="费用">
          {{ selectedItem.fee > 0 ? `¥${selectedItem.fee}` : '免费' }}
        </el-descriptions-item>
        <el-descriptions-item label="所需材料" :span="2">
          {{ selectedItem.requiredDocs || '无特殊要求' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        受理并提交
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { submitApply, reviewApply } from '@/api/apply/apply'
import { getResidentList } from '@/api/resident/resident'
import { getEnabledItems } from '@/api/item/item'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['success'])
const visible = ref(false)
const submitting = ref(false)
const formRef = ref()
const residentList = ref([])
const itemList = ref([])
const selectedItem = ref(null)

const form = reactive({
  residentId: null,
  residentName: '',
  serviceItemId: null,
  serviceItemName: '',
  applyContent: ''
})

const rules = {
  residentId: [{ required: true, message: '请选择居民', trigger: 'change' }],
  serviceItemId: [{ required: true, message: '请选择服务项目', trigger: 'change' }],
  applyContent: [{ required: true, message: '请填写申请内容', trigger: 'blur' }]
}

async function open() {
  try {
    const [rRes, iRes] = await Promise.all([
      getResidentList({ pageNum: 1, pageSize: 100 }),
      getEnabledItems()
    ])
    residentList.value = rRes.data?.records || []
    itemList.value = iRes.data || []
  } catch {
    residentList.value = []
    itemList.value = []
  }
  visible.value = true
}

function onResidentChange(id) {
  const r = residentList.value.find(x => x.id === id)
  form.residentName = r ? r.name : ''
}

function onItemChange(id) {
  const item = itemList.value.find(x => x.id === id)
  if (item) {
    form.serviceItemName = item.itemName
    selectedItem.value = item
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const res = await submitApply(form)
      const applyId = res.data?.id
      if (applyId) {
        await reviewApply(applyId, { result: 'APPROVED', opinion: '社区受理员受理' })
      }
      ElMessage.success('申请已受理，已流转至街道审核')
      visible.value = false
      emit('success')
    } catch {
      // handled by interceptor
    } finally {
      submitting.value = false
    }
  })
}

function handleClosed() {
  formRef.value?.resetFields()
  selectedItem.value = null
}

defineExpose({ open })
</script>
