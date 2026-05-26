import request from '@/utils/request'

export function getAppointmentList(params) {
  return request({ url: '/appointment/list', method: 'get', params })
}

export function addAppointment(data) {
  return request({ url: '/appointment', method: 'post', data })
}

export function confirmAppointment(id) {
  return request({ url: `/appointment/${id}/confirm`, method: 'put' })
}

export function cancelAppointment(id) {
  return request({ url: `/appointment/${id}/cancel`, method: 'put' })
}

export function completeAppointment(id) {
  return request({ url: `/appointment/${id}/complete`, method: 'put' })
}
