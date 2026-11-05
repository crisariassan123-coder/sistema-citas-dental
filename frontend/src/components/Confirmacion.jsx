import { useState } from 'react'
import { api } from '../api'
import { fechaLarga, hora } from '../formato'

export default function Confirmacion({ cita, onNuevaCita }) {
  const [estado, setEstado] = useState(cita.estado)
  const [cancelando, setCancelando] = useState(false)
  const [error, setError] = useState(null)

  const cancelada = estado === 'CANCELADA'

  async function cancelar() {
    if (!window.confirm('¿Seguro que quieres cancelar tu cita?')) return

    setCancelando(true)
    setError(null)
    try {
      const actualizada = await api.cancelarCita(cita.token)
      setEstado(actualizada.estado)
    } catch (e) {
      setError(e.message)
    } finally {
      setCancelando(false)
    }
  }

  return (
    <section className="confirmacion">
      <h2>{cancelada ? 'Tu cita fue cancelada' : '¡Tu cita quedó agendada!'}</h2>

      <dl className="resumen">
        <dt>Servicio</dt>
        <dd>{cita.servicio}</dd>
        <dt>Dentista</dt>
        <dd>{cita.dentista}</dd>
        <dt>Fecha</dt>
        <dd>{fechaLarga(cita.inicio.slice(0, 10))}</dd>
        <dt>Hora</dt>
        <dd>
          {hora(cita.inicio.slice(11))} a {hora(cita.fin.slice(11))}
        </dd>
        <dt>Estado</dt>
        <dd>{estado}</dd>
      </dl>

      {!cancelada && (
        <p className="nota">
          Guarda este código para consultar o cancelar tu cita: <code>{cita.token}</code>
        </p>
      )}
      {error && <p className="error">{error}</p>}

      <div className="acciones">
        {!cancelada && (
          <button type="button" className="boton-peligro" onClick={cancelar} disabled={cancelando}>
            {cancelando ? 'Cancelando...' : 'Cancelar cita'}
          </button>
        )}
        <button type="button" className="boton-primario" onClick={onNuevaCita}>
          Agendar otra cita
        </button>
      </div>
    </section>
  )
}
