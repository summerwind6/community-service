import request from '@/utils/request'

export function getResidentList(params) {
  return request({
    url: '/resident/list',
    method: 'get',
    params
  })
}

export function getResidentDetail(id) {
  return request({
    url: `/resident/${id}`,
    method: 'get'
  })
}

export function addResident(data) {
  return request({
    url: '/resident',
    method: 'post',
    data
  })
}

export function updateResident(data) {
  return request({
    url: '/resident',
    method: 'put',
    data
  })
}

export function deleteResident(id) {
  return request({
    url: `/resident/${id}`,
    method: 'delete'
  })
}

export function getResidentByPhone(phone) {
  return request({
    url: '/resident/by-phone',
    method: 'get',
    params: { phone }
  })
}
