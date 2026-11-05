import { useEffect, useState } from 'react'
import { api } from './api'
import { fechaLarga, hora, hoyISO } from './formato'
import PasoServicio from './components/PasoServicio'
import PasoDentista from './components/PasoDentista'
import PasoHorario from './components/PasoHorario'
import PasoDatos from './components/PasoDatos'
import Confirmacion from './components/Confirmacion'
import './App.css'

const DATOS_VACIOS = { nombre: '', apellidos: '', telefono: '', email: '', motivo: '' }

function App() {
  // Se guarda el paso actual del asistente: servicio → dentista → horario → datos → confirmacion
  const [paso, setPaso] = useState('servicio')

  const [dentistas, setDentistas] = useState([])
  const [servicio, setServicio] = useState(null)
  const [dentista, setDentista] = useState(null)
  const [fecha, setFecha] = useState(hoyISO())
  const [inicio, setInicio] = useState(null)
  const [datos, setDatos] = useState(DATOS_VACIOS)
  const [cita, setCita] = useState(null)

  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)
  const [errores, setErrores] = useState({})
  const [aviso, setAviso] = useState(null)

  useEffect(() => {
    api.dentistas().then(setDentistas).catch(() => setDentistas([]))
  }, [])

  // Si el consultorio tiene un solo dentista, se omite el paso de elegirlo
  const unSoloDentista = dentistas.length === 1

  function elegirServicio(s) {
    setServicio(s)
    if (unSoloDentista) {
      setDentista(dentistas[0])
      setPaso('horario')
    } else {
      setPaso('dentista')
    }
  }

  function elegirDentista(d) {
    setDentista(d)
    setPaso('horario')
  }

  function elegirHorario(fechaHora) {
    setInicio(fechaHora)
    setAviso(null)
    setPaso('datos')
  }

  async function confirmar() {
    setEnviando(true)
    setError(null)
    setErrores({})

    try {
      const nueva = await api.crearCita({
        dentistaId: dentista.id,
        servicioId: servicio.id,
        inicio,
        nombre: datos.nombre.trim(),
        apellidos: datos.apellidos.trim(),
        telefono: datos.telefono.trim(),
        email: datos.email.trim() || null,
        motivo: datos.motivo.trim() || null,
      })
      setCita(nueva)
      setPaso('confirmacion')
    } catch (e) {
      if (e.status === 409) {
        // El horario se ocupó un momento antes: se regresa al paso de elegir otro
        setAviso(`${e.message}. Por favor elige otro horario.`)
        setPaso('horario')
      } else {
        setError(e.message)
        setErrores(e.errores)
      }
    } finally {
      setEnviando(false)
    }
  }

  function reiniciar() {
    setPaso('servicio')
    setServicio(null)
    setDentista(null)
    setFecha(hoyISO())
    setInicio(null)
    setDatos(DATOS_VACIOS)
    setCita(null)
    setError(null)
    setErrores({})
    setAviso(null)
  }

  const volverDesdeHorario = () => setPaso(unSoloDentista ? 'servicio' : 'dentista')

  return (
    <main className="contenedor">
      <header className="encabezado">
        <h1>🦷 Agenda tu cita</h1>
        <p>Reserva en línea en menos de un minuto</p>
      </header>

      {servicio && paso !== 'confirmacion' && (
        <div className="seleccion">
          <span>{servicio.nombre}</span>
          {dentista && <span>{dentista.nombre}</span>}
          {inicio && paso === 'datos' && (
            <span>
              {fechaLarga(inicio.slice(0, 10))}, {hora(inicio.slice(11))}
            </span>
          )}
        </div>
      )}

      {paso === 'servicio' && <PasoServicio onElegir={elegirServicio} />}

      {paso === 'dentista' && (
        <PasoDentista dentistas={dentistas} onElegir={elegirDentista} onVolver={() => setPaso('servicio')} />
      )}

      {paso === 'horario' && (
        <PasoHorario
          servicio={servicio}
          dentista={dentista}
          fecha={fecha}
          onCambiarFecha={setFecha}
          onElegir={elegirHorario}
          onVolver={volverDesdeHorario}
          aviso={aviso}
        />
      )}

      {paso === 'datos' && (
        <PasoDatos
          datos={datos}
          onCambiar={setDatos}
          onConfirmar={confirmar}
          onVolver={() => setPaso('horario')}
          enviando={enviando}
          error={error}
          errores={errores}
        />
      )}

      {paso === 'confirmacion' && <Confirmacion cita={cita} onNuevaCita={reiniciar} />}
    </main>
  )
}

export default App
