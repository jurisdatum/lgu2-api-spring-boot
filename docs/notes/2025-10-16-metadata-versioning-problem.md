# Metadata Version Logic – Problem Statement

This note explains how MarkLogic’s `legislation.xq` pipeline emits version
metadata (via Atom `hasVersion` links and fragment attributes) and how the
`uk.gov.legislation.transform.simple.Metadata` class interprets it. Future
changes to either the XQuery or the Java logic should revisit these assumptions.

## Context
- Source metadata comes from Atom feeds that describe legislation XML snapshots. Each snapshot may include multiple `<atom:link rel="http://purl.org/dc/terms/hasVersion">` entries whose `@title` values we ingest verbatim.
- The feeds are inconsistent: early versions often list only `"current"` alongside the snapshot being fetched, while revised documents may include a full history of ISO-dated revisions mixed with human labels such as `"enacted"` or `"made"`.
- Downstream consumers need two derived views:
  1. `version()` – a single label representing the version that the current XML payload describes.
  2. `versions()` – an ordered set of all meaningful version labels (initial label, dated revisions, “prospective” etc.) suitable for UI widgets.

## Observed Challenges
- **Ambiguous “current” links.** `"current"` may point to an actual dated revision or stand in for “prospective” when no dated revisions exist yet. The feed does not tell us which meaning applies.
- **Sparse initial releases.** First-publication XML sometimes lacks any dated entries beyond `"current"`, so we must infer the existence of a prospective version without explicit evidence.
- **Mixed labels and dates.** The feed can contain both natural-language labels and ISO dates. Ordering and deduplication must be stable so clients see predictable sequences.
- **Fragment snapshots lagging behind parent documents.** For sections or sub-sections, the `dct:valid` date can be newer than any explicit version label because other parts of the document have been amended. We need to detect that anomaly and surface the latest actual change instead of the theoretical document-level valid date.
- **Prospective detection.** Only the first descendant node within `<descendants>` carries the status flag relevant to the current snippet. We have to treat that as authoritative while ignoring the rest.

## Desired Behaviour
1. **Single version label (`version()`).**
   - Return the document-type-specific first label (`enacted`, `made`, …) when the snapshot is the initial publication (`dct:valid` missing).
   - Return `"prospective"` when the active node is marked `Status="Prospective"` and no dated history exists.
   - Otherwise return the best-known ISO date: prefer the latest dated entry in `versions()` unless it is younger than the fragment’s own `dct:valid`, in which case fall back to the fragment’s `dct:valid`.
2. **Complete ordered set (`versions()`).**
   - Start from the raw titles, remove `"current"`, normalise `"… repealed"` suffixes, and sort by the existing comparator (initial labels → dates → `"prospective"`).
   - Inject the first-version label for `status="final"` payloads so clients always see it.
   - For revised material, add the authoritative `dct:valid` date back in (unless we already resolved to `"prospective"`), ensuring the active snapshot appears exactly once.
   - Append `"prospective"` when `version()` resolves to that label, guaranteeing consistency between the scalar and the collection.

## What the MarkLogic XQuery Actually Emits
- The authoritative feed is built in `src/legislation/queries/legislation.xq` (plus a condensed variant in `large-data.xq`). The `<atom:link rel="…hasVersion">` entries come from the block starting near line 1120.
- `utils:find-valid-dates-lang` always yields the first-version label for “final” material by calling `utils:enacted` in `utils.xq`. The XQuery deliberately suppresses that label when the request already targets `/enacted`, `/made`, etc. (`not($version = $utils:inforceStrings)`), so the feed for the first version does **not** repeat its own label.
- `"current"` is appended only when `$version` is a dated revision *and* there are any dated values in the index. If there are no dated entries but a prospective snapshot exists, the same slot is filled with `"prospective"` instead.
- Fragment-level dates (`@ValidDates`, `search:get-alt-dates`) flow into `$altDates`. The loop that writes `<hasVersion>` links iterates over those values, normalises `"current"` to the empty string in the URI, and appends `" repealed"` when the date falls beyond the last active `RestrictEndDate`.
- Welsh items clone the entire link set per language when the alternate fragment is available (see lines 1230–1274). The large-document export path mirrors the same logic at `large-data.xq:398-406`.
- The feed treats in-force versus revised material as two distinct universes. A document can have only the first version (no revised collection) or only revisions (no final collection), and the XQuery adjusts the link set accordingly.

## Updated Questions / Follow-ups
- Given the explicit suppression of first-version labels in the `/enacted` responses, we should keep reinjecting that label in `versions()` for `status="final"` payloads. No further feed samples are needed to prove the gap.
- Still worth considering whether we should record how `"prospective"` was inferred (explicit vs. fallback) for analytics/debugging.
- If descendant status becomes unreliable, revisiting the transformation to capture it earlier might simplify the Java-side heuristics.

This note captures both the downstream requirements and the upstream feed behaviour so future changes can revisit the assumptions with concrete data.

run `codex resume 0199ee4b-2729-7ab1-bace-c0b02b96db07`
