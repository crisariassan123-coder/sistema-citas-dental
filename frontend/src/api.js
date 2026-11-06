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

const JSON_HEADERS = { 'Content-Type': 'application/json' }
const conToken = (token, extra = {}) => ({ Authorization: `Bearer ${token}`, ...extra })

// Endpoints públicos: flujo del paciente
export const api = {
  servicios: () => pedir('/api/public/servicios'),

  dentistas: () => pedir('/api/public/dentistas'),

  disponibilidad: (dentistaId, servicioId, fecha) =>
    pedir(`/api/public/disponibilidad?dentistaId=${dentistaId}&servicioId=${servicioId}&fecha=${fecha}`),

  crearCita: (datos) =>
    pedir('/api/public/citas', {
      method: 'POST',
      headers: JSON_HEADERS,
      body: JSON.stringify(datos),
    }),

  cancelarCita: (token) =>
    pedir(`/api/public/citas/${encodeURIComponent(token)}/cancelar`, { method: 'PATCH' }),
}

// Endpoints del personal: requieren el token JWT obtenido en el login
export const apiPanel = {
  login: (email, password) =>
    pedir('/api/auth/login', {
      method: 'POST',
      headers: JSON_HEADERS,
      body: JSON.stringify({ email, password }),
    }),

  citasDelDia: (token, fecha) =>
    pedir(`/api/personal/citas?fecha=${fecha}`, { headers: conToken(token) }),

  cambiarEstado: (token, id, estado) =>
    pedir(`/api/personal/citas/${id}/estado`, {
      method: 'PATCH',
      headers: conToken(token, JSON_HEADERS),
      body: JSON.stringify({ estado }),
    }),
}
