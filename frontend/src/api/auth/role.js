import request from '@/utils/request'

export function getRoleList() {
  return request({
    url: '/role/list',
    method: 'get'
  })
}

export function getRoleDetail(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/role',
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

export function getRolePermissions(roleId) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'get'
  })
}

export function assignRolePermissions(roleId, data) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'put',
    data
  })
}
