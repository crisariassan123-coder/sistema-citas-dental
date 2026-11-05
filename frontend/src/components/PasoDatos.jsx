export default function PasoDatos({ datos, onCambiar, onConfirmar, onVolver, enviando, error, errores }) {
  const cambiar = (e) => onCambiar({ ...datos, [e.target.name]: e.target.value })

  const enviar = (e) => {
    e.preventDefault()
    onConfirmar()
  }

  return (
    <section>
      <h2>Tus datos</h2>
      {error && <p className="error">{error}</p>}

      <form className="formulario" onSubmit={enviar}>
        <label className="campo">
          Nombre *
          <input name="nombre" value={datos.nombre} onChange={cambiar} required maxLength={100} autoComplete="given-name" />
          {errores.nombre && <small className="error">{errores.nombre}</small>}
        </label>

        <label className="campo">
          Apellidos *
          <input name="apellidos" value={datos.apellidos} onChange={cambiar} required maxLength={100} autoComplete="family-name" />
          {errores.apellidos && <small className="error">{errores.apellidos}</small>}
        </label>

        <label className="campo">
          Teléfono (10 dígitos) *
          <input
            name="telefono"
            value={datos.telefono}
            onChange={cambiar}
            required
            inputMode="numeric"
            pattern="\d{10}"
            maxLength={10}
            title="Escribe 10 dígitos, sin espacios"
            autoComplete="tel-national"
          />
          {errores.telefono && <small className="error">{errores.telefono}</small>}
        </label>

        <label className="campo">
          Correo electrónico
          <input name="email" type="email" value={datos.email} onChange={cambiar} maxLength={150} autoComplete="email" />
          {errores.email && <small className="error">{errores.email}</small>}
        </label>

        <label className="campo">
          Motivo de la consulta
          <textarea name="motivo" value={datos.motivo} onChange={cambiar} maxLength={255} rows={3} />
          {errores.motivo && <small className="error">{errores.motivo}</small>}
        </label>

        <div className="acciones">
          <button type="button" className="boton-secundario" onClick={onVolver} disabled={enviando}>
            ← Volver
          </button>
          <button type="submit" className="boton-primario" disabled={enviando}>
            {enviando ? 'Agendando...' : 'Confirmar cita'}
          </button>
        </div>
      </form>
    </section>
  )
}
