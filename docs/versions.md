# Versions

This is the canonical version-derivation document for the Java API and the website rewrite.

**Terminology:** *milestone* means a first-version keyword (`enacted`, `made`, `created`, `adopted`) or an ISO date; *label* means a milestone or the label-only `"prospective"` value.

The practical question is:

> Given any XML response for a document or fragment, can we derive every available version in scope, with both a user-facing identity and the correct way to fetch it?

For modern observed data, the answer is yes, if we keep these concepts separate:

- the human-readable milestone label
- the URL component used to fetch that milestone
- the point-in-time date requested or carried by the response URI
- the `dct:valid` valid-from date of the returned revised representation

## Core Model

Each available version should be represented as:

- **`label`**: the identity shown to users, such as `enacted`, `made`, `2024-01-01`, or `prospective`.
- **`urlVersion`**: the version segment used in a legislation.gov.uk URL, when such a segment exists.
- **Fetch strategy**: normally use `urlVersion`; when `urlVersion` is absent, use the versionless URL.

In almost all cases, `label` and `urlVersion` are the same:

- `label = "enacted"`, `urlVersion = "enacted"`
- `label = "2024-01-01"`, `urlVersion = "2024-01-01"`

The important modern-data exception is:

- `label = "prospective"`
- `urlVersion = null`
- fetch via the versionless URL

`"current"` is never a final `label` or `urlVersion`. It is an alias that must be replaced or ignored.

The Java API `version` field is not the raw point-in-time date. It is the label yielded by the request. For ordinary revised XML, that means the latest emitted first-version or dated milestone that is not after `dct:valid`, treating `dct:valid` as the returned representation's valid-from date. For prospective revised XML, it is the label-only `"prospective"` value. The requested point-in-time or `dct:valid` date may be later than the selected ordinary milestone.

## Scope

- For a whole-document response, derive the versions of that document.
- For a fragment response, derive the versions of that fragment only, not all versions of the whole document.
- In bilingual XML, derive versions from the `hasVersion` links for the response language only. Do not merge English and Welsh milestone sets.
- A version may be deduced from the XML plus stable endpoint rules. It does not need to be stated literally.
- This document describes modern observed behavior. Legacy or anomalous data may expose raw `"prospective"` links or other patterns and should be handled separately if encountered.

## Java API Contract

The Java API should expose `versions` on every metadata response, including versioned responses. That differs from the MCP server, which currently suppresses `versions` for some versioned tool responses.

The Java API `versions` field is a list of version labels in the relevant scope:

- whole-document labels for whole-document responses
- fragment labels for fragment responses
- same-language labels for bilingual responses

The `version` field identifies the label yielded by the request:

- final/enacted or made XML uses the type-specific first-version keyword
- prospective revised XML uses the label-only `"prospective"` value
- ordinary revised XML uses the latest eligible label from `versions` that is not after `dct:valid`

For ordinary revised XML, eligible labels are the type-specific first-version keyword and ISO date labels. The label-only `"prospective"` value can appear in `versions`; it is selected as the scalar `version` only when the returned revised target is prospective.

Normally, `version` is one of the `versions` labels after normalisation. `dct:valid` may become `version` when it is added to `versions` under the non-prospective recovery rules below. If no first-version keyword or dated milestone on or before `dct:valid` can be derived from `versions`, the implementation falls back to `dct:valid` because that is the only value available that identifies the returned revised snapshot.

### Mapping the Core Model to Java Fields

The Java API flattens the `{label, urlVersion}` pair into plain strings: `version` is a single scalar, `versions` is a list of scalars. For every value except `"prospective"`, `label === urlVersion`, so no disambiguation is needed on the caller side. The one exception is `"prospective"`: it is a label-only value, and callers must know to fetch that content via the versionless URL rather than by appending `/prospective` to the path. The scalar `version` may also be `"prospective"` when the returned revised target is prospective.

The `prospective` signal described below is used internally to synthesize the label-only `"prospective"` value. It is not surfaced as a top-level field on the metadata response; its effect on `versions` is mediated entirely through the list itself. Fragment responses do carry a per-level `prospective` boolean on `fragmentInfo` (and on every entry in `descendants`), which reflects the target fragment's own `@Status` attribute — that's a per-element field, not the top-level signal that drives `versions()`.

## Data Sources

### `hasVersion` Links

The primary source is:

```xml
<atom:link rel="http://purl.org/dc/terms/hasVersion" ... />
```

The link `title` attribute contains the candidate label, typically:

- a first-version keyword, such as `enacted`, `made`, `created`, or `adopted`
- an ISO date, such as `2024-01-01`
- the alias `current`
- occasionally, in legacy/anomalous data, `prospective`

In bilingual XML, `hasVersion` links may carry `hreflang="en"` or `hreflang="cy"`. English and Welsh milestone sets may differ.

### `hreflang`

`hreflang` identifies the language of an individual `hasVersion` link. It is the key to filtering bilingual XML. Untagged links remain in scope because older or single-language XML may not annotate every link.

### Response Language

Determine the response language before normalising version labels.

In the Java API, this normally comes from the simplified metadata language value derived from the XML. If the response language cannot be determined, retain all `hasVersion` links rather than guessing.

### `dct:valid`

`dct:valid` contains the valid-from date of the returned revised representation. It says that the XML snapshot served in this response is valid from that date.

For a fragment response, the returned representation is still served from a containing document snapshot. The requested fragment is the target content inside that snapshot. Therefore the representation's `dct:valid` date can move forward when some other part of the document changes, while the target fragment's own milestone remains unchanged.

- It is absent for final/enacted or made XML.
- It is present for revised XML.
- It is not the requested point-in-time from the URL; that comes from the response URI / `dc:identifier`.
- It is not, by definition, a version label. It may coincide with a version label, especially for whole-document revised responses.
- It is not, by definition, a fragment milestone. A fragment can be served from a later revised representation even when the selected fragment milestone is earlier.
- It can be later than the selected milestone and need not itself be a milestone.
- Different point-in-time requests between two fragment milestones may return different `dct:valid` dates, but still select the same fragment milestone.

### Document Status

`DocumentStatus` controls major normalisation rules:

- `final` means enacted/made/original text.
- `revised` means a revised snapshot with `dct:valid`.

For final XML, the first-version keyword is always derivable from legislation type.

### Prospective Status

`Status="Prospective"` on the target content means the content being viewed is prospective. For a whole document, the target is the root `<Legislation>` element. For a fragment, the target is the fragment element.

The Java transformation produces a `prospective` signal from the target's `@Status` attribute. For `P1` fragments, the status may be carried by the parent `P1group` rather than the `P1` itself, so the target is treated as prospective if either the target element has `Status="Prospective"` or the target is a `P1` whose parent `P1group` has it. This matches the pattern already used when copying status onto descendants entries in `metadata.xsl`.

The `prospective` signal is internal: it drives synthesis of the label-only `"prospective"` value for revised prospective content but is not itself surfaced on the API response.

### First-Version Keyword

Each legislation type has a type-specific first-version keyword:

- primary legislation: `enacted`
- secondary legislation: `made`
- `ukmo` and `ukci`: `created`
- EU retained legislation: `adopted`

## Normalisation Algorithm

Apply this algorithm to produce the Java API `versions` labels.

1. Determine the response language.
2. Collect `title` values from retained `hasVersion` links:
   - retain a link if `hreflang` is absent
   - for an English response, retain a link with `hreflang="en"`
   - for a Welsh response, retain a link with `hreflang="cy"`
   - if the response language cannot be determined, retain all links
3. Strip any trailing `" repealed"` suffix on the retained labels. In practice only the latest dated label ever carries this suffix; stripping on every label is defensive rather than load-bearing.
4. Remove `"current"` from the label set, while remembering whether it was present and whether it was the only retained link.
5. If status is `final`, ensure the first-version keyword is present.
6. If status is `final` and `"current"` was the only retained link, add `"prospective"` as a label-only entry.
7. If status is not `final` and `prospective` is true, add `"prospective"` as a label-only entry.
8. If status is not `final`, `prospective` is false, and `dct:valid` is present, decide whether to add `dct:valid` as a version label:
   - add it if `"current"` was present in the retained links and either
     - the response is whole-document (`dct:valid` is a document milestone), or
     - the response is a fragment that has no other dated labels in the retained set (`dct:valid` stands in as the only date-indexed pointer to the returned representation).
   - otherwise, do not add `dct:valid`.
9. Sort: first-version keywords first, then dates chronologically, then `"prospective"`.

In modern observed data, `"prospective"` enters the `versions` list via synthesis: final XML with standalone `current`, or revised XML whose returned target is prospective. Raw `"prospective"` hasVersion titles — occasionally seen in legacy/anomalous XML, where `/prospective` may resolve as a version path — are explicitly out of scope for this document (see Scope). The flat Java string model cannot distinguish a synthesised label-only `"prospective"` from a raw legacy link, so if such data is ever encountered, callers should assume the modern fetch rule (versionless URL). Handling raw legacy links would require an out-of-band mechanism this contract does not provide.

### Scalar `version` Algorithm

After producing `versions`, derive the Java API `version` field separately.

1. If status is `revised` and `prospective` is true, return `"prospective"`.
2. If `dct:valid` is absent, return the type-specific first-version keyword.
3. If `dct:valid` is present, scan the sorted `versions` labels and keep only:
   - the type-specific first-version keyword
   - ISO date labels that are not after `dct:valid`
4. Return the last retained label.
5. If no label is retained, fall back to `dct:valid`: the response is still a revised snapshot valid from that date, and no eligible milestone from `versions` identifies it.

This is why a fragment can have `dct:valid = "2007-01-01"` and `pointInTime = "2007-01-01"` while the API `version` is `"1991-02-01"`: the later date identifies the returned representation's valid-from date and the requested point-in-time, but `"1991-02-01"` is the selected fragment milestone.

## Sort Order

Version labels should sort as:

- **Rank 0**: first-version keywords: `enacted`, `made`, `created`, `adopted`
- **Rank 1**: ISO dates, sorted chronologically (which coincides with lexicographic order on `YYYY-MM-DD`)
- **Rank 2**: the `"prospective"` label

Within the same rank, labels sort lexicographically.

## The `current` Alias

`current` can appear in `hasVersion` links and in legislation.gov.uk URLs, but it is never a stable version identity.

In final/enacted or made XML:

- If `current` is present and no other retained version labels remain, there are effectively two versions: the first-version form and a current prospective form.
- Represent the first-version form with the first-version keyword.
- Represent the current prospective form as `label = "prospective"`, `urlVersion = null`, fetched via the versionless URL.
- If `current` appears alongside dated versions, ignore it; the dated versions are the real version identities.
- If `current` is absent, only the first-version form exists.

In revised XML:

- `current` may be added by legislation.gov.uk when a specific version was requested.
- It aliases the latest available version and should be removed.
- If the returned target is prospective, synthesize `"prospective"` as a label-only value.
- Use `dct:valid` as a `versions` label only under the non-prospective recovery rules in the normalisation algorithm.

## Why `dct:valid` Is Not Always a Version Label

`dct:valid` identifies the valid-from date of the returned revised representation, but it is not always a milestone in the scope of the response.

For whole-document responses:

- `dct:valid` is the valid-from date of the document snapshot.
- In unversioned revised whole-document XML, it normally appears in `hasVersion`.
- If it is missing from `hasVersion` and the content is prospective, add `"prospective"` so the current revised label is not lost.
- If it is missing from `hasVersion` and the content is not prospective, add `dct:valid` only under the non-prospective recovery rules.

For fragment responses:

- `hasVersion` links are fragment-scoped.
- `dct:valid` remains the valid-from date of the returned representation.
- The document can change on a date when the fragment itself did not change.
- Therefore, do not add `dct:valid` as a fragment version merely because it is present.
- Add `"prospective"` for prospective fragment content to represent the current prospective label.
- Add `dct:valid` as a fallback only for non-prospective fragments when `current` was present and no dated fragment labels remain.

This distinction is why a fragment response can legitimately have `dct:valid = "2024-11-01"` while `version` is an earlier fragment milestone from `versions`. The representation valid-from date is allowed to be later than the selected milestone.

It also means that multiple document snapshots can map to the same fragment milestone. If a fragment last changed on `2021-02-27`, and other parts of the containing document changed on `2021-03-01` and `2021-04-26`, dated fragment requests served from those later document snapshots may carry `dct:valid = "2021-03-01"` or `dct:valid = "2021-04-26"` while the fragment's scalar `version` remains `"2021-02-27"`.

## Language-Aware Version Extraction

Version extraction must be language-aware per link. Do not first classify an entire XML response as bilingual and then apply a separate rule.

Use this rule:

- keep untagged `hasVersion` links
- for English responses, keep `hreflang="en"`
- for Welsh responses, keep `hreflang="cy"`
- if the response language is unknown, keep all links
- normalise only the retained links

This matters because Welsh XML can include both Welsh and English `hasVersion` links in the same response, and the English set may continue later than the Welsh set.

Observed April 2026:

- `wsi/2020/1609/part/3/chapter/1/welsh` has Welsh fragment version links through `2021-02-27`.
- The same Welsh XML also contains later English-only fragment version links, including dates through `2022-03-28`.
- `dct:valid` on the Welsh fragment is `2021-04-26`.
- `/wsi/2020/1609/part/3/chapter/1/2021-03-01/welsh` is also fetchable and has `dct:valid = 2021-03-01`, but the Welsh fragment milestone set still stops at `2021-02-27`.
- A Welsh point-in-time URL such as `/2021-05-17/welsh` is fetchable, but the returned Welsh fragment still has `dct:valid = 2021-04-26`.

So:

- later accepted point-in-time dates are not automatically fragment milestone versions
- `dct:valid` is not automatically the fragment's current milestone date
- different containing-document snapshot dates can still select the same fragment milestone
- same-language `hasVersion` links are the primary source for fragment versions
- the API `version` is selected from the same-language milestone set, so a Welsh `dct:valid` of `2021-04-26` yields `version = "2021-02-27"` when `2021-02-27` is the latest Welsh milestone

## Prospective Content

### Fully Prospective Revised Documents

Observed modern examples include `ukpga/2026/8` and `ukpga/2026/5`.

Pattern:

- `DocumentStatus` is `revised`.
- `dct:valid` is present.
- the root `<Legislation>` has `Status="Prospective"`.
- `hasVersion` links may contain only the first-version keyword.
- there is no `current` link on unversioned requests.
- there is no resolvable `/prospective` version URL.

Rule:

- add `"prospective"` to `versions` as the current revised label
- the scalar `version` then selects `"prospective"`
- treat `"prospective"` as label-only: callers should fetch it via the versionless URL, not `/prospective`
- keep `dct:valid` as the valid-from date of the returned representation, not as the exposed version label

Without this rule, the version list would contain only `enacted` and would miss the current revised version.

The legacy timeline UI supports this label choice. When the selected content is prospective and no `prospective` version link exists, it synthesizes a `prospective` pointer and treats that pointer as current. That supports exposing `prospective` as the label while still treating it as label-only rather than a URL token.

### Explicit Enacted/Made XML With Standalone `current`

Observed April 15, 2026:

- `ukpga/2026/8/enacted/data.xml` has `DocumentStatus="final"` and only `hasVersion="current"`.
- `ukpga/2026/8/data.xml` is prospective, has `dct:valid=2026-03-05`, and is fetched via the versionless URL.
- `ukpga/2026/8/prospective/data.xml` returns `404`.
- `ukpga/2026/8/section/1/enacted/data.xml` has only `hasVersion="current"`.
- `ukpga/2026/8/section/1/data.xml` is prospective and fetched via the versionless fragment URL.
- `ukpga/2026/8/section/1/prospective/data.xml` returns `404`.
- `ukpga/2026/5` shows the same pattern.

Rule:

- remove `current`
- add the first-version keyword
- synthesize `prospective`
- treat `prospective` as label-only: `urlVersion = null`, fetch via the versionless URL

Older amended enacted XML supports the same interpretation:

- `ukpga/2010/15/enacted/data.xml` contains dated `hasVersion` links plus `current`.
- `ukpga/2010/15/section/1/enacted/data.xml` likewise contains dated fragment versions plus `current`.
- In those cases, `current` is just an alias and should not synthesize `prospective`.

### Prospective Fragments Within Revised Documents

When an amendment inserts a provision that is not yet in force, the provision can appear in revised text with `Status="Prospective"`.

Rule:

- treat the fragment as prospective when the target fragment has prospective status
- for `P1` fragments, also honor prospective status inherited from the parent `P1group`
- use the prospective signal to add `"prospective"` as the current label
- outside the prospective and fallback cases, do not use unrelated representation valid-from dates as fragment milestone labels when same-language fragment milestones already exist

## Source-by-Source Rules

### Unversioned Whole-Document XML

Use same-language `hasVersion` links as the base list.

If the document is final:

- add the first-version keyword if needed

If the document is revised and prospective:

- add `"prospective"` as a label-only value

In normal unversioned whole-document responses, `"current"` is not emitted by the server. If a synthetic or anomalous response does include `"current"`, treat it under the versioned whole-document rule: strip it, synthesize `"prospective"` for prospective content, and otherwise add `dct:valid` when it identifies a real version missing from the retained links.

Conclusion: determinative for whole-document versions.

### Unversioned Fragment XML

Use same-language fragment-scoped `hasVersion` links as the base list.

Rules:

- do not treat the list as document-complete
- do not merge language sets
- do not treat `dct:valid` as a fragment version unless a recovery rule applies
- add `"prospective"` for prospective fragment content
- select scalar `version` from the retained fragment labels, not from `dct:valid` automatically; prospective revised fragments select `"prospective"`

Conclusion: determinative for fragment-scoped versions.

### Versioned Whole-Document XML

Use same-language `hasVersion` links as the base list.

Rules:

- remove `current`
- add `"prospective"` for prospective content
- otherwise add `dct:valid` when it identifies a real version missing from the retained links
- select scalar `version` from the normalised document labels

Conclusion: determinative for whole-document versions.

### Versioned Fragment XML

Use same-language fragment-scoped `hasVersion` links as the base list.

Rules:

- remove `current`
- do not add `dct:valid` when the fragment already has dated milestone labels
- add `"prospective"` for prospective fragment content
- add `dct:valid` only under the non-prospective fallback recovery rule
- select scalar `version` from the normalised fragment labels

Conclusion: determinative for fragment-scoped versions.

### Final Unversioned XML

If a document is final and unrevised:

- `hasVersion` may be empty
- `dct:valid` is absent
- derive the first-version keyword from legislation type

Conclusion: determinative.

### Explicit Enacted/Made XML

Rules:

- if the only retained `hasVersion` is `current`, derive the first-version form plus a label-only `prospective` version
- if dated versions are also present, ignore `current`
- if `current` is absent, only the first-version form exists
- scalar `version` is the first-version keyword, not `prospective`

Conclusion: determinative under the two-field model.

## Endpoint Behavior

| Request | Endpoint | `dct:valid` | `current` in links | Java `versions` behavior | Java `version` behavior |
|---------|----------|-------------|--------------------|--------------------------|-------------------------|
| No version, no fragment | `/resources/data.xml` | Present for revised, absent for final | No | Use document milestones; synthesize `prospective` for prospective revised content | First-version keyword for final; `prospective` for prospective revised; otherwise latest document milestone not after `dct:valid` |
| No version, fragment | `/fragment/data.xml` | Present for revised, absent for final | No | Use fragment milestones; synthesize `prospective` for prospective revised content; recover `dct:valid` only under non-prospective fallback rules | First-version keyword for final; `prospective` for prospective revised; otherwise latest fragment milestone not after `dct:valid` |
| Version, no fragment | `/data.xml` | Present for revised | Yes | Strip `current`; synthesize `prospective` for prospective revised content; otherwise add `dct:valid` when needed | `prospective` for prospective revised; otherwise latest document milestone not after `dct:valid` |
| Version, fragment | `/fragment/version/data.xml` | Present for revised | Yes | Strip `current`; synthesize `prospective` for prospective revised content; otherwise do not treat `dct:valid` as a fragment milestone unless recovery applies | `prospective` for prospective revised; otherwise latest fragment milestone not after `dct:valid` |
| Enacted/made unversioned | `/resources/data.xml` | Absent | No | First-version keyword only | First-version keyword |
| Enacted/made explicit | `/enacted/data.xml`, `/made/data.xml`, etc. | Absent | Yes when a non-enacted current version exists | Standalone `current` becomes label-only `prospective`; otherwise ignore `current` | First-version keyword |

Notes:

- `/resources/data.xml` returns 404 for versioned whole-document URLs; versioned whole-document requests use the full `/data.xml` endpoint.
- Fragment `hasVersion` links are scoped to the fragment.
- First-version keywords may be absent from fragment `hasVersion` links even for final XML; add them from legislation type.

## Up-To-Date Gating

The API should compute `upToDate` only for the latest selected label.

Rule:

- derive `versions`
- derive scalar `version`
- compute `upToDate` only when `versions` is non-empty and `version.equals(versions.last())`
- for final XML, also require enriched final effects data
- for revised XML, the latest-label check is sufficient

Do not compare `pointInTime` or `dct:valid` directly with `versions.last()`. A fragment can be served for a later point-in-time because the document changed, while the fragment's selected milestone remains its last own milestone. That is still the latest fragment version and should be eligible for `upToDate`.

## Bottom Line

For modern observed data, the XML is determinative enough to derive every version in scope:

- normally `label === urlVersion`
- `current` is never surfaced directly
- bilingual XML requires per-link language filtering
- fragment versions are fragment-scoped
- `dct:valid` is the representation valid-from date but only sometimes a version label in the response scope
- scalar `version` is the selected label, not the raw point-in-time or `dct:valid` date
- `"prospective"` is the modern label-only value: it can come from explicit final/enacted or made XML with standalone `current`, or from revised XML whose returned target is prospective

## MCP Note

The MCP server follows the same core derivation rules, but its tool responses are not the Java API contract.

Current MCP deviations:

- MCP suppresses `versions` for some versioned tool responses.
- MCP represents prospective state separately from the `versions` labels where possible.
- Tool documentation warns that `version = "prospective"` is not a valid input parameter; when `prospective` appears in `versions`, callers should omit the `version` input to fetch that content.

The Java API should treat this document, not the MCP tool-response shape, as canonical.
