<template>
  <div class="page-container resident-apply-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>居民办事入口</span>
          <el-steps :active="activeStep" finish-status="success" simple style="width: 420px">
            <el-step title="填表" />
            <el-step title="查进度" />
            <el-step title="评价" />
          </el-steps>
        </div>
      </template>

      <el-tabs v-model="activeStepName" type="border-card">
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

            <el-descriptions v-if="selectedItem" :column="2" border size="small" style="margin-top: 12px">
              <el-descriptions-item label="服务类别">{{ selectedItem.category }}</el-descriptions-item>
              <el-descriptions-item label="承诺天数">{{ selectedItem.processingDays || 7 }} 天</el-descriptions-item>
              <el-descriptions-item label="费用">{{ selectedItem.fee > 0 ? `¥${selectedItem.fee}` : '免费' }}</el-descriptions-item>
              <el-descriptions-item label="所需材料" :span="2">{{ selectedItem.requiredDocs || '无特殊要求' }}</el-descriptions-item>
            </el-descriptions>

            <div class="form-actions">
              <el-button type="primary" :loading="submitLoading" @click="submitApplyForm">提交申请</el-button>
            </div>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="Step2 查进度" name="progress">
          <el-form :inline="true" :model="progressForm">
            <el-form-item label="手机号">
              <el-input v-model="progressForm.phone" placeholder="请输入手机号" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadProgress">查询进度</el-button>
            </el-form-item>
          </el-form>

          <el-empty v-if="progressList.length === 0" description="暂无申请记录" />

          <div v-else class="progress-list">
            <el-card v-for="item in progressList" :key="item.id" class="progress-card" shadow="never">
              <div class="progress-header">
                <div>
                  <strong>{{ item.applyNo }}</strong>
                  <span class="muted">{{ item.serviceItemName }}</span>
                </div>
                <el-tag :type="formatStatus(item.status).type">{{ formatStatus(item.status).text }}</el-tag>
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
                <el-timeline-item v-if="item.status === 'COMPLETED'" timestamp="办结" color="#F56C6C">
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
                  {{ item.evaluated ? '已评价' : '评价' }}
                </el-button>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="Step3 评价" name="evaluation">
          <el-alert title="请先在“查进度”中选择已办结申请进行评价" type="info" show-icon :closable="false" />

          <el-card v-if="selectedApply" class="eval-card" shadow="never">
            <div class="eval-header">
              <span>评价申请：{{ selectedApply.applyNo }}</span>
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
              <el-button type="primary" :loading="evaluateLoading" @click="submitEvaluationForm">提交评价</el-button>
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getEnabledItems } from '@/api/item/item'
import { submitPublicApply, getApplyProgress } from '@/api/apply/public'
import { uploadFile } from '@/api/upload/upload'
import { submitEvaluation } from '@/api/evaluation/evaluation'
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

const applyFormRef = ref()
const applyForm = reactive({
  residentName: '',
  phone: '',
  serviceItemId: null,
  applyContent: '',
  attachments: []
})

const applyRules = {
  residentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  serviceItemId: [{ required: true, message: '请选择服务事项', trigger: 'change' }],
  applyContent: [{ required: true, message: '请填写申请内容', trigger: 'blur' }]
}

const progressForm = reactive({ phone: '' })
const evaluationForm = reactive({ score: 5, content: '' })

async function loadItems() {
  const res = await getEnabledItems()
  itemList.value = res.data || []
}

function handleItemChange(id) {
  selectedItem.value = itemList.value.find(item => item.id === id) || null
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
  await loadItems()
})
</script>

<style scoped lang="scss">
.resident-apply-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
  }

  .apply-form {
    max-width: 920px;
  }

  .form-actions {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }

  .progress-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .progress-card {
    .progress-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .muted {
        margin-left: 12px;
        color: #909399;
      }
    }

    .card-actions {
      margin-top: 12px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .eval-card {
    margin-top: 16px;

    .eval-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
    }
  }
}
</style>
