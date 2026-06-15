/*
 * Shared issue card (§3.4.8, §5.3 最小问题 schema). Not a log line:
 * severity · title · affected object · explanation · suggested actions · jump.
 */
import type { Issue, SuggestedAction } from '../types'
import { SEVERITY_LABEL, SEVERITY_STATUS, STATUS_COLOR, STATUS_SOFT } from './status'

interface Props {
  issue: Issue
  onAction?: (action: SuggestedAction) => void
  onJump?: (uid: string) => void
}

export function IssueCard({ issue, onAction, onJump }: Props) {
  const status = SEVERITY_STATUS[issue.severity]
  return (
    <div className="issue" style={{ borderLeft: `3px solid ${STATUS_COLOR[status]}` }}>
      <div className="issue__head">
        <span className="issue__sev" style={{ color: STATUS_COLOR[status], background: STATUS_SOFT[status] }}>
          {SEVERITY_LABEL[issue.severity]}
        </span>
        <span className="issue__title">{issue.title}</span>
      </div>
      {issue.affectedLabel && (
        <div className="issue__affected">
          影响对象：
          {issue.jumpTarget ? (
            <button className="issue__link" onClick={() => onJump?.(issue.jumpTarget!.uid)}>
              {issue.affectedLabel}
            </button>
          ) : (
            <strong>{issue.affectedLabel}</strong>
          )}
        </div>
      )}
      <p className="issue__explain">{issue.explanation}</p>
      {issue.suggestedActions.length > 0 && (
        <div className="issue__actions">
          {issue.suggestedActions.map((a, i) => (
            <button key={i} className="btn btn--sm btn--secondary" onClick={() => onAction?.(a)}>
              {a.label}
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
