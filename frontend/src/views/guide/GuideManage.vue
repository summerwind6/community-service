<template>
    <div class="page-container">
        <el-card shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>办事指南管理</span>
                </div>
            </template>

            <div class="search-form">
                <el-form :inline="true" :model="searchForm">
                    <el-form-item label="关键词">
                        <el-input v-model="searchForm.keyword" placeholder="标题关键词" clearable />
                    </el-form-item>
                    <el-form-item label="分类">
                        <el-select v-model="searchForm.category" placeholder="全部" clearable style="width:160px">
                            <el-option label="证件办理" value="证件办理" />
                            <el-option label="救助帮扶" value="救助帮扶" />
                            <el-option label="养老服务" value="养老服务" />
                            <el-option label="计划生育" value="计划生育" />
                            <el-option label="就业服务" value="就业服务" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:120px">
                            <el-option label="草稿" :value="0" />
                            <el-option label="发布" :value="1" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <div style="margin-bottom: 16px;">
                <el-button type="primary" icon="Plus" @click="openDialog(null)">新增指南</el-button>
            </div>

            <el-table :data="tableData" border stripe v-loading="loading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="标题" min-width="220" />
                <el-table-column prop="category" label="分类" width="140" />
                <el-table-column prop="serviceItemId" label="服务事项ID" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 1 ? 'success' : 'info'">
                            {{ row.status === 1 ? '发布' : '草稿' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column label="操作" width="180" fixed="right">
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
            :title="isEdit ? '编辑办事指南' : '新增办事指南'"
            width="900px"
            :close-on-click-modal="false"
            @closed="resetForm"
        >
            <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="form.title" placeholder="请输入标题" />
                </el-form-item>
                <el-form-item label="分类" prop="category">
                    <el-select v-model="form.category" placeholder="请选择分类" style="width:100%">
                        <el-option label="证件办理" value="证件办理" />
                        <el-option label="救助帮扶" value="救助帮扶" />
                        <el-option label="养老服务" value="养老服务" />
                        <el-option label="计划生育" value="计划生育" />
                        <el-option label="就业服务" value="就业服务" />
                    </el-select>
                </el-form-item>
                <el-form-item label="关联服务事项ID" prop="serviceItemId">
                    <el-input-number v-model="form.serviceItemId" :min="1" style="width:220px" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="form.status">
                        <el-radio :value="0">草稿</el-radio>
                        <el-radio :value="1">发布</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <div class="editor-wrap">
                        <Toolbar
                            :editor="editorRef"
                            :defaultConfig="toolbarConfig"
                            mode="default"
                            class="editor-toolbar"
                        />
                        <Editor
                            v-model="form.content"
                            :defaultConfig="editorConfig"
                            mode="default"
                            class="editor-content"
                            @onCreated="handleCreated"
                        />
                    </div>
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
import { onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addGuide, deleteGuide, getGuideList, updateGuide } from '@/api/guide/guide'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
    keyword: '',
    category: '',
    status: null
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = {
    placeholder: '请输入办事指南内容，支持富文本编辑',
    MENU_CONF: {
        uploadImage: {
            server: '/api/upload',
            fieldName: 'file'
        }
    }
}

const defaultForm = {
    id: null,
    title: '',
    content: '',
    serviceItemId: null,
    category: '',
    status: 0
}
const form = reactive({ ...defaultForm })

const rules = {
    title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    category: [{ required: true, message: '请选择分类', trigger: 'change' }],
    content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

function handleCreated(editor) {
    editorRef.value = editor
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
            await updateGuide({ ...form })
            ElMessage.success('编辑成功')
        } else {
            await addGuide({ ...form })
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

function handleDelete(row) {
    ElMessageBox.confirm(`确定删除「${row.title}」吗？删除后不可恢复`, '确认删除', {
        type: 'warning'
    }).then(async () => {
        await deleteGuide(row.id)
        ElMessage.success('删除成功')
        loadData()
    }).catch(() => {})
}

async function loadData() {
    loading.value = true
    try {
        const res = await getGuideList({
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
    searchForm.category = ''
    searchForm.status = null
    pageNum.value = 1
    loadData()
}

onMounted(() => {
    loadData()
})

onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()
})
</script>

<style scoped lang="scss">
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.editor-wrap {
    width: 100%;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    overflow: hidden;
}

.editor-toolbar {
    border-bottom: 1px solid #dcdfe6;
}

.editor-content {
    height: 320px;
    overflow-y: hidden;
}
</style>
