export default function ErrorMessage({ error }) {
  if (!error) return null

  const details = Object.entries(error.details ?? {})

  return (
    <div role="alert" className="error">
      <strong>{error.message}</strong>
      {details.length > 0 && (
        <ul>
          {details.map(([field, msg]) => (
            <li key={field}>{msg}</li>
          ))}
        </ul>
      )}
    </div>
  )
}
