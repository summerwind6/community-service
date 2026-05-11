import request from '@/utils/request'

export function submitApply(data) {
  return request({
    url: '/apply',
    method: 'post',
    data
  })
}

export function getMyApplyList() {
  return request({
    url: '/apply/my',
    method: 'get'
  })
}

export function getApplyList(params) {
  return request({
    url: '/apply/list',
    method: 'get',
    params
  })
}

export function auditApply(id, data) {
  return request({
    url: `/apply/audit/${id}`,
    method: 'put',
    data
  })
}
