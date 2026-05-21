import request from '@/utils/request'

export function submitApply(data) {
  return request({
    url: '/apply',
    method: 'post',
    data
  })
}

export function getApplyList(params) {
  return request({
    url: '/apply/list',
    method: 'get',
    params
  })
}

export function getApplyDetail(id) {
  return request({
    url: `/apply/${id}`,
    method: 'get'
  })
}

export function getAuditRecords(id) {
  return request({
    url: `/apply/${id}/audit-records`,
    method: 'get'
  })
}

/** 初审（社区受理员） */
export function reviewApply(id, data) {
  return request({
    url: `/apply/${id}/review`,
    method: 'post',
    data
  })
}

/** 复审（街道审核员） */
export function approveApply(id, data) {
  return request({
    url: `/apply/${id}/approve`,
    method: 'post',
    data
  })
}
