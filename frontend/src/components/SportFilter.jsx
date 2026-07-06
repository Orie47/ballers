import { SPORTS } from '../constants'
import { formatSportLabel } from '../utils/format'

function SportFilter({ value, onChange }) {
  return (
    <select
      value={value}
      onChange={(event) => onChange(event.target.value)}
      className="rounded-md border border-(--border) bg-(--bg) px-3 py-2 text-sm text-(--text-h)"
    >
      <option value="">All sports</option>
      {SPORTS.map((sport) => (
        <option key={sport} value={sport}>
          {formatSportLabel(sport)}
        </option>
      ))}
    </select>
  )
}

export default SportFilter
