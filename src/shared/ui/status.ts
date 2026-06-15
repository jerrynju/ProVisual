/*
 * Shared status → label/color mapping (§3.6). Domain-neutral.
 */
import type { Status, Severity } from '../types'

export const STATUS_LABEL: Record<Status, string> = {
  unconfigured: '未配置',
  ready: '就绪',
  running: '运行中',
  done: '已完成',
  stale: '需更新',
  warning: '警告',
  error: '错误',
  disabled: '已禁用',
}

export const STATUS_COLOR: Record<Status, string> = {
  unconfigured: 'var(--color-disabled)',
  ready: 'var(--color-primary)',
  running: 'var(--color-info)',
  done: 'var(--color-success)',
  stale: 'var(--color-stale)',
  warning: 'var(--color-warning)',
  error: 'var(--color-error)',
  disabled: 'var(--color-disabled)',
}

export const STATUS_SOFT: Record<Status, string> = {
  unconfigured: 'var(--color-disabled-soft)',
  ready: 'var(--color-primary-soft)',
  running: 'var(--color-primary-soft)',
  done: 'var(--color-success-soft)',
  stale: 'var(--color-stale-soft)',
  warning: 'var(--color-warning-soft)',
  error: 'var(--color-error-soft)',
  disabled: 'var(--color-disabled-soft)',
}

export const SEVERITY_STATUS: Record<Severity, Status> = {
  error: 'error',
  warning: 'warning',
  info: 'ready',
}

export const SEVERITY_LABEL: Record<Severity, string> = {
  error: '错误',
  warning: '警告',
  info: '提示',
}
