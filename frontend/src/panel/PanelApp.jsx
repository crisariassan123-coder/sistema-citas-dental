import { useEffect, useState } from 'react'
import Login from './Login'
import Agenda from './Agenda'
import { borrarSesion, guardarSesion, leerSesion } from './sesion'
import '../App.css'
import './Panel.css'

const MENSAJE_EXPIRADA = 'Tu sesión expiró. Inicia sesión de nuevo.'

export default function PanelApp() {
  const [sesion, setSesion] = useState(() => leerSesion())
  const [aviso, setAviso] = useState(null)

  function iniciarSesion(nueva) {
    guardarSesion(nueva)
    setSesion(nueva)
    setAviso(null)
  }

  function cerrarSesion(mensaje = null) {
    borrarSesion()
    setSesion(null)
    setAviso(mensaje)
  }

  // Se cierra la sesión automáticamente en el momento en que el token expira
  useEffect(() => {
    if (!sesion) return
    const restante = new Date(sesion.expiraEn) - new Date()
    const temporizador = setTimeout(() => cerrarSesion(MENSAJE_EXPIRADA), Math.max(restante, 0))
    return () => clearTimeout(temporizador)
  }, [sesion])

  if (!sesion) {
    return <Login onIngresar={iniciarSesion} aviso={aviso} />
  }

  return (
    <Agenda
      sesion={sesion}
      onCerrarSesion={() => cerrarSesion()}
      onSesionExpirada={() => cerrarSesion(MENSAJE_EXPIRADA)}
    />
  )
}
