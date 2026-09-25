import { useMemo, useState } from 'react'
import './App.css'

const controls = [
  {
    key: 'span',
    label: 'Ouverture de l’arc',
    unit: 'm',
    min: 4,
    max: 20,
    step: 0.1,
  },
  {
    key: 'rise',
    label: 'Flèche de l’arc',
    unit: 'm',
    min: 2,
    max: 14,
    step: 0.1,
  },
  {
    key: 'thickness',
    label: 'Épaisseur constante',
    unit: 'm',
    min: 0.2,
    max: 2.5,
    step: 0.05,
  },
  {
    key: 'pillarWidth',
    label: 'Largeur du pilier',
    unit: 'm',
    min: 0.5,
    max: 4,
    step: 0.05,
  },
  {
    key: 'pillarHeight',
    label: 'Hauteur du pilier',
    unit: 'm',
    min: 1.5,
    max: 8,
    step: 0.1,
  },
]

const initialParameters = {
  span: 9,
  rise: 5.8,
  thickness: 0.7,
  pillarWidth: 1.4,
  pillarHeight: 3.5,
}

const viewBoxWidth = 1000
const viewBoxHeight = 720
const arcSamples = 60
const catenarySamples = 120
const tau = Math.PI * 2

function normalizeAngle(angle) {
  let normalized = angle

  while (normalized <= -Math.PI) {
    normalized += tau
  }

  while (normalized > Math.PI) {
    normalized -= tau
  }

  return normalized
}

function chooseUpperDelta(startAngle, endAngle, centerY, radius) {
  const primary = normalizeAngle(endAngle - startAngle)
  const alternate = primary > 0 ? primary - tau : primary + tau
  const deltas = [primary, alternate]

  return deltas.reduce((bestDelta, delta) => {
    const bestMidAngle = startAngle + bestDelta / 2
    const candidateMidAngle = startAngle + delta / 2
    const bestMidY = centerY + Math.sin(bestMidAngle) * radius
    const candidateMidY = centerY + Math.sin(candidateMidAngle) * radius

    return candidateMidY > bestMidY ? delta : bestDelta
  })
}

function sampleArc(centerX, centerY, radius, startAngle, endAngle, steps) {
  const delta = chooseUpperDelta(startAngle, endAngle, centerY, radius)

  return Array.from({ length: steps + 1 }, (_, index) => {
    const angle = startAngle + (delta * index) / steps

    return {
      x: centerX + Math.cos(angle) * radius,
      y: centerY + Math.sin(angle) * radius,
    }
  })
}

function solveCatenaryParameter(halfSpan, rise) {
  if (halfSpan <= 0 || rise <= 0) {
    return 1
  }

  const valueAt = (parameter) => parameter * (Math.cosh(halfSpan / parameter) - 1)
  let low = 0.0001
  let high = Math.max(halfSpan, rise, 1)

  while (valueAt(high) > rise) {
    high *= 2
  }

  for (let index = 0; index < 80; index += 1) {
    const middle = (low + high) / 2

    if (valueAt(middle) > rise) {
      low = middle
    } else {
      high = middle
    }
  }

  return high
}

function sampleCatenary(halfSpan, baseY, apexY, steps) {
  const rise = apexY - baseY
  const parameter = solveCatenaryParameter(halfSpan, rise)

  return Array.from({ length: steps + 1 }, (_, index) => {
    const x = -halfSpan + (2 * halfSpan * index) / steps
    const y = apexY - parameter * (Math.cosh(x / parameter) - 1)

    return { x, y }
  })
}

function pointsToPath(points, close = false) {
  if (points.length === 0) {
    return ''
  }

  const segments = points.map(({ x, y }, index) =>
    `${index === 0 ? 'M' : 'L'} ${x.toFixed(2)} ${y.toFixed(2)}`,
  )

  return `${segments.join(' ')}${close ? ' Z' : ''}`
}

function formatValue(value, digits = 2) {
  return new Intl.NumberFormat('fr-FR', {
    maximumFractionDigits: digits,
    minimumFractionDigits: digits,
  }).format(value)
}

function buildModel({ span, rise, thickness, pillarWidth, pillarHeight }) {
  const halfSpan = span / 2
  const centerOffset = span / 6
  const localCenterY = (rise * rise - (span * span) / 12) / (2 * rise)
  const leftCenter = { x: -centerOffset, y: pillarHeight + localCenterY }
  const rightCenter = { x: centerOffset, y: pillarHeight + localCenterY }
  const radius = Math.hypot(halfSpan - centerOffset, localCenterY)
  const outerRadius = radius + thickness

  const leftSpringAngle = Math.atan2(pillarHeight - leftCenter.y, -halfSpan - leftCenter.x)
  const leftApexAngle = Math.atan2(
    pillarHeight + rise - leftCenter.y,
    -leftCenter.x,
  )
  const rightApexAngle = Math.atan2(
    pillarHeight + rise - rightCenter.y,
    -rightCenter.x,
  )
  const rightSpringAngle = Math.atan2(
    pillarHeight - rightCenter.y,
    halfSpan - rightCenter.x,
  )

  const intradosLeft = sampleArc(
    leftCenter.x,
    leftCenter.y,
    radius,
    leftSpringAngle,
    leftApexAngle,
    arcSamples,
  )
  const intradosRight = sampleArc(
    rightCenter.x,
    rightCenter.y,
    radius,
    rightApexAngle,
    rightSpringAngle,
    arcSamples,
  ).slice(1)

  const extradosLeft = sampleArc(
    leftCenter.x,
    leftCenter.y,
    outerRadius,
    leftSpringAngle,
    leftApexAngle,
    arcSamples,
  )
  const extradosRight = sampleArc(
    rightCenter.x,
    rightCenter.y,
    outerRadius,
    rightApexAngle,
    rightSpringAngle,
    arcSamples,
  ).slice(1)

  const intrados = [...intradosLeft, ...intradosRight]
  const extrados = [...extradosLeft, ...extradosRight]
  const archEnvelope = [...extrados, ...[...intrados].reverse()]
  const outerLeftSpring = extradosLeft[0]
  const outerApex = extradosLeft.at(-1)

  const intradosCatenary = sampleCatenary(
    halfSpan,
    pillarHeight,
    pillarHeight + rise,
    catenarySamples,
  )
  const extradosCatenary = sampleCatenary(
    Math.abs(outerLeftSpring.x),
    outerLeftSpring.y,
    outerApex.y,
    catenarySamples,
  )

  const xMin = -halfSpan - pillarWidth - thickness * 1.5
  const xMax = halfSpan + pillarWidth + thickness * 1.5
  const yMin = 0
  const yMax = Math.max(outerApex.y + thickness, pillarHeight + rise + thickness)
  const padding = 0.9
  const usableWidth = xMax - xMin + padding * 2
  const usableHeight = yMax - yMin + padding * 2
  const scale = Math.min(viewBoxWidth / usableWidth, viewBoxHeight / usableHeight)

  const toCanvasPoint = ({ x, y }) => ({
    x: (x - xMin + padding) * scale,
    y: viewBoxHeight - (y - yMin + padding) * scale,
  })

  const toCanvasPath = (points, close = false) => pointsToPath(points.map(toCanvasPoint), close)

  const leftPillarTop = toCanvasPoint({ x: -halfSpan - pillarWidth, y: pillarHeight })
  const leftPillarBottom = toCanvasPoint({ x: -halfSpan, y: 0 })
  const rightPillarTop = toCanvasPoint({ x: halfSpan, y: pillarHeight })
  const rightPillarBottom = toCanvasPoint({ x: halfSpan + pillarWidth, y: 0 })

  return {
    archPath: toCanvasPath(archEnvelope, true),
    intradosPath: toCanvasPath(intrados),
    extradosPath: toCanvasPath(extrados),
    intradosCatenaryPath: toCanvasPath(intradosCatenary),
    extradosCatenaryPath: toCanvasPath(extradosCatenary),
    leftPillar: {
      x: leftPillarTop.x,
      y: leftPillarTop.y,
      width: leftPillarBottom.x - leftPillarTop.x,
      height: leftPillarBottom.y - leftPillarTop.y,
    },
    rightPillar: {
      x: rightPillarTop.x,
      y: rightPillarTop.y,
      width: rightPillarBottom.x - rightPillarTop.x,
      height: rightPillarBottom.y - rightPillarTop.y,
    },
    metrics: {
      section: pillarWidth * pillarWidth,
      intradosLength: radius * Math.abs(chooseUpperDelta(leftSpringAngle, leftApexAngle, leftCenter.y, radius)) * 2,
      catenaryRise: outerApex.y - outerLeftSpring.y,
      apexHeight: pillarHeight + rise,
    },
  }
}

function App() {
  const [parameters, setParameters] = useState(initialParameters)
  const model = useMemo(() => buildModel(parameters), [parameters])

  const handleChange = (key, value) => {
    setParameters((current) => ({
      ...current,
      [key]: Number(value),
    }))
  }

  return (
    <main className="app-shell">
      <section className="intro">
        <div>
          <p className="eyebrow">Dimensionnement d’arc gothique</p>
          <h1>Arc en tiers-point, épaisseur constante et piliers paramétrables</h1>
          <p className="lead">
            Cette application React trace un arc brisé interactif posé sur deux piliers,
            avec son intrados, son extrados et leurs chainettes pour comparer rapidement
            les proportions utiles au dimensionnement.
          </p>
        </div>
        <div className="stats" aria-label="Indicateurs de dimensionnement">
          <article>
            <span>Section estimée du pilier</span>
            <strong>{formatValue(model.metrics.section)} m²</strong>
            <small>Hypothèse d’un pilier carré</small>
          </article>
          <article>
            <span>Longueur d’intrados</span>
            <strong>{formatValue(model.metrics.intradosLength)} m</strong>
            <small>Somme des deux arcs circulaires</small>
          </article>
          <article>
            <span>Sommet de l’arc</span>
            <strong>{formatValue(model.metrics.apexHeight)} m</strong>
            <small>Depuis le pied des piliers</small>
          </article>
          <article>
            <span>Flèche de la chainette extrados</span>
            <strong>{formatValue(model.metrics.catenaryRise)} m</strong>
            <small>Comparaison rapide avec l’arc</small>
          </article>
        </div>
      </section>

      <section className="workspace">
        <form className="controls" aria-label="Paramètres géométriques">
          <h2>Paramètres</h2>
          {controls.map(({ key, label, unit, min, max, step }) => (
            <label key={key} className="control">
              <div className="control-head">
                <span>{label}</span>
                <output htmlFor={key}>
                  {formatValue(parameters[key], step < 0.1 ? 2 : 1)} {unit}
                </output>
              </div>
              <input
                id={key}
                type="range"
                min={min}
                max={max}
                step={step}
                value={parameters[key]}
                onChange={(event) => handleChange(key, event.target.value)}
              />
            </label>
          ))}
        </form>

        <section className="drawing-card">
          <div className="legend" aria-label="Légende du dessin">
            <span>
              <i className="legend-chip arch"></i>
              Arc
            </span>
            <span>
              <i className="legend-chip intrados"></i>
              Intrados
            </span>
            <span>
              <i className="legend-chip extrados"></i>
              Extrados
            </span>
            <span>
              <i className="legend-chip catenary"></i>
              Chainettes
            </span>
          </div>

          <svg
            className="drawing"
            viewBox={`0 0 ${viewBoxWidth} ${viewBoxHeight}`}
            role="img"
            aria-label="Dessin interactif de l’arc gothique et des piliers"
          >
            <rect className="background" x="0" y="0" width={viewBoxWidth} height={viewBoxHeight} />
            <rect {...model.leftPillar} className="pillar" />
            <rect {...model.rightPillar} className="pillar" />
            <path d={model.archPath} className="arch-fill" />
            <path d={model.intradosPath} className="intrados-line" />
            <path d={model.extradosPath} className="extrados-line" />
            <path d={model.intradosCatenaryPath} className="catenary-line" />
            <path d={model.extradosCatenaryPath} className="catenary-line outer" />
          </svg>
        </section>
      </section>
    </main>
  )
}

export default App
