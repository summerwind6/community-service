import request from '@/utils/request'

export function getGuideList(params) {
    return request({
        url: '/guide/list',
        method: 'get',
        params
    })
}

export function getPublicGuides() {
    return request({
        url: '/guide/public',
        method: 'get'
    })
}

export function getGuideDetail(id) {
    return request({
        url: `/guide/${id}`,
        method: 'get'
    })
}

export function addGuide(data) {
    return request({
        url: '/guide',
        method: 'post',
        data
    })
}

export function updateGuide(data) {
    return request({
        url: '/guide',
        method: 'put',
        data
    })
}

export function deleteGuide(id) {
    return request({
        url: `/guide/${id}`,
        method: 'delete'
    })
}
