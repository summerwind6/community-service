import request from '@/utils/request'

export function getAppointmentList(params) {
  return request({
    url: '/appointment/list',
    method: 'get',
    params
  })
}

export function addAppointment(data) {
  return request({
    url: '/appointment',
    method: 'post',
    data
  })
}

export function updateAppointmentStatus(id, status) {
  return request({
    url: `/appointment/${id}/status`,
    method: 'put',
    data: { status }
  })
}
