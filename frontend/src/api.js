// Funciones con las que se realiza la comunicación con el backend.
// Si la respuesta es un error, se lanza una excepción con el mensaje "detail" del ProblemDetail.
async function pedir(url, opciones) {
  const res = await fetch(url, opciones)
  const datos = await res.json().catch(() => null)

  if (!res.ok) {
    const error = new Error(datos?.detail ?? 'Ocurrió un error, intenta de nuevo')
    error.status = res.status
    error.errores = datos?.errores ?? {}
    throw error
  }
  return datos
}

export const api = {
  servicios: () => pedir('/api/public/servicios'),

  dentistas: () => pedir('/api/public/dentistas'),

  disponibilidad: (dentistaId, servicioId, fecha) =>
    pedir(`/api/public/disponibilidad?dentistaId=${dentistaId}&servicioId=${servicioId}&fecha=${fecha}`),

  crearCita: (datos) =>
    pedir('/api/public/citas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos),
    }),

  cancelarCita: (token) =>
    pedir(`/api/public/citas/${encodeURIComponent(token)}/cancelar`, { method: 'PATCH' }),
}
