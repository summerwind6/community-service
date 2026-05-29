<template>
    <div class="page-container">
        <el-card shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>办事指南公开浏览</span>
                </div>
            </template>

            <el-skeleton :loading="loading" animated :rows="6">
                <div v-if="groupedGuides.length === 0" class="empty-box">
                    <el-empty description="暂无公开办事指南" />
                </div>

                <el-collapse v-else accordion>
                    <el-collapse-item
                        v-for="group in groupedGuides"
                        :key="group.serviceItemId"
                        :title="group.groupTitle"
                        :name="String(group.serviceItemId)"
                    >
                        <div class="guide-list">
                            <el-card v-for="guide in group.guides" :key="guide.id" class="guide-card" shadow="never">
                                <div class="guide-title">{{ guide.title }}</div>
                                <div class="guide-meta">
                                    <el-tag size="small">{{ guide.category || '未分类' }}</el-tag>
                                    <span>服务事项ID：{{ guide.serviceItemId || '未关联' }}</span>
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

const loading = ref(false)
const guideList = ref([])

const groupedGuides = computed(() => {
    const map = new Map()
    guideList.value.forEach((item) => {
        const key = item.serviceItemId || 0
        if (!map.has(key)) {
            map.set(key, {
                serviceItemId: key,
                groupTitle: key === 0 ? '未关联服务事项' : `服务事项 ${key}`,
                guides: []
            })
        }
        map.get(key).guides.push(item)
    })
    return Array.from(map.values())
})

async function loadData() {
    loading.value = true
    try {
        const res = await getPublicGuides()
        guideList.value = res.data || []
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
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.guide-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.guide-card {
    border: 1px solid #ebeef5;
}

.guide-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 8px;
}

.guide-meta {
    display: flex;
    gap: 12px;
    align-items: center;
    color: #909399;
    font-size: 13px;
    margin-bottom: 12px;
}

.guide-content {
    line-height: 1.8;
    color: #303133;
    white-space: pre-wrap;
}

.empty-box {
    padding: 20px 0;
}
</style>
