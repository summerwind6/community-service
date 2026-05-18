import request from '@/utils/request'

export function getSessionList() {
  return request({
    url: '/session/list',
    method: 'get'
  })
}

export function kickSession(sessionId) {
  return request({
    url: `/session/${sessionId}`,
    method: 'delete'
  })
}
