import request from '@/utils/request'

export function getAttachmentList(params) {
    return request({
        url: '/upload/attachment/list',
        method: 'get',
        params
    })
}

export function uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/upload',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
