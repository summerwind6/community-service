<template>
    <div class="page-container">
        <el-card shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>附件管理</span>
                    <el-input
                        v-model="searchKeyword"
                        placeholder="输入申请编号搜索"
                        clearable
                        style="width: 260px"
                        @clear="loadData"
                        @keyup.enter="loadData"
                    >
                        <template #append>
                            <el-button icon="Search" @click="loadData" />
                        </template>
                    </el-input>
                </div>
            </template>

            <el-skeleton :loading="loading" animated :rows="8">
                <el-empty v-if="groupedList.length === 0" description="暂无附件数据" />

                <el-collapse v-else accordion>
                    <el-collapse-item
                        v-for="group in groupedList"
                        :key="group.applicationId"
                        :name="String(group.applicationId)"
                    >
                        <template #title>
                            <div class="group-title">
                                <span>{{ group.applyNo }}</span>
                                <span class="group-meta">{{ group.residentName || '未知申请人' }} / {{ group.serviceItemName || '未填写事项' }}</span>
                                <el-tag size="small" type="info">{{ group.attachments.length }} 个附件</el-tag>
                            </div>
                        </template>

                        <div class="attachment-grid">
                            <el-card v-for="item in group.attachments" :key="item.filePath" class="attachment-card" shadow="never">
                                <div class="attachment-preview" @click="previewAttachment(item)">
                                    <el-image
                                        v-if="item.image"
                                        :src="resolveFileUrl(item.filePath)"
                                        fit="cover"
                                        class="thumb"
                                    />
                                    <div v-else class="file-box">
                                        <el-icon :size="36"><Document /></el-icon>
                                        <div class="file-name">{{ item.fileName }}</div>
                                    </div>
                                </div>
                                <div class="attachment-footer">
                                    <div class="verify-status">
                                        <el-tag :type="item.verifyStatus === 'PROBLEM' ? 'danger' : item.verifyStatus === 'CHECKED' ? 'success' : 'info'">
                                            {{ item.verifyStatus === 'PROBLEM' ? '有问题' : item.verifyStatus === 'CHECKED' ? '已核验' : '未标注' }}
                                        </el-tag>
                                    </div>
                                    <div class="actions">
                                        <el-button type="primary" link size="small" @click="markVerify(item, 'CHECKED')">已核验</el-button>
                                        <el-button type="warning" link size="small" @click="markVerify(item, 'PROBLEM')">有问题</el-button>
                                    </div>
                                </div>
                            </el-card>
                        </div>
                    </el-collapse-item>
                </el-collapse>
            </el-skeleton>
        </el-card>

        <el-dialog v-model="previewVisible" :title="previewTitle" width="720px">
            <div v-if="previewItem?.image" class="preview-image-box">
                <el-image :src="resolveFileUrl(previewItem.filePath)" fit="contain" />
            </div>
            <div v-else class="preview-file-box">
                <p>{{ previewItem?.fileName }}</p>
                <el-button type="primary" @click="downloadCurrentFile">下载文件</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAttachmentList } from '@/api/upload/upload'

const loading = ref(false)
const searchKeyword = ref('')
const attachmentRows = ref([])
const previewVisible = ref(false)
const previewItem = ref(null)
const previewTitle = ref('附件预览')

const groupedList = computed(() => {
    const map = new Map()
    attachmentRows.value.forEach((row) => {
        const key = row.applicationId
        if (!map.has(key)) {
            map.set(key, {
                applicationId: row.applicationId,
                applyNo: row.applyNo,
                residentName: row.residentName,
                serviceItemName: row.serviceItemName,
                attachments: []
            })
        }
        map.get(key).attachments.push(row)
    })
    return Array.from(map.values())
})

function resolveFileUrl(filePath) {
    if (!filePath) return ''
    if (filePath.startsWith('http')) return filePath
    return filePath.startsWith('/api') ? filePath : `/api${filePath}`
}

async function loadData() {
    loading.value = true
    try {
        const res = await getAttachmentList({ keyword: searchKeyword.value || undefined })
        attachmentRows.value = res.data || []
    } catch {
        // handled by interceptor
    } finally {
        loading.value = false
    }
}

function previewAttachment(item) {
    previewItem.value = item
    previewTitle.value = item.fileName
    previewVisible.value = true
}

function downloadCurrentFile() {
    if (!previewItem.value) return
    window.open(resolveFileUrl(previewItem.value.filePath), '_blank')
}

function markVerify(item, status) {
    item.verifyStatus = status
    ElMessage.success(status === 'CHECKED' ? '已标记为已核验' : '已标记为有问题')
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

.group-title {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;
}

.group-meta {
    color: #909399;
    font-size: 13px;
}

.attachment-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 16px;
}

.attachment-card {
    cursor: pointer;
}

.attachment-preview {
    height: 180px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #ebeef5;
    border-radius: 6px;
    overflow: hidden;
    background: #fafafa;
}

.thumb {
    width: 100%;
    height: 100%;
}

.file-box {
    text-align: center;
    color: #606266;
}

.file-name {
    margin-top: 8px;
    word-break: break-all;
}

.attachment-footer {
    margin-top: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
}

.preview-image-box {
    display: flex;
    justify-content: center;
}

.preview-image-box :deep(.el-image) {
    max-width: 100%;
    max-height: 70vh;
}

.preview-file-box {
    text-align: center;
}
</style>
