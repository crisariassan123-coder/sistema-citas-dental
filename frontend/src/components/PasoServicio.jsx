import { useEffect, useState } from 'react'
import { api } from '../api'
import { formatoPrecio } from '../formato'

export default function PasoServicio({ onElegir }) {
  const [servicios, setServicios] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.servicios()
      .then(setServicios)
      .catch((e) => setError(e.message))
      .finally(() => setCargando(false))
  }, [])

  return (
    <section>
      <h2>Elige el servicio</h2>
      {cargando && <p>Cargando servicios...</p>}
      {error && <p className="error">{error}</p>}

      <div className="opciones">
        {servicios.map((servicio) => (
          <button key={servicio.id} type="button" className="tarjeta" onClick={() => onElegir(servicio)}>
            <strong>{servicio.nombre}</strong>
            <span>{servicio.descripcion}</span>
            <span className="detalle">
              {servicio.duracionMinutos} min · {formatoPrecio.format(servicio.precio)}
            </span>
          </button>
        ))}
      </div>

      <p className="nota">
        ¿Necesitas otro tratamiento? Agenda una <strong>Valoración</strong> y el dentista te indicará el plan a seguir.
      </p>
    </section>
  )
}
