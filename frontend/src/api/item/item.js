import request from '@/utils/request'

export function getItemList() {
  return request({
    url: '/item/list',
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

export function deleteItem(id) {
  return request({
    url: `/item/${id}`,
    method: 'delete'
  })
}
