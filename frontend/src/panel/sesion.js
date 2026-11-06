// La sesión se guarda en sessionStorage: se conserva al recargar la página,
// pero se borra al cerrar la pestaña, lo que reduce el riesgo en equipos compartidos.
const CLAVE = 'citas-dental-sesion'

export function leerSesion() {
  try {
    const sesion = JSON.parse(sessionStorage.getItem(CLAVE))
    // Solo se acepta una sesión con token y que todavía no haya expirado
    if (sesion?.token && new Date(sesion.expiraEn) > new Date()) {
      return sesion
    }
  } catch {
    // Si el almacenamiento no está disponible, se continúa sin sesión guardada
  }
  return null
}

export function guardarSesion(sesion) {
  try {
    sessionStorage.setItem(CLAVE, JSON.stringify(sesion))
  } catch {
    // Si no se puede guardar, la sesión solo dura mientras la página siga abierta
  }
}

export function borrarSesion() {
  try {
    sessionStorage.removeItem(CLAVE)
  } catch {
    // No hay nada que borrar si el almacenamiento no está disponible
  }
}
