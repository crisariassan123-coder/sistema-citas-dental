import { useEffect, useState } from 'react'
import './App.css'

const formatoPrecio = new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' })

function App() {
  const [servicios, setServicios] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetch('/api/public/servicios')
      .then((res) => {
        if (!res.ok) throw new Error('No se pudieron cargar los servicios')
        return res.json()
      })
      .then(setServicios)
      .catch((err) => setError(err.message))
      .finally(() => setCargando(false))
  }, [])

  return (
    <main className="contenedor">
      <h1>🦷 Agenda tu cita</h1>
      <p>Elige el servicio que necesitas</p>

      {cargando && <p>Cargando servicios...</p>}
      {error && <p className="error">{error}</p>}

      <ul className="servicios">
        {servicios.map((servicio) => (
          <li key={servicio.id} className="servicio">
            <h2>{servicio.nombre}</h2>
            <p>{servicio.descripcion}</p>
            <p className="detalle">
              {servicio.duracionMinutos} min · {formatoPrecio.format(servicio.precio)}
            </p>
          </li>
        ))}
      </ul>
    </main>
  )
}

export default App
