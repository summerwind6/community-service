import request from '@/utils/request'

export function getItemList(params) {
  return request({
    url: '/item/list',
    method: 'get',
    params
  })
}

export function getEnabledItems() {
  return request({
    url: '/item/enabled',
    method: 'get'
  })
}

export function getItemDetail(id) {
  return request({
    url: `/item/${id}`,
    method: 'get'
  })
}

export function addItem(data) {
  return request({
    url: '/item',
    method: 'post',
    data
  })
}

export function updateItem(data) {
  return request({
    url: '/item',
    method: 'put',
    data
  })
}

export function toggleItemStatus(id, status) {
  return request({
    url: `/item/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function deleteItem(id) {
  return request({
    url: `/item/${id}`,
    method: 'delete'
  })
}
