# Status Messages

## Red/Green "Up to Date" Messages

### Rule
A resource is up to date if it has no required, unapplied effects that are in force on or before the cutoff date. In other words, for every unapplied required effect, all its in‑force dates are either missing or after the cutoff.

In Simon's words: A resource is up to date if: all required-to-be-applied effects affecting it, its descendants, and—if it is a fragment of a whole document—its ancestors, are applied for all those effects' in force dates dated today and earlier.

### New Algorithm
1. Choose a cutoff date (normally today).
2. For each effect in the resource's list of unapplied effects (see step 4 for scoping), mark whether the effect is outstanding.
    - If the effect is already marked applied, it is not outstanding.
    - If the effect is not required (the `required` field on the effect), it is not outstanding.
    - Otherwise, inspect each in-force entry on the effect; an in-force entry is outstanding only if
        - it has a non-null date, and
        - that date is on or before the cutoff.
    - The effect is outstanding if any of its in-force entries are outstanding.
3. The resource (document or fragment) is up to date if none of its effects are outstanding.
4. For full documents, only the document's `unappliedEffects` list is considered. For fragments, both the fragment-targeting list and the ancestor-targeting list are considered; any outstanding effect in either list means the fragment is not up to date.

Code: `src/main/java/uk/gov/legislation/util/UpToDate.java`

### Old Algorithm (XSLT legacy system)
1. The cutoff date is always today (`current-date()`).
2. Determine whether the document is current Welsh. This controls which attribute is used for the "required" check throughout.
3. Check whether any outstanding effects exist: count the `UnappliedEffect` elements where the required attribute is not `'false'` (using `@RequiresWelshApplied` for Welsh documents, `@RequiresApplied` otherwise). If the count is zero, the document is up to date.
4. If outstanding effects do exist, check whether they are all prospective or future-dated: for every in-force entry across all required unapplied effects, verify that it either
    - has `@Prospective='true'`, or
    - has a `@Date` that is after today (with a guard that the date is castable as `xs:date`).
5. If all in-force entries satisfy step 4, the document is still treated as up to date (green message). Otherwise it is not up to date (red message).

Note: unlike the new algorithm, the XSLT version has no effect-level "applied" short-circuit — effects are already filtered to `UnappliedEffect` elements by the data source. It also handles a "prospective" concept that the new algorithm does not.

Code: `tna.legislation.transformations.clml-html-fo/src/legislation/html/statuswarning.xsl`— functions `leg:IsOutstandingEffectExists` and `leg:IsOutstandingEffectsOnlyProspectiveOrFutureDate`

### Material Differences

| Aspect | New (Java) | Old (XSLT) |
|---|---|---|
| Welsh-specific filtering | No (not yet implemented) | Yes (`RequiresWelshApplied`) |
| Effect-level `applied` check | Yes | No (implicit via `UnappliedEffect` element) |
| Prospective effects | No (prospective flag is not checked) | Treated as non-outstanding |
| Date validation | Assumes valid | `castable as xs:date` guard |
