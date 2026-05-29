import request from '@/utils/request'

export function submitPublicApply(data) {
  return request({
    url: '/apply/public',
    method: 'post',
    data
  })
}

export function getApplyProgress(params) {
  return request({
    url: '/apply/progress',
    method: 'get',
    params
  })
}