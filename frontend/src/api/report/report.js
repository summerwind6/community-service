import request from '@/utils/request'

export function exportApplyReport(params) {
  return request({
    url: '/report/export/apply',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function exportResidentReport() {
  return request({
    url: '/report/export/resident',
    method: 'get',
    responseType: 'blob'
  })
}

export function exportEvaluationReport() {
  return request({
    url: '/report/export/evaluation',
    method: 'get',
    responseType: 'blob'
  })
}
