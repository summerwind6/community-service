import { performance } from 'node:perf_hooks'

const base = process.env.API_BASE || 'http://localhost:8080/api'
const count = Number(process.argv[2] || 20)
if (!Number.isInteger(count) || count < 1 || count > 200) {
  throw new Error('并发量必须是 1 到 200 的整数')
}

const samples = await Promise.all(Array.from({ length: count }, async (_, index) => {
  const started = performance.now()
  try {
    const response = await fetch(`${base}/guide/public`, {
      headers: { 'X-Trace-Id': `guide-probe-${Date.now()}-${index}` },
      signal: AbortSignal.timeout(15_000)
    })
    const payload = await response.json()
    return {
      status: response.status,
      code: payload.code,
      ok: response.ok && payload.code === 200 && Array.isArray(payload.data),
      traceId: response.headers.get('x-trace-id'),
      durationMs: performance.now() - started
    }
  } catch (error) {
    return { status: 'error', ok: false, durationMs: performance.now() - started, error: error.message }
  }
}))

const ordered = samples.map(sample => sample.durationMs).sort((a, b) => a - b)
const statuses = {}
for (const sample of samples) {
  const key = `${sample.status}/${sample.code ?? '-'}`
  statuses[key] = (statuses[key] || 0) + 1
}
console.log(JSON.stringify({
  concurrentRequests: count,
  successfulResponses: samples.filter(sample => sample.ok).length,
  statuses,
  p95Ms: Number(ordered[Math.ceil(count * 0.95) - 1].toFixed(1)),
  traceIds: samples.map(sample => sample.traceId).filter(Boolean),
  errors: samples.filter(sample => sample.error).map(sample => sample.error)
}, null, 2))
if (!samples.some(sample => sample.ok)) process.exitCode = 1
