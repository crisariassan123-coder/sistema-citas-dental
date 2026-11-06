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

// Se convierte una fecha a "AAAA-MM-DD" según la hora local del navegador
function aISO(fecha) {
  const copia = new Date(fecha)
  copia.setMinutes(copia.getMinutes() - copia.getTimezoneOffset())
  return copia.toISOString().slice(0, 10)
}

// Fecha de hoy en formato "AAAA-MM-DD"
export const hoyISO = () => aISO(new Date())

// "2026-11-12" + 1 -> "2026-11-13"
export function sumarDias(fecha, dias) {
  const resultado = new Date(`${fecha}T00:00`)
  resultado.setDate(resultado.getDate() + dias)
  return aISO(resultado)
}
