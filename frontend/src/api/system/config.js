import request from '@/utils/request'

export function getConfigList() {
  return request({
    url: '/config/list',
    method: 'get'
  })
}

export function updateConfig(data) {
  return request({
    url: '/config',
    method: 'put',
    data
  })
}
