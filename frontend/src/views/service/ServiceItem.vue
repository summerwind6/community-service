<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>服务项目管理</span>
        </div>
      </template>

      <div class="search-form">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="关键词">
            <el-input v-model="searchForm.keyword" placeholder="项目名称/类别" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:120px">
              <el-option label="启用" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div style="margin-bottom: 16px;">
        <el-button type="primary" icon="Plus" @click="openDialog(null)">新增项目</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="itemName" label="项目名称" min-width="150" />
        <el-table-column prop="itemCode" label="项目编码" width="120" />
        <el-table-column prop="category" label="服务类别" width="120" />
        <el-table-column prop="fee" label="费用(元)" width="100">
          <template #default="{ row }">
            {{ row.fee === 0 ? '免费' : `¥${row.fee}` }}
          </template>
        </el-table-column>
        <el-table-column prop="processingDays" label="处理天数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              @change="(val) => handleToggleStatus(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑服务项目' : '新增服务项目'"
      width="560px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="itemName">
              <el-input v-model="form.itemName" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目编码" prop="itemCode">
              <el-input v-model="form.itemCode" placeholder="请输入" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="服务类别" prop="category">
              <el-select v-model="form.category" placeholder="请选择" style="width:100%">
                <el-option label="证件办理" value="证件办理" />
                <el-option label="救助帮扶" value="救助帮扶" />
                <el-option label="养老服务" value="养老服务" />
                <el-option label="计划生育" value="计划生育" />
                <el-option label="就业服务" value="就业服务" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用(元)" prop="fee">
              <el-input-number v-model="form.fee" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="处理天数" prop="processingDays">
          <el-input-number v-model="form.processingDays" :min="1" :max="90" style="width:200px" />
        </el-form-item>
        <el-form-item label="所需材料" prop="requiredDocs">
          <el-input v-model="form.requiredDocs" type="textarea" :rows="2" placeholder="所需材料说明" />
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="项目描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getItemList, addItem, updateItem, deleteItem, toggleItemStatus } from '@/api/item/item'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: '',
  status: null
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const defaultForm = {
  id: null,
  itemName: '',
  itemCode: '',
  category: '',
  fee: 0,
  processingDays: 7,
  requiredDocs: '',
  description: ''
}
const form = reactive({ ...defaultForm })

const rules = {
  itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  itemCode: [{ required: true, message: '请输入项目编码', trigger: 'blur' }],
  category: [{ required: true, message: '请选择服务类别', trigger: 'change' }]
}

function openDialog(row) {
  if (row) {
    isEdit.value = true
    Object.assign(form, row)
  } else {
    isEdit.value = false
  }
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(form, defaultForm)
  formRef.value?.resetFields()
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateItem({ ...form })
      ElMessage.success('编辑成功')
    } else {
      await addItem({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

async function handleToggleStatus(row, enabled) {
  try {
    await toggleItemStatus(row.id, enabled ? 1 : 0)
    row.status = enabled ? 1 : 0
    ElMessage.success(enabled ? '已启用' : '已停用')
  } catch {
    // handled by interceptor
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除「${row.itemName}」吗？删除后不可恢复`, '确认删除', {
    type: 'warning'
  }).then(async () => {
    await deleteItem(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

async function loadData() {
  loading.value = true
  try {
    const res = await getItemList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.status = null
  pageNum.value = 1
  loadData()
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
