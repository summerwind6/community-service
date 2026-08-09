import request from '@/utils/request'

export function getEvaluationList(params) {
  return request({
    url: '/evaluation/list',
    method: 'get',
    params
  })
}

export function submitEvaluation(data) {
  return request({
    url: '/evaluation',
    method: 'post',
    data
  })
}

export function submitPublicEvaluation(data) {
  return request({
    url: '/evaluation/public',
    method: 'post',
    data
  })
}

export function getEvaluationStats() {
  return request({
    url: '/evaluation/stats',
    method: 'get'
  })
}
