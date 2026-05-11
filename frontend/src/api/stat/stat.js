import request from '@/utils/request'

export function getDashboardData() {
  return request({
    url: '/stat/dashboard',
    method: 'get'
  })
}

export function getTrendData(params) {
  return request({
    url: '/stat/trend',
    method: 'get',
    params
  })
}

export function getDistributionData() {
  return request({
    url: '/stat/distribution',
    method: 'get'
  })
}
