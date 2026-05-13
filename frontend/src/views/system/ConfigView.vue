<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header><span>系统配置</span></template>
      <el-form :model="formData" label-width="140px" style="max-width: 600px;">
        <el-form-item v-for="item in configList" :key="item.id" :label="item.description">
          <template v-if="item.configKey === 'notice_enabled'">
            <el-switch v-model="formData[item.configKey]" active-text="启用" inactive-text="关闭" />
          </template>
          <template v-else-if="item.configKey === 'processing_deadline_days'">
            <el-input-number v-model="formData[item.configKey]" :min="1" :max="30" /> 天
          </template>
          <template v-else>
            <el-input v-model="formData[item.configKey]" />
          </template>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getConfigList, updateConfig } from '@/api/system/config'
import { ElMessage } from 'element-plus'

const configList = ref([])
const formData = reactive({})
const saving = ref(false)

async function loadConfig() {
  try {
    const res = await getConfigList()
    configList.value = res.data || []
    configList.value.forEach(item => {
      formData[item.configKey] = item.configValue
    })
  } catch {
    // handled by interceptor
  }
}

async function handleSave() {
  saving.value = true
  try {
    const data = configList.value.map(item => ({
      id: item.id,
      configValue: formData[item.configKey]
    }))
    await updateConfig(data)
    ElMessage.success('配置保存成功')
  } catch {
    // handled by interceptor
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>
