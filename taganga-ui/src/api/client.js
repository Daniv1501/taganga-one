export class ApiError extends Error {
  constructor({ status, message, details }) {
    super(message)
    this.status = status
    this.details = details ?? {}
  }
}

const UNREACHABLE = 'No se pudo contactar el servidor'

export async function request(path, options = {}) {
  let response
  try {
    response = await fetch(path, {
      headers: { 'Content-Type': 'application/json' },
      ...options,
    })
  } catch {
    // Sin conexión: fetch rechaza antes de recibir respuesta
    throw new ApiError({ status: 0, message: UNREACHABLE })
  }

  if (!response.ok) {
    // El backend responde ErrorResponse; si no (backend caído), usamos un mensaje genérico
    const body = await response.json().catch(() => null)
    throw new ApiError({
      status: body?.status ?? response.status,
      message: body?.message ?? UNREACHABLE,
      details: body?.details,
    })
  }

  return response.json()
}
