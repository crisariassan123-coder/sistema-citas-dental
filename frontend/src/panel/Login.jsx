import { useState } from 'react'
import { apiPanel } from '../api'

export default function Login({ onIngresar, aviso }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)

  async function ingresar(e) {
    e.preventDefault()
    setEnviando(true)
    setError(null)

    try {
      const respuesta = await apiPanel.login(email.trim(), password)
      onIngresar({
        token: respuesta.token,
        expiraEn: respuesta.expiraEn,
        nombre: respuesta.nombre,
        rol: respuesta.rol,
      })
    } catch (err) {
      setError(err.message)
      // Por seguridad, se borra la contraseña escrita después de un intento fallido
      setPassword('')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <main className="contenedor login">
      <header className="encabezado">
        <h1>🦷 Panel del consultorio</h1>
        <p>Acceso exclusivo para el personal</p>
      </header>

      {aviso && <p className="aviso">{aviso}</p>}
      {error && <p className="error">{error}</p>}

      <form className="formulario tarjeta-formulario" onSubmit={ingresar}>
        <label className="campo">
          Correo electrónico
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required autoComplete="username" />
        </label>

        <label className="campo">
          Contraseña
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            autoComplete="current-password"
          />
        </label>

        <button type="submit" className="boton-primario" disabled={enviando}>
          {enviando ? 'Ingresando...' : 'Iniciar sesión'}
        </button>
      </form>
    </main>
  )
}
