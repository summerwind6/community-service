import request from '@/utils/request'

export function queryProgress(phone) {
  return request({
    url: '/apply/progress',
    method: 'get',
    params: { phone }
  })
}
