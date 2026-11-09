// Logotipo: sol (dorado), mar (azul), cerro (verde)
export default function BrandLogo({ size }) {
  return (
    <div className="brand-logo" aria-hidden="true">
      <svg width={size} height={size} viewBox="0 0 40 40">
        <circle cx="27" cy="13" r="7" fill="#D17900"/>
        <path d="M2 28 L14 12 L24 26 Z" fill="#005C06"/>
        <path d="M2 30 q5-4 10 0 t10 0 t10 0 t8 0 V38 H2Z" fill="#004A87"/>
      </svg>
    </div>
  )
}
