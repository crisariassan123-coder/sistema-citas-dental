import { useEffect, useState } from 'react'
import { apiPanel } from '../api'
import { fechaLarga, hora, hoyISO, sumarDias } from '../formato'

const ETIQUETAS = {
  PENDIENTE: 'Pendiente',
  CONFIRMADA: 'Confirmada',
  ATENDIDA: 'Atendida',
  NO_ASISTIO: 'No asistió',
  CANCELADA: 'Cancelada',
}

// Acciones disponibles según el estado. Son las mismas reglas que valida el backend;
// aquí solo se usan para no mostrar botones que el servidor rechazaría.
function accionesPara(cita) {
  const activa = cita.estado === 'PENDIENTE' || cita.estado === 'CONFIRMADA'
  if (!activa) return []

  const yaEmpezo = new Date(cita.inicio) <= new Date()
  const acciones = []

  if (cita.estado === 'PENDIENTE') acciones.push(['CONFIRMADA', 'Confirmar'])
  if (yaEmpezo) {
    acciones.push(['ATENDIDA', 'Atendida'], ['NO_ASISTIO', 'No asistió'])
  } else {
    acciones.push(['CANCELADA', 'Cancelar'])
  }
  return acciones
}

export default function Agenda({ sesion, onCerrarSesion, onSesionExpirada }) {
  const [fecha, setFecha] = useState(hoyISO())
  const [resultado, setResultado] = useState({ fecha: null, citas: [], error: null })
  const [actualizando, setActualizando] = useState(null)
  const [errorAccion, setErrorAccion] = useState(null)

  // Si el resultado guardado no corresponde a la fecha elegida, se considera que está cargando
  const cargando = resultado.fecha !== fecha

  useEffect(() => {
    let vigente = true

    apiPanel.citasDelDia(sesion.token, fecha)
      .then((citas) => vigente && setResultado({ fecha, citas, error: null }))
      .catch((e) => {
        if (!vigente) return
        if (e.status === 401) return onSesionExpirada()
        setResultado({ fecha, citas: [], error: e.message })
      })

    return () => {
      vigente = false
    }
  }, [fecha, sesion.token, onSesionExpirada])

  async function cambiarEstado(cita, estado, etiqueta) {
    if (estado === 'CANCELADA' && !window.confirm(`¿Cancelar la cita de ${cita.paciente}?`)) return

    setActualizando(cita.id)
    setErrorAccion(null)
    try {
      const actualizada = await apiPanel.cambiarEstado(sesion.token, cita.id, estado)
      // Se reemplaza solo la cita modificada, sin volver a pedir toda la agenda
      setResultado((r) => ({ ...r, citas: r.citas.map((c) => (c.id === actualizada.id ? actualizada : c)) }))
    } catch (e) {
      if (e.status === 401) return onSesionExpirada()
      setErrorAccion(`No se pudo marcar como "${etiqueta}": ${e.message}`)
    } finally {
      setActualizando(null)
    }
  }

  const { citas, error } = resultado
  const activas = citas.filter((c) => c.estado !== 'CANCELADA').length

  return (
    <main className="contenedor panel">
      <header className="barra-panel">
        <div>
          <h1>🦷 Agenda del consultorio</h1>
          <p>
            {sesion.nombre} · <span className="rol">{sesion.rol}</span>
          </p>
        </div>
        <button type="button" className="boton-secundario" onClick={onCerrarSesion}>
          Cerrar sesión
        </button>
      </header>

      <div className="navegacion-fecha">
        <button type="button" className="boton-secundario" onClick={() => setFecha(sumarDias(fecha, -1))}>
          ← Anterior
        </button>
        <button type="button" className="boton-secundario" onClick={() => setFecha(hoyISO())}>
          Hoy
        </button>
        <button type="button" className="boton-secundario" onClick={() => setFecha(sumarDias(fecha, 1))}>
          Siguiente →
        </button>
        <input type="date" value={fecha} onChange={(e) => e.target.value && setFecha(e.target.value)} />
      </div>

      <h2 className="subtitulo">{fechaLarga(fecha)}</h2>

      {cargando && <p>Cargando citas...</p>}
      {!cargando && error && <p className="error">{error}</p>}
      {errorAccion && <p className="error">{errorAccion}</p>}

      {!cargando && !error && (
        <>
          <p className="nota">
            {citas.length === 0 ? 'No hay citas para este día.' : `${activas} cita(s) activa(s) de ${citas.length} registrada(s).`}
          </p>

          <ul className="lista-citas">
            {citas.map((cita) => (
              <li key={cita.id} className={`cita estado-${cita.estado.toLowerCase()}`}>
                <div className="cita-hora">
                  {hora(cita.inicio.slice(11))}
                  <small>a {hora(cita.fin.slice(11))}</small>
                </div>

                <div className="cita-detalle">
                  <strong>{cita.paciente}</strong>
                  <span>{cita.servicio}</span>
                  <a href={`tel:${cita.telefono}`}>📞 {cita.telefono}</a>
                  {cita.email && <span>✉️ {cita.email}</span>}
                  {cita.motivo && <span className="motivo">“{cita.motivo}”</span>}
                </div>

                <div className="cita-acciones">
                  <span className={`etiqueta etiqueta-${cita.estado.toLowerCase()}`}>{ETIQUETAS[cita.estado]}</span>
                  {accionesPara(cita).map(([estado, etiqueta]) => (
                    <button
                      key={estado}
                      type="button"
                      className={estado === 'CANCELADA' ? 'boton-peligro' : 'boton-secundario'}
                      disabled={actualizando === cita.id}
                      onClick={() => cambiarEstado(cita, estado, etiqueta)}
                    >
                      {etiqueta}
                    </button>
                  ))}
                </div>
              </li>
            ))}
          </ul>
        </>
      )}
    </main>
  )
}
