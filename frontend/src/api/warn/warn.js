import request from '@/utils/request'

export function getTimeoutWarnList(params) {
    return request({
        url: '/warn/timeout-list',
        method: 'get',
        params
    })
}

export function getTimeoutWarnCount() {
    return request({
        url: '/warn/timeout-count',
        method: 'get'
    })
}

export function urgeTimeoutApplication(id) {
    return request({
        url: `/warn/${id}/urge`,
        method: 'put'
    })
}
