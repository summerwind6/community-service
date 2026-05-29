<template>
    <div class="page-container guide-public-page">
        <el-card shadow="hover">
            <template #header>
                <div class="card-header">
                    <div>
                        <div class="page-title">办事指南公开浏览</div>
                        <div class="page-subtitle">居民无需登录即可查看各服务事项的办理说明与材料要求</div>
                    </div>
                    <el-tag type="success" effect="dark">共 {{ guideList.length }} 篇指南</el-tag>
                </div>
            </template>

            <el-skeleton :loading="loading" animated :rows="6">
                <div v-if="groupedGuides.length === 0" class="empty-box">
                    <el-empty description="暂无公开办事指南" />
                </div>

                <el-collapse v-else v-model="activeNames" accordion>
                    <el-collapse-item
                        v-for="group in groupedGuides"
                        :key="group.groupKey"
                        :title="group.groupTitle"
                        :name="group.groupKey"
                    >
                        <template #title>
                            <div class="collapse-title">
                                <span class="collapse-title-text">{{ group.groupTitle }}</span>
                                <el-tag size="small" type="info">{{ group.guides.length }} 篇</el-tag>
                            </div>
                        </template>

                        <div class="guide-list">
                            <el-card v-for="guide in group.guides" :key="guide.id" class="guide-card" shadow="never">
                                <div class="guide-card-header">
                                    <div>
                                        <div class="guide-title">{{ guide.title }}</div>
                                        <div class="guide-meta">
                                            <el-tag size="small">{{ guide.category || '未分类' }}</el-tag>
                                            <span>服务事项：{{ getItemName(guide.serviceItemId) }}</span>
                                        </div>
                                    </div>
                                    <el-tag :type="guide.status === 1 ? 'success' : 'info'">{{ guide.status === 1 ? '发布' : '草稿' }}</el-tag>
                                </div>

                                <div class="guide-content" v-html="guide.content"></div>
                            </el-card>
                        </div>
                    </el-collapse-item>
                </el-collapse>
            </el-skeleton>
        </el-card>
    </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getPublicGuides } from '@/api/guide/guide'
import { getEnabledItems } from '@/api/item/item'

const loading = ref(false)
const guideList = ref([])
const itemList = ref([])
const activeNames = ref('')

const itemNameMap = computed(() => {
    const map = new Map()
    itemList.value.forEach(item => {
        map.set(item.id, item.itemName)
    })
    return map
})

const groupedGuides = computed(() => {
    const map = new Map()
    guideList.value.forEach((item) => {
        const key = item.serviceItemId || 0
        if (!map.has(key)) {
            map.set(key, {
                groupKey: String(key),
                serviceItemId: key,
                groupTitle: key === 0 ? '未关联服务事项' : getItemName(key),
                guides: []
            })
        }
        map.get(key).guides.push(item)
    })
    return Array.from(map.values())
})

function getItemName(serviceItemId) {
    if (!serviceItemId) return '未关联服务事项'
    return itemNameMap.value.get(serviceItemId) || `服务事项 ${serviceItemId}`
}

async function loadData() {
    loading.value = true
    try {
        const [guideRes, itemRes] = await Promise.all([
            getPublicGuides(),
            getEnabledItems()
        ])
        guideList.value = guideRes.data || []
        itemList.value = itemRes.data || []
        activeNames.value = groupedGuides.value[0]?.groupKey || ''
    } catch {
        // handled by interceptor
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    loadData()
})
</script>

<style scoped lang="scss">
.guide-public-page {
    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
    }

    .page-title {
        font-size: 20px;
        font-weight: 700;
        color: #303133;
    }

    .page-subtitle {
        margin-top: 4px;
        color: #909399;
        font-size: 13px;
    }

    .collapse-title {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding-right: 16px;
    }

    .collapse-title-text {
        font-weight: 600;
    }

    .guide-list {
        display: flex;
        flex-direction: column;
        gap: 16px;
        padding-top: 8px;
    }

    .guide-card {
        border: 1px solid #ebeef5;
    }

    .guide-card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
        margin-bottom: 12px;
    }

    .guide-title {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 8px;
    }

    .guide-meta {
        display: flex;
        gap: 12px;
        flex-wrap: wrap;
        align-items: center;
        color: #909399;
        font-size: 13px;
    }

    .guide-content {
        line-height: 1.8;
        color: #303133;
        white-space: normal;
        word-break: break-word;
    }

    .empty-box {
        padding: 20px 0;
    }
}
</style>
