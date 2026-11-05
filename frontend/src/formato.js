export const formatoPrecio = new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' })

const formatoFecha = new Intl.DateTimeFormat('es-MX', {
  weekday: 'long',
  day: 'numeric',
  month: 'long',
  year: 'numeric',
})

// "09:00:00" -> "09:00"
export const hora = (texto) => texto.slice(0, 5)

// "2026-11-12" -> "jueves, 12 de noviembre de 2026"
export const fechaLarga = (fecha) => formatoFecha.format(new Date(`${fecha}T00:00`))

// Fecha de hoy en formato "AAAA-MM-DD", según la hora local del navegador
export function hoyISO() {
  const ahora = new Date()
  ahora.setMinutes(ahora.getMinutes() - ahora.getTimezoneOffset())
  return ahora.toISOString().slice(0, 10)
}
