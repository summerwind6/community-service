<template>
  <div class="page-container resident-apply-page">
    <!-- 页面标题栏 -->
    <div class="page-banner">
      <div class="banner-content">
        <h2>居民办事大厅</h2>
        <p>在线申请、进度查询、服务评价，一站式办理</p>
      </div>
      <el-steps :active="activeStep" simple class="banner-steps">
        <el-step title="填表" />
        <el-step title="查进度" />
        <el-step title="评价" />
      </el-steps>
    </div>

    <!-- 指南区：独立卡片 -->
    <el-card shadow="hover" class="guide-section-card">
      <template #header>
        <div class="section-header">
          <div>
            <div class="section-title">
              <el-icon style="margin-right: 6px; vertical-align: -2px"><Reading /></el-icon>
              公开办事指南
            </div>
            <div class="section-subtitle">先了解办理材料、承诺时限和事项说明，再开始申请</div>
          </div>
          <el-button type="primary" plain @click="scrollToApply">去填表 ↓</el-button>
        </div>
      </template>

      <el-skeleton :loading="guideLoading" animated :rows="4">
        <div v-if="groupedGuides.length === 0" class="empty-box">
          <el-empty description="暂无公开办事指南" />
        </div>

        <el-collapse v-else v-model="guideActiveNames" accordion>
          <el-collapse-item
            v-for="group in groupedGuides"
            :key="group.groupKey"
            :name="group.groupKey"
          >
            <template #title>
              <div class="guide-collapse-title">
                <span>{{ group.groupTitle }}</span>
                <el-tag size="small" type="info">{{ group.guides.length }} 篇</el-tag>
              </div>
            </template>

            <div class="guide-list">
              <div v-for="guide in group.guides" :key="guide.id" class="guide-card">
                <div class="guide-card-header">
                  <div>
                    <div class="guide-title">{{ guide.title }}</div>
                    <div class="guide-meta">
                      <el-tag size="small">{{ guide.category || '未分类' }}</el-tag>
                      <span>承办事项：{{ getItemName(guide.serviceItemId) }}</span>
                    </div>
                  </div>
                </div>
                <div class="guide-content-separator"></div>
                <div class="guide-content" v-html="guide.content"></div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-skeleton>
    </el-card>

    <!-- 办事区：独立卡片 -->
    <el-card shadow="hover" class="apply-section-card">
      <el-tabs v-model="activeStepName" type="border-card" class="apply-tabs">
        <el-tab-pane label="Step1 填表" name="apply">
          <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-width="100px" class="apply-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名" prop="residentName">
                  <el-input v-model="applyForm.residentName" placeholder="请输入姓名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="applyForm.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="applyForm.gender" placeholder="请选择性别" style="width: 100%">
                    <el-option label="男" value="M" />
                    <el-option label="女" value="F" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="服务事项" prop="serviceItemId">
              <el-select v-model="applyForm.serviceItemId" placeholder="请选择服务事项" style="width: 100%" @change="handleItemChange">
                <el-option
                  v-for="item in itemList"
                  :key="item.id"
                  :label="`${item.itemName}（${item.processingDays || 7}天办结）`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>

            <el-descriptions v-if="selectedItem" :column="2" border size="small" class="item-info">
              <el-descriptions-item label="服务类别">{{ selectedItem.category }}</el-descriptions-item>
              <el-descriptions-item label="承诺天数">{{ selectedItem.processingDays || 7 }} 天</el-descriptions-item>
              <el-descriptions-item label="费用">{{ selectedItem.fee > 0 ? `¥${selectedItem.fee}` : '免费' }}</el-descriptions-item>
              <el-descriptions-item label="所需材料" :span="2">{{ selectedItem.requiredDocs || '无特殊要求' }}</el-descriptions-item>
            </el-descriptions>

            <el-form-item label="申请内容" prop="applyContent">
              <el-input v-model="applyForm.applyContent" type="textarea" :rows="5" placeholder="请描述您的申请内容" />
            </el-form-item>

            <el-form-item label="上传附件">
              <el-upload
                :http-request="handleUpload"
                :file-list="fileList"
                :limit="5"
                multiple
                list-type="text"
                :on-remove="handleRemoveFile"
              >
                <el-button type="primary">选择附件</el-button>
                <template #tip>
                  <div class="el-upload__tip">支持图片、PDF、文档等文件，最多 5 个</div>
                </template>
              </el-upload>
            </el-form-item>

            <div class="form-actions">
              <el-button type="primary" size="large" :loading="submitLoading" @click="submitApplyForm">提交申请</el-button>
            </div>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="Step2 查进度" name="progress">
          <el-form :inline="true" :model="progressForm" class="progress-search">
            <el-form-item label="手机号">
              <el-input v-model="progressForm.phone" placeholder="请输入手机号" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadProgress">查询进度</el-button>
            </el-form-item>
          </el-form>

          <el-empty v-if="progressList.length === 0" description="暂无申请记录" />

          <div v-else class="progress-list">
            <div
              v-for="item in progressList"
              :key="item.id"
              class="progress-card"
              :class="'status-' + item.status"
            >
              <div class="progress-header">
                <div>
                  <strong class="apply-no">{{ item.applyNo }}</strong>
                  <span class="muted">{{ item.serviceItemName }}</span>
                </div>
                <el-tag :type="formatStatus(item.status).type" effect="dark" size="small">
                  {{ formatStatus(item.status).text }}
                </el-tag>
              </div>
              <el-timeline>
                <el-timeline-item timestamp="提交申请" color="#409EFF">
                  {{ item.createTime }}
                </el-timeline-item>
                <el-timeline-item timestamp="申请内容" color="#67C23A">
                  {{ item.applyContent || '-' }}
                </el-timeline-item>
                <el-timeline-item v-if="item.remark" timestamp="备注" color="#E6A23C">
                  {{ item.remark }}
                </el-timeline-item>
                <el-timeline-item v-if="item.status === 'COMPLETED'" timestamp="办结" color="#67C23A">
                  已办结，可前往评价
                </el-timeline-item>
              </el-timeline>

              <div class="card-actions">
                <el-button
                  type="primary"
                  plain
                  size="small"
                  @click="openEvaluation(item)"
                  :disabled="item.status !== 'COMPLETED' || item.evaluated"
                >
                  {{ item.evaluated ? '已评价' : '去评价' }}
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="Step3 评价" name="evaluation">
          <el-alert title="请先在「查进度」中选择已办结申请进行评价" type="info" show-icon :closable="false" />

          <div v-if="selectedApply" class="eval-card">
            <div class="eval-header">
              <span>评价申请：<strong>{{ selectedApply.applyNo }}</strong></span>
              <el-tag type="success">{{ selectedApply.serviceItemName }}</el-tag>
            </div>
            <el-form :model="evaluationForm" label-width="90px">
              <el-form-item label="星级评分">
                <el-rate v-model="evaluationForm.score" show-text :texts="['差', '较差', '一般', '满意', '非常满意']" />
              </el-form-item>
              <el-form-item label="评价内容">
                <el-input v-model="evaluationForm.content" type="textarea" :rows="4" placeholder="请输入评价内容" />
              </el-form-item>
            </el-form>
            <div class="form-actions">
              <el-button type="primary" size="large" :loading="evaluateLoading" @click="submitEvaluationForm">提交评价</el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'
import { getEnabledItems } from '@/api/item/item'
import { submitPublicApply, getApplyProgress } from '@/api/apply/public'
import { uploadFile } from '@/api/upload/upload'
import { submitEvaluation } from '@/api/evaluation/evaluation'
import { getPublicGuides } from '@/api/guide/guide'
import { formatStatus } from '@/utils/common'

const activeStepName = ref('apply')
const activeStep = computed(() => ({ apply: 0, progress: 1, evaluation: 2 }[activeStepName.value]))
const itemList = ref([])
const selectedItem = ref(null)
const submitLoading = ref(false)
const evaluateLoading = ref(false)
const fileList = ref([])
const uploadedPaths = ref([])
const progressList = ref([])
const selectedApply = ref(null)
const guideLoading = ref(false)
const guideList = ref([])
const guideActiveNames = ref('')

const applyFormRef = ref()
const applyForm = reactive({
  residentName: '',
  phone: '',
  gender: '',
  serviceItemId: null,
  applyContent: '',
  attachments: []
})

const applyRules = {
  residentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  serviceItemId: [{ required: true, message: '请选择服务事项', trigger: 'change' }],
  applyContent: [{ required: true, message: '请填写申请内容', trigger: 'blur' }]
}

const progressForm = reactive({ phone: '' })
const evaluationForm = reactive({ score: 5, content: '' })

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

async function loadItems() {
  const res = await getEnabledItems()
  itemList.value = res.data || []
}

async function loadPublicGuides() {
  guideLoading.value = true
  try {
    const res = await getPublicGuides()
    guideList.value = res.data || []
    guideActiveNames.value = groupedGuides.value[0]?.groupKey || ''
  } catch {
    // handled by interceptor
  } finally {
    guideLoading.value = false
  }
}

function handleItemChange(id) {
  selectedItem.value = itemList.value.find(item => item.id === id) || null
}

function scrollToApply() {
  activeStepName.value = 'apply'
  document.querySelector('.apply-tabs')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function normalizeGender(gender) {
  if (gender === '男') return 'M'
  if (gender === '女') return 'F'
  return gender || ''
}

async function handleUpload(options) {
  try {
    const res = await uploadFile(options.file)
    const path = res.data?.path || res.data?.url || res.data
    if (path) {
      uploadedPaths.value.push(path)
      applyForm.attachments = [...uploadedPaths.value]
      fileList.value.push({ name: options.file.name, url: path })
      options.onSuccess(res)
    } else {
      options.onError(new Error('上传失败'))
    }
  } catch (error) {
    options.onError(error)
  }
}

function handleRemoveFile(file) {
  uploadedPaths.value = uploadedPaths.value.filter(p => p !== file.url)
  applyForm.attachments = [...uploadedPaths.value]
}

async function submitApplyForm() {
  try {
    await applyFormRef.value.validate()
  } catch {
    return
  }
  submitLoading.value = true
  try {
    const payload = {
      residentName: applyForm.residentName,
      phone: applyForm.phone,
      gender: normalizeGender(applyForm.gender),
      serviceItemId: applyForm.serviceItemId,
      applyContent: applyForm.applyContent,
      attachments: JSON.stringify(applyForm.attachments),
      fee: selectedItem.value?.fee || 0
    }
    await submitPublicApply(payload)
    ElMessage.success('申请提交成功')
    activeStepName.value = 'progress'
    progressForm.phone = applyForm.phone
    await loadProgress()
  } catch {
    // handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

async function loadProgress() {
  if (!progressForm.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  const res = await getApplyProgress({ phone: progressForm.phone })
  progressList.value = res.data || []
}

function openEvaluation(item) {
  selectedApply.value = item
  evaluationForm.score = 5
  evaluationForm.content = ''
  activeStepName.value = 'evaluation'
}

async function submitEvaluationForm() {
  if (!selectedApply.value) {
    ElMessage.warning('请先选择已办结申请')
    return
  }
  if (!evaluationForm.score) {
    ElMessage.warning('请选择评分')
    return
  }
  evaluateLoading.value = true
  try {
    await submitEvaluation({
      applicationId: selectedApply.value.id,
      score: evaluationForm.score,
      content: evaluationForm.content
    })
    if (selectedApply.value) {
      selectedApply.value.evaluated = true
      const idx = progressList.value.findIndex(item => item.id === selectedApply.value.id)
      if (idx !== -1) {
        progressList.value[idx].evaluated = true
      }
    }
    ElMessage.success('已评价')
    activeStepName.value = 'progress'
  } catch {
    // handled by interceptor
  } finally {
    evaluateLoading.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    loadItems(),
    loadPublicGuides()
  ])
})
</script>

<style scoped lang="scss">
.resident-apply-page {

  /* ── 顶部横幅 ── */
  .page-banner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 28px;
    margin-bottom: 16px;
    background: linear-gradient(135deg, #304156 0%, #3b5998 100%);
    border-radius: 8px;
    color: #fff;

    h2 {
      font-size: 22px;
      font-weight: 700;
      margin: 0 0 4px;
    }

    p {
      margin: 0;
      font-size: 13px;
      opacity: 0.8;
    }

    .banner-steps {
      max-width: 360px;
      :deep(.el-step__title) {
        color: rgba(255, 255, 255, 0.85);
        font-weight: 500;
      }
      :deep(.el-step__head.is-finish .el-step__title),
      :deep(.el-step__head.is-process .el-step__title) {
        color: #fff;
        font-weight: 700;
      }
      :deep(.el-step__head.is-finish .el-step__line),
      :deep(.el-step__head.is-wait .el-step__line) {
        background: rgba(255, 255, 255, 0.3);
      }
      :deep(.el-step__head.is-finish .el-step__icon),
      :deep(.el-step__head.is-process .el-step__icon) {
        background: #409EFF;
        border-color: #409EFF;
        color: #fff;
      }
      :deep(.el-step__head.is-wait .el-step__icon) {
        background: rgba(255, 255, 255, 0.25);
        border-color: rgba(255, 255, 255, 0.5);
        color: rgba(255, 255, 255, 0.9);
      }
    }
  }

  /* ── 指南区卡片 ── */
  .guide-section-card {
    margin-bottom: 16px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;
    }

    .section-title {
      font-size: 16px;
      font-weight: 700;
      color: #303133;
    }

    .section-subtitle {
      margin-top: 2px;
      font-size: 13px;
      color: #909399;
    }

    .guide-collapse-title {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-right: 16px;
      font-weight: 600;
    }

    .guide-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
      padding-top: 8px;
    }

    .guide-card {
      padding: 20px 24px;
      border: 1px solid #ebeef5;
      border-radius: 6px;
      background: #fafbfc;
      border-left: 3px solid #409EFF;

      .guide-card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 16px;
        margin-bottom: 0;
      }

      .guide-title {
        font-size: 15px;
        font-weight: 600;
        margin-bottom: 6px;
        color: #303133;
      }

      .guide-meta {
        display: flex;
        gap: 12px;
        flex-wrap: wrap;
        align-items: center;
        color: #909399;
        font-size: 13px;
      }

      .guide-content-separator {
        height: 1px;
        background: #e8eaed;
        margin: 14px 0;
      }

      .guide-content {
        line-height: 1.8;
        color: #303133;
        white-space: normal;
        word-break: break-word;
        max-width: 800px;
        font-size: 14px;
      }
    }
  }

  .empty-box {
    padding: 20px 0;
  }

  /* ── 办事区卡片 ── */
  .apply-section-card {
    :deep(.el-card__body) {
      padding: 0;
    }
  }

  .apply-tabs {
    border: none;
    box-shadow: none;
  }

  .apply-form {
    max-width: 920px;
    padding: 8px 0;
  }

  .item-info {
    margin-top: 4px;
    margin-bottom: 18px;
  }

  .form-actions {
    margin-top: 24px;
    padding: 16px;
    background: #fafafa;
    border-radius: 6px;
    display: flex;
    justify-content: center;
  }

  /* ── 进度搜索 ── */
  .progress-search {
    margin-bottom: 16px;
  }

  /* ── 进度卡片列表 ── */
  .progress-list {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .progress-card {
    padding: 16px 20px;
    border: 1px solid #ebeef5;
    border-radius: 6px;
    background: #fff;
    transition: box-shadow 0.2s;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    }

    /* 左侧状态色条 */
    border-left: 4px solid #909399;

    &.status-PENDING    { border-left-color: #E6A23C; }
    &.status-PROCESSING { border-left-color: #409EFF; }
    &.status-APPROVED   { border-left-color: #67C23A; }
    &.status-REJECTED   { border-left-color: #F56C6C; }
    &.status-COMPLETED  { border-left-color: #67C23A; }

    .progress-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 14px;

      .apply-no {
        font-size: 15px;
        color: #303133;
      }

      .muted {
        margin-left: 12px;
        color: #909399;
        font-size: 13px;
      }
    }

    .card-actions {
      margin-top: 12px;
      padding-top: 10px;
      border-top: 1px solid #f0f0f0;
      display: flex;
      justify-content: flex-end;
    }
  }

  /* ── 评价区 ── */
  .eval-card {
    margin-top: 16px;
    padding: 20px;
    border: 1px solid #ebeef5;
    border-radius: 6px;
    background: #fafbfc;

    .eval-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      font-size: 15px;
    }
  }
}
</style>
