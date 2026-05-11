import dayjs from 'dayjs'

export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  return dayjs(date).format(format)
}

export function formatStatus(status) {
  const statusMap = {
    PENDING: { text: '待受理', type: 'warning' },
    PROCESSING: { text: '处理中', type: '' },
    APPROVED: { text: '已通过', type: 'success' },
    REJECTED: { text: '已驳回', type: 'danger' },
    COMPLETED: { text: '已完成', type: 'info' }
  }
  return statusMap[status] || { text: status, type: 'info' }
}

export function debounce(func, delay = 300) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      func.apply(this, args)
    }, delay)
  }
}
