# ADR-0001 — Align the prototype to the layered Build Spec (UI scope)

- Status: Accepted
- Date: 2026-06-15

## Context

The prototype began as a single Compose module with RF values hardcoded as
display strings inside the UI (e.g. `"+43.0 dB"`), violating several rules of
*ProRF Agent Build Spec v1.0*: §1 (layering), §6 (no compute in UI), §7
(value + unit + dimension), §9 (Node Card / Inspector structure), §10
(capability gating) and §12 (anti-patterns: RF logic in UI, string-based units).

A full multi-module refactor (separate `:platform`, `:engineering`,
`:domain-rf` Gradle modules) was considered but explicitly de-scoped by the
product owner to "UI-layer alignment" for this iteration, consistent with the
spec's own guidance: *one minimal closed loop → runnable → testable → don't
break architecture → then extend.*

## Decision

Introduce the layers as **packages** inside the app module, with the same hard
boundaries the spec mandates between real modules:

| Layer | Package | Responsibility |
|------|---------|----------------|
| L0 Platform Core | `com.prorf.app.platform` | Graph / Node / Port / Edge, DAG `ExecutionEngine` (topological sort + cache + cycle detection), `NodeRegistry`, `CapabilityService`. No RF, no engineering, no UI. |
| L1 Engineering | `com.prorf.app.engineering` | `Quantity` = value + `Unit` + `Dimension`; log/linear handling; FSPL helper. |
| L3 RF Domain | `com.prorf.app.domain.rf` | RF `NodeDefinition`s with pure executors, the link-budget workflow template, `RfLinkBudget.compute()`. Depends only on platform + engineering. |
| L2 UI | `com.prorf.app.ui` + `com.prorf.app.data.RfPresenter` | Renders `RfResult`; decorates with icons/colours. Performs no RF maths. |
| L4 App Shell | `com.prorf.app` | `MainActivity`, `AppCapabilities` entitlement wiring. |

Concrete UI changes driven by the spec:
- Parameters now carry `Quantity` (value + unit + dimension); the UI formats
  them — no naked doubles, no string-parsed units (§7).
- The link budget is **computed** by the engine and only displayed by the UI
  (§6/§12). `SampleData` delegates the RF chain to `RfPresenter`.
- Inspector tabs are **输入 / 参数 / 输出 / 诊断 / 图表** (§9); Node Cards show
  标题 / 参数摘要 / 状态 / 输出摘要.
- Export is gated through `CapabilityService.has(EXPORT_REPORT)` (§10), not an
  `isProUser` check.

## Alternatives

1. **Full multi-module split** — most faithful to §1/§2, gives module-level
   enforcement and JVM test modules. Deferred: larger build churn than the
   requested UI scope.
2. **Leave hardcoded strings** — rejected; directly violates §7/§12.

## Risks

- Package-level boundaries are convention, not compiler-enforced; a future
  `./gradlew` module split (ADR-0002) would harden them.
- The computation is an MVP cascade (Friis NF over the receive chain only);
  sweep / Monte-Carlo (§5, M-later) are out of scope.

## Verification

JVM unit tests (`./gradlew :app:testDebugUnitTest`):
- `ExecutionEngineTest` — topological execution + cycle detection.
- `RfLinkBudgetTest` — graph executes, output non-empty, EIRP / received power
  match the hand-computed cascade, link margin positive.
