export default function PasoDentista({ dentistas, onElegir, onVolver }) {
  return (
    <section>
      <h2>Elige al dentista</h2>

      <div className="opciones">
        {dentistas.map((dentista) => (
          <button key={dentista.id} type="button" className="tarjeta" onClick={() => onElegir(dentista)}>
            <strong>{dentista.nombre}</strong>
            <span>{dentista.especialidad}</span>
          </button>
        ))}
      </div>

      <div className="acciones">
        <button type="button" className="boton-secundario" onClick={onVolver}>
          ← Volver
        </button>
      </div>
    </section>
  )
}
