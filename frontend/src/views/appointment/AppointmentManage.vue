<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <span>预约管理</span>
          <el-button-group>
            <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'">列表视图</el-button>
            <el-button :type="viewMode === 'calendar' ? 'primary' : ''" @click="viewMode = 'calendar'">日历视图</el-button>
          </el-button-group>
        </div>
      </template>

      <div class="search-form" v-if="viewMode === 'list'">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="关键词">
            <el-input v-model="searchForm.keyword" placeholder="姓名/电话" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待确认" value="PENDING" />
              <el-option label="已确认" value="CONFIRMED" />
              <el-option label="已取消" value="CANCELED" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
          </el-form-item>
          <el-form-item label="预约日期">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="viewMode === 'list'" style="margin-bottom:16px;">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增预约</el-button>
      </div>

      <el-table v-if="viewMode === 'list'" :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="residentName" label="预约人" width="100" />
        <el-table-column prop="residentPhone" label="电话" width="130" />
        <el-table-column prop="serviceItemName" label="服务事项" min-width="140" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="80">
          <template #default="{ row }">
            {{ row.timeSlot === 'AM' ? '上午' : '下午' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="success" link size="small" @click="handleConfirm(row)">确认</el-button>
            <el-button v-if="row.status === 'PENDING'" type="danger" link size="small" @click="handleCancel(row)">取消</el-button>
            <el-button v-if="row.status === 'CONFIRMED'" type="primary" link size="small" @click="handleComplete(row)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-calendar v-if="viewMode === 'calendar'" v-model="calendarDate">
        <template #date-cell="{ data }">
          <div class="calendar-day" @click="handleCalendarDayClick(data)">
            <span>{{ data.day.split('-').pop() }}</span>
            <el-tag v-for="a in getAppointmentsByDate(data.day)" :key="a.id" size="small" :type="getStatusType(a.status)" style="margin-top:2px;">
              {{ a.residentName }}-{{ a.timeSlot === 'AM' ? '上午' : '下午' }}
            </el-tag>
          </div>
        </template>
      </el-calendar>

      <div v-if="viewMode === 'list'" class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增预约" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="预约人姓名" prop="residentName">
          <el-input v-model="form.residentName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="预约人电话" prop="residentPhone">
          <el-input v-model="form.residentPhone" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="服务事项" prop="serviceItemId">
          <el-select v-model="form.serviceItemId" placeholder="请选择" style="width:100%;">
            <el-option v-for="item in serviceItems" :key="item.id" :label="item.itemName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker v-model="form.appointmentDate" type="date" placeholder="选择日期" style="width:100%;" />
        </el-form-item>
        <el-form-item label="时间段">
          <el-radio-group v-model="form.timeSlot">
            <el-radio label="AM">上午</el-radio>
            <el-radio label="PM">下午</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getAppointmentList, addAppointment, confirmAppointment, cancelAppointment, completeAppointment } from '@/api/appointment/appointment'
import { getEnabledItems } from '@/api/item/item'

const viewMode = ref('list')
const loading = ref(false)
const tableData = ref([])
const allAppointments = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const calendarDate = ref(new Date())
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const serviceItems = ref([])

const searchForm = reactive({ keyword: '', status: '', dateRange: null })

const form = reactive({
  residentName: '',
  residentPhone: '',
  serviceItemId: null,
  appointmentDate: null,
  timeSlot: 'AM',
  remark: ''
})

const rules = {
  residentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  residentPhone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  serviceItemId: [{ required: true, message: '请选择服务事项', trigger: 'change' }],
  appointmentDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

function getStatusType(status) {
  const map = { PENDING: 'warning', CONFIRMED: 'success', CANCELED: 'info', COMPLETED: 'primary' }
  return map[status] || 'info'
}

function getStatusText(status) {
  const map = { PENDING: '待确认', CONFIRMED: '已确认', CANCELED: '已取消', COMPLETED: '已完成' }
  return map[status] || status
}

function getAppointmentsByDate(day) {
  return allAppointments.value.filter(a => a.appointmentDate === day)
}

async function loadData() {
  loading.value = true
  try {
    const [listRes, allRes] = await Promise.all([
      getAppointmentList({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: searchForm.keyword || undefined,
        status: searchForm.status || undefined,
        startDate: searchForm.dateRange?.[0] || undefined,
        endDate: searchForm.dateRange?.[1] || undefined
      }),
      getAppointmentList({ pageNum: 1, pageSize: 1000 })
    ])
    tableData.value = listRes.data?.records || []
    total.value = listRes.data?.total || 0
    allAppointments.value = allRes.data?.records || []
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function handleSearch() { pageNum.value = 1; loadData() }
function resetSearch() { searchForm.keyword = ''; searchForm.status = ''; searchForm.dateRange = null; pageNum.value = 1; loadData() }

async function handleAdd() {
  try {
    const res = await getEnabledItems()
    serviceItems.value = res.data || []
  } catch {
    serviceItems.value = []
  }
  form.residentName = ''
  form.residentPhone = ''
  form.serviceItemId = null
  form.appointmentDate = null
  form.timeSlot = 'AM'
  form.remark = ''
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const item = serviceItems.value.find(i => i.id === form.serviceItemId)
      await addAppointment({
        residentName: form.residentName,
        residentPhone: form.residentPhone,
        serviceItemId: form.serviceItemId,
        serviceItemName: item?.itemName || '',
        appointmentDate: form.appointmentDate,
        timeSlot: form.timeSlot,
        remark: form.remark
      })
      dialogVisible.value = false
      loadData()
    } catch {
      // handled by interceptor
    } finally {
      submitting.value = false
    }
  })
}

async function handleConfirm(row) { await confirmAppointment(row.id); loadData() }
async function handleCancel(row) { await cancelAppointment(row.id); loadData() }
async function handleComplete(row) { await completeAppointment(row.id); loadData() }

function handleCalendarDayClick(data) {
  viewMode.value = 'list'
  searchForm.keyword = ''
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.calendar-day {
  min-height: 60px;
  cursor: pointer;
}
</style>
