import request from '@/utils/request'

/** 超级管理员：账号维护（列表 / 保存 / 删除）及角色下拉 */

export function fetchAdminList(params) {
  return request({
    url: '/admin/list',
    method: 'get',
    params
  })
}

export function deleteAdmin(data) {
  return request({
    url: '/admin/delete',
    method: 'post',
    data
  })
}

export function saveAdmin(data) {
  return request({
    url: '/admin/save',
    method: 'post',
    data
  })
}

/** 管理员表单中「角色」下拉数据源 */
export function fetchRoles() {
  return request({
    url: '/roles',
    method: 'get'
  })
}
