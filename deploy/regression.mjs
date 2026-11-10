import assert from 'node:assert/strict'
import { randomBytes, randomInt } from 'node:crypto'
import { test } from 'node:test'

const { SMOKE_API_BASE: base, SMOKE_ADMIN_USER: adminUser, SMOKE_ADMIN_PASSWORD: adminPassword,
  SMOKE_FRONTEND_URL: frontend, SMOKE_EXPIRED_APPLY_NO: expiredApplyNo } = process.env
assert.ok(base && adminUser && adminPassword, 'Set SMOKE_API_BASE, SMOKE_ADMIN_USER and SMOKE_ADMIN_PASSWORD for a test instance')

const suffix = randomBytes(4).toString('hex')
const password = 'SmokeA!' + suffix
const nextPassword = 'SmokeB!' + suffix
const fixtureName = 'Regression ' + suffix
const phonePrefix = '139' + String(randomInt(0, 10_000)).padStart(4, '0')
let phoneSequence = 0
function resident() {
  const number = String(++phoneSequence).padStart(4, '0')
  return { name: fixtureName, phone: phonePrefix + number, idCard: '1101011990' + phonePrefix.slice(3) + number }
}

function client() {
  const cookies = new Map()
  async function request(path, { method = 'GET', data, body } = {}) {
    const headers = {}
    if (cookies.size) {
      headers.Cookie = Array.from(cookies, ([name, value]) => name + '=' + value).join('; ')
    }
    if (data !== undefined) {
      headers['Content-Type'] = 'application/json'
      body = JSON.stringify(data)
    }
    const response = await fetch(base + path, { method, headers, body, signal: AbortSignal.timeout(15_000) })
    for (const value of response.headers.getSetCookie()) {
      const [pair, ...attributes] = value.split(';')
      const index = pair.indexOf('=')
      const name = pair.slice(0, index)
      if (attributes.some(attribute => attribute.trim().toLowerCase() === 'max-age=0')) cookies.delete(name)
      else cookies.set(name, pair.slice(index + 1))
    }
    return response
  }
  async function api(path, method = 'GET', data, code = 200) {
    const response = await request(path, { method, data })
    const result = await response.json()
    assert.equal(result.code, code, method + ' ' + path + ' (HTTP ' + response.status + '): ' + result.msg)
    if (code === 200) assert.equal(response.status, 200, method + ' ' + path)
    return result.data
  }
  async function login(username, secret) {
    await api('/auth/login', 'POST', { username, password: secret })
    assert.equal((await api('/auth/info')).username, username)
  }
  return { api, request, login }
}

async function administrator() {
  const session = client()
  await session.login(adminUser, adminPassword)
  return session
}

function query(path, params) {
  return path + '?' + new URLSearchParams(params)
}

test('authentication, user and role administration, profile, password and sessions', async () => {
  const anonymous = client()
  await anonymous.api('/auth/info', 'GET', undefined, 401)
  await anonymous.api('/auth/login', 'POST', { username: 'missing_' + suffix, password }, 400)
  const admin = await administrator()
  const role = await admin.api('/role', 'POST', { roleName: fixtureName, roleCode: 'SMOKE_' + suffix, status: 1 })
  assert.ok((await admin.api('/role/list')).some(row => row.id === role.id))
  await admin.api('/role', 'PUT', { ...role, description: 'role regression' })
  assert.equal((await admin.api('/role/' + role.id)).description, 'role regression')
  await admin.api('/role/' + role.id + '/permissions', 'PUT', { permIds: [8] })
  assert.ok((await admin.api('/role/' + role.id + '/permissions')).some(row => row.permCode === 'resident:list'))

  const username = 'smoke_' + suffix
  await admin.api('/user', 'POST', { username, password, realName: fixtureName, roleId: role.id, status: 1 })
  const { records } = await admin.api(query('/user/list', { keyword: username }))
  const created = records.find(row => row.username === username)
  assert.ok(created)
  await admin.api('/user', 'PUT', { id: created.id, username, realName: 'Changed ' + suffix, roleId: role.id, status: 1 })
  assert.equal((await admin.api('/user/' + created.id)).realName, 'Changed ' + suffix)

  const user = client()
  await user.login(username, password)
  await user.api('/user/list', 'GET', undefined, 403)
  await user.api('/resident/list')
  await user.api('/auth/profile', 'PUT', { realName: 'Profile ' + suffix, email: username + '@example.invalid' })
  assert.equal((await user.api('/auth/info')).realName, 'Profile ' + suffix)
  await user.api('/auth/change-password', 'PUT', { oldPassword: password, newPassword: nextPassword })
  await user.api('/auth/logout', 'POST')
  await user.api('/auth/info', 'GET', undefined, 401)
  await user.login(username, nextPassword)
  const sessions = await admin.api('/session/list')
  const online = sessions.find(row => row.username === username)
  assert.ok(online)
  await admin.api('/session/' + online.sessionId, 'DELETE')
  await user.api('/auth/info', 'GET', undefined, 401)
  await admin.api('/user/' + created.id, 'DELETE')
  assert.ok(!(await admin.api(query('/user/list', { keyword: username }))).records.some(row => row.id === created.id))
  await admin.api('/role/' + role.id, 'DELETE')
  assert.ok(!(await admin.api('/role/list')).some(row => row.id === role.id))
})

test('resident creation, search, update and deletion', async () => {
  const admin = await administrator()
  const details = resident()
  const created = await admin.api('/resident', 'POST', details)
  assert.equal((await admin.api(query('/resident/by-phone', { phone: details.phone }))).id, created.id)
  assert.ok((await admin.api(query('/resident/list', { keyword: details.phone }))).records.some(row => row.id === created.id))
  await admin.api('/resident', 'PUT', { id: created.id, address: 'Updated address' })
  assert.equal((await admin.api('/resident/' + created.id)).address, 'Updated address')
  await admin.api('/resident/' + created.id, 'DELETE')
  assert.equal(await admin.api(query('/resident/by-phone', { phone: details.phone })), null)
})

test('guide access, pagination and publish, unpublish and delete', async () => {
  const admin = await administrator()
  const publicClient = client()
  const category = 'A & B'
  await publicClient.api('/guide', 'POST', { title: fixtureName }, 401)
  assert.equal((await publicClient.request('/internal/guide')).status, 404)
  assert.equal((await publicClient.request('/actuator/health')).status, 404)
  const guide = await admin.api('/guide', 'POST', { title: fixtureName, content: '<p>test</p>', status: 1, category })
  const list = await admin.api(query('/guide/list', { keyword: suffix, category, status: 1 }))
  assert.ok(list.records.some(row => row.id === guide.id))
  assert.equal((await admin.api('/guide/' + guide.id)).category, category)
  assert.ok((await publicClient.api('/guide/public')).some(row => row.id === guide.id))
  await admin.api('/guide', 'PUT', { id: guide.id, status: 0 })
  assert.ok(!(await publicClient.api('/guide/public')).some(row => row.id === guide.id))
  await admin.api('/guide/' + guide.id, 'DELETE')
  assert.equal(await admin.api('/guide/' + guide.id), null)
})

test('service item creation, update, cache invalidation and deletion', async () => {
  const admin = await administrator()
  const publicClient = client()
  const item = await admin.api('/item', 'POST', { itemName: fixtureName, itemCode: 'SMOKE_' + suffix,
    category: 'test', processingDays: 3, fee: 12, status: 1 })
  assert.ok((await admin.api(query('/item/list', { keyword: suffix }))).records.some(row => row.id === item.id))
  assert.equal((await admin.api('/item/' + item.id)).fee, 12)
  assert.ok((await publicClient.api('/item/enabled')).some(row => row.id === item.id))
  await admin.api('/item', 'PUT', { id: item.id, itemName: 'Updated ' + suffix })
  assert.ok((await publicClient.api('/item/enabled')).some(row => row.itemName === 'Updated ' + suffix))
  await admin.api('/item/' + item.id + '/status?status=0', 'PUT')
  assert.ok(!(await publicClient.api('/item/enabled')).some(row => row.id === item.id))
  await admin.api('/item/' + item.id + '/status?status=1', 'PUT')
  assert.ok((await publicClient.api('/item/enabled')).some(row => row.id === item.id))
  await admin.api('/item/' + item.id, 'DELETE')
  assert.ok(!(await publicClient.api('/item/enabled')).some(row => row.id === item.id))
})

test('attachments, submission, review, approval, progress and evaluation', async () => {
  const admin = await administrator()
  const publicClient = client()
  const item = (await publicClient.api('/item/enabled'))[0]
  assert.ok(item, 'Initialize demo service items first')
  const details = resident()
  await publicClient.api('/apply/public', 'POST', {
    residentName: details.name, idCard: details.idCard, serviceItemId: String(item.id)
  }, 400)
  const multipart = new FormData()
  multipart.set('file', new Blob(['regression attachment']), 'regression.txt')
  const upload = await publicClient.request('/upload/public', { method: 'POST', body: multipart })
  assert.equal(upload.status, 200)
  const temp = await upload.json()
  assert.equal(temp.code, 200)
  const { applyNo } = await publicClient.api('/apply/public', 'POST', { residentName: details.name, phone: details.phone,
    idCard: details.idCard, serviceItemId: String(item.id), attachments: JSON.stringify([temp.data]) })
  const verification = { applyNo, phoneLast4: details.phone.slice(-4) }
  const progress = query('/apply/progress', verification)
  assert.equal((await publicClient.api(progress)).status, 'PENDING')
  await publicClient.api(query('/apply/progress', { applyNo, phoneLast4: '9999' }), 'GET', undefined, 404)
  await publicClient.api(query('/apply/progress', { applyNo }), 'GET', undefined, 400)

  const { records } = await admin.api(query('/apply/list', { keyword: applyNo, status: 'PENDING' }))
  const application = records.find(row => row.applyNo === applyNo)
  assert.ok(application)
  const path = '/apply/' + application.id
  assert.equal((await admin.api(path)).applyNo, applyNo)
  const [attachment] = await admin.api(query('/upload/attachment/list', { applyNo }))
  assert.equal(await (await admin.request(attachment.filePath)).text(), 'regression attachment')
  await admin.api(path + '/review', 'POST', { result: 'PROCESSING', opinion: 'first review' })
  assert.equal((await publicClient.api(progress)).status, 'PROCESSING')
  await admin.api(path + '/approve', 'POST', { result: 'COMPLETED', opinion: 'second review' })
  assert.equal((await publicClient.api(progress)).status, 'COMPLETED')
  assert.equal((await admin.api(path + '/audit-records')).length, 2)
  for (let i = 0; i < 20; i++) {
    const { records: notices } = await admin.api('/notification/list?type=AUDIT&pageSize=50')
    if (notices.filter(row => row.targetId === application.id).length === 2) break
    assert.ok(i < 19, 'Expected both audit notifications')
    await new Promise(resolve => setTimeout(resolve, 500))
  }

  await publicClient.api('/evaluation/public', 'POST', { applyNo, score: 5 }, 400)
  await publicClient.api('/evaluation/public', 'POST', verification, 400)
  await publicClient.api('/evaluation/public', 'POST', { ...verification, phoneLast4: '9999', score: 5 }, 404)
  const evaluation = await publicClient.api('/evaluation/public', 'POST', { ...verification, score: 5, content: fixtureName })
  assert.equal(evaluation.applicationId, application.id)
  assert.equal((await publicClient.api(progress)).evaluated, true)
  assert.ok((await admin.api('/evaluation/list?score=5')).records.some(row => row.id === evaluation.id))
  assert.ok((await admin.api('/evaluation/stats')).total > 0)
  await admin.api('/evaluation', 'POST', { applicationId: application.id, score: 5 }, 400)
})

test('appointment confirmation, completion, cancellation and status/date filters', async () => {
  const admin = await administrator()
  const details = resident()
  const date = new Date(Date.now() + 86_400_000).toISOString().slice(0, 10)
  const item = (await admin.api('/item/enabled'))[0]
  const payload = { residentName: details.name, residentPhone: details.phone, serviceItemId: item.id,
    serviceItemName: item.itemName, appointmentDate: date, timeSlot: '09:00-10:00' }
  const first = await admin.api('/appointment', 'POST', payload)
  const second = await admin.api('/appointment', 'POST', payload)
  await admin.api('/appointment/' + first.id + '/confirm', 'PUT')
  const { records } = await admin.api(query('/appointment/list', { status: 'CONFIRMED', startDate: date, endDate: date }))
  assert.ok(records.some(row => row.id === first.id))
  assert.ok(!records.some(row => row.id === second.id))
  await admin.api('/appointment/' + first.id + '/complete', 'PUT')
  await admin.api('/appointment/' + second.id + '/cancel', 'PUT')
  assert.ok((await admin.api('/appointment/list?status=COMPLETED')).records.some(row => row.id === first.id))
  assert.ok((await admin.api('/appointment/list?status=CANCELED')).records.some(row => row.id === second.id))
})

test('warning scan, reminders, notifications, system config, dashboard and reports', async () => {
  const admin = await administrator()
  await admin.api('/warn/system-scan', 'POST')
  const warnings = await admin.api('/warn/timeout-list')
  assert.ok((await admin.api('/warn/timeout-count')).count >= warnings.records.length)
  if (expiredApplyNo) {
    const warning = warnings.records.find(row => row.applyNo === expiredApplyNo)
    assert.ok(warning, 'Expected the provided overdue application fixture')
    await admin.api('/warn/' + warning.applicationId + '/urge', 'PUT')
    assert.ok((await admin.api('/notification/list?type=URGENT')).records.some(row => row.targetId === warning.applicationId))
  }

  const sent = await admin.api('/notification', 'POST', { title: fixtureName, content: 'manual notification', type: 'SYSTEM' })
  assert.ok((await admin.api('/notification/unread-count')).count > 0)
  await admin.api('/notification/' + sent.id + '/read', 'PUT')
  assert.ok((await admin.api('/notification/list?type=SYSTEM&isRead=1')).records.some(row => row.id === sent.id))

  const config = (await admin.api('/config/list')).find(row => row.configKey === 'system_name')
  assert.ok(config)
  try {
    await admin.api('/config', 'PUT', [{ ...config, configValue: fixtureName }])
    assert.equal((await admin.api('/config/list')).find(row => row.id === config.id).configValue, fixtureName)
  } finally {
    await admin.api('/config', 'PUT', [config])
  }
  await admin.api('/stat/dashboard')
  await admin.api('/stat/trend?period=week')
  await admin.api('/stat/distribution')
  for (const name of ['apply', 'resident', 'evaluation']) {
    const response = await admin.request('/report/export/' + name)
    assert.equal(response.status, 200)
    assert.match(response.headers.get('content-disposition') || '', /\.xlsx/)
    const bytes = new Uint8Array(await response.arrayBuffer())
    assert.deepEqual(Array.from(bytes.slice(0, 4)), [80, 75, 3, 4])
  }
})

test('frontend page routing', { skip: !frontend }, async () => {
  for (const path of ['/login', '/resident/apply', '/guide/manage', '/apply/list', '/appointment/list', '/report']) {
    const response = await fetch(new URL(path, frontend), { signal: AbortSignal.timeout(10_000) })
    assert.equal(response.status, 200, path)
    assert.match(await response.text(), /id="app"/, path)
  }
})
