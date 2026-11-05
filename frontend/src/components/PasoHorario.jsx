import { useEffect, useState } from 'react'
import { api } from '../api'
import { fechaLarga, hora, hoyISO } from '../formato'

export default function PasoHorario({ servicio, dentista, fecha, onCambiarFecha, onElegir, onVolver, aviso }) {
  // Se guarda a qué consulta pertenece el resultado (dentista + servicio + fecha).
  // Si no coincide con la consulta actual, se considera que todavía está cargando.
  const consulta = `${dentista.id}-${servicio.id}-${fecha}`
  const [resultado, setResultado] = useState({ consulta: null, horarios: [], error: null })
  const cargando = Boolean(fecha) && resultado.consulta !== consulta

  useEffect(() => {
    if (!fecha) return

    // Con "vigente" se evita mostrar una respuesta vieja si el paciente cambia de fecha muy rápido
    let vigente = true

    api.disponibilidad(dentista.id, servicio.id, fecha)
      .then((datos) => {
        if (vigente) setResultado({ consulta, horarios: datos.horariosDisponibles, error: null })
      })
      .catch((e) => {
        if (vigente) setResultado({ consulta, horarios: [], error: e.message })
      })

    return () => {
      vigente = false
    }
  }, [consulta, dentista.id, servicio.id, fecha])

  const { horarios, error } = resultado

  return (
    <section>
      <h2>Elige la fecha y la hora</h2>
      {aviso && <p className="aviso">{aviso}</p>}

      <label className="campo">
        Fecha
        <input type="date" value={fecha} min={hoyISO()} onChange={(e) => onCambiarFecha(e.target.value)} />
      </label>

      {fecha && <p className="subtitulo">{fechaLarga(fecha)}</p>}
      {cargando && <p>Buscando horarios disponibles...</p>}

      {!cargando && fecha && error && <p className="error">{error}</p>}

      {!cargando && fecha && !error && horarios.length === 0 && (
        <p className="nota">No hay horarios disponibles este día. Prueba con otra fecha.</p>
      )}

      {!cargando && fecha && (
        <div className="horas">
          {horarios.map((h) => (
            <button key={h} type="button" className="hora" onClick={() => onElegir(`${fecha}T${h}`)}>
              {hora(h)}
            </button>
          ))}
        </div>
      )}

      <div className="acciones">
        <button type="button" className="boton-secundario" onClick={onVolver}>
          ← Volver
        </button>
      </div>
    </section>
  )
}
