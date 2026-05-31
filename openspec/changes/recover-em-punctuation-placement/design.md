## Context

`LiblouisBrailleFilter` translates text by calling `table.translate(...)` and then
reconstructing a `BrailleFilter`-compatible string in `toBrailleFilterString(...)`. For
multi-word emphasis, Liblouis emits the end-marker `⠱` (U+2831) immediately after the last
emphasized *word character* and before any trailing punctuation — regardless of whether
that punctuation was inside or outside the emphasized span. So `<em>två ord,</em>` (comma
inside) and `<em>två ord</em>,` (comma outside) both come out of Liblouis as
`⠠⠤⠞⠧⠡⠀⠕⠗⠙⠱⠂`.

The legacy `SwedishBrailleTranslator` (and the MTM spec §3.4.2) keep the distinction:
comma-outside → `…⠙⠱⠂`, comma-inside → `…⠙⠂⠱`. The regression baseline encodes the legacy
behavior, so the always-before-punctuation Liblouis output fails thousands of
whole-sentence-emphasis cases.

The information needed to tell the cases apart is already present in the filter: the
per-character `Typeform[] typeForm` array (and the `TextAttribute`) records which input
characters are emphasized. The Liblouis `TranslationResult` also exposes, per output cell,
the source input index (`getCharacterAttributes()`, used here as `charAtts[outputIndex] =
inputIndex`). Combining the two lets us decide, for the punctuation cell that follows a
`⠱`, whether its *source* character was emphasized.

## Goals / Non-Goals

**Goals:**
- Place the multi-word emphasis end-marker `⠱` after trailing punctuation that is part of
  the emphasized span, and before punctuation that is not — matching the legacy translator
  and the MTM spec, for both italic (`⠠⠤ … ⠱`) and bold (`⠨⠨ … ⠱`).
- Make the change a strict superset: byte-identical output for any input that does not have
  an emphasized multi-word run immediately followed by in-span punctuation.

**Non-Goals:**
- The caps/versal end-marker gap (e.g. `ISBN-centralen`, where Liblouis omits `⠱`
  entirely). Caps come from letter case, not a `TextAttribute`, so the emphasis-typeform
  guard naturally excludes them.
- Single-word emphasis (`⠠⠄` / `⠨`), which has no end-marker.
- Translator selection/priority between the legacy and Liblouis factories.
- Liblouis table (`sv-mtm-g0.utb`) edits — the fix is entirely in Java post-processing.

## Decisions

### D1: Fix by post-processing the Liblouis cell output, not by changing the table

The table currently has no `noemphchars` directive (it was removed because it
mis-positioned the phrase-end marker — the "röra" pattern, BREAKING_CHANGES item 6).
Re-introducing table directives risks regressing that fix and is hard to scope to "only
in-span punctuation." A Java post-process keeps the table stable and mirrors the existing
`stripStraySpaceAfterEmphasizedWord` approach. **Alternative considered:** manipulating
`typeForm` so Liblouis emits the marker later — rejected; Liblouis decides marker placement
internally and does not expose that control.

### D2: Detect the swap with the emphasis-typeform of the *following* punctuation's source char

Operate on the Liblouis braille cells before the existing whitespace/soft-hyphen
reconstruction, with access to `charAtts` (output→input index) and an
`isEmphasized[inputIndex]` mask derived from `typeForm` (`typeForm[i] != null &&
typeForm[i] != PLAIN_TEXT`). For each `⠱` cell at output index `k`, scan the following
cells `k+1, k+2, …` while each one's *source* character (`inputCodePoints[charAtts[j]]`) is
punctuation **and** `isEmphasized[charAtts[j]]` is true. If at least one such cell exists,
rotate the `⠱` to the end of that punctuation run (`…⠙⠱⠂` → `…⠙⠂⠱`). The comma-OUTSIDE case
does not match (its comma's source char is not emphasized), so it is left untouched.

"Punctuation" is judged from the source character (locale-agnostic) — the terminal set
relevant to Swedish (`. , ; : ! ?` and the closing marks already enumerated in
`SWEDISH_TERMINAL_PUNCT_CELLS`). Using the source char rather than the cell avoids
re-deriving punctuation identity from braille.

### D3: Thread `typeForm` (as an `isEmphasized` mask) into `toBrailleFilterString`

Both `filter(Translatable)` and `filter(TranslatableWithContext)` already build `typeForm`.
`toBrailleFilterString` and its `TranslationResult` overload are package-private static
helpers (not public API), so adding the parameter is safe. No public signatures change.

### D4: Reorder `charAtts` / `interCharAttr` in lockstep, and guard on break attributes

The reconstruction loop indexes `charAtts` and `interCharAttr` by output position, so the
rotation must move their entries together. A soft hyphen / zero-width space practically
never sits between a word's terminal punctuation and the end-marker; to keep the transform
provably safe we **only perform the swap when every inter-character attribute in the
affected region is `LIBLOUIS_NO_BREAKPOINT`**, and skip (leaving prior output) otherwise.

## Risks / Trade-offs

- **A literal `⠱`-producing source letter (e.g. `û`, `š`) at the end of an emphasized word,
  followed by in-span punctuation, could be mistaken for the inserted end-marker** (e.g.
  `<em>blaû,</em>`), producing a wrong swap. → Mitigation: before treating a `⠱` cell as
  the end-marker, confirm it is an *inserted* indicator, not a cell produced by a source
  `⠱`-letter. During implementation, empirically inspect the `charAtts` value Liblouis
  assigns to the inserted marker (e.g. whether it duplicates the previous input index)
  and add a guard plus a dedicated `<em>…û,</em>` regression test.
- **Multiple / mixed trailing punctuation** (`<em>ord?!</em>`, or partly-in/partly-out). →
  The scan handles a contiguous run of emphasized punctuation and stops at the first
  non-emphasized or non-punctuation cell, which is the spec-faithful behavior.
- **Reverting the documented always-swap trade-off is a behavior change** for any document
  with emphasized trailing punctuation. → Intended and the entire point; realigns with the
  regression baseline. Captured as BREAKING in the proposal and in BREAKING_CHANGES item 6.
- **Interaction with line breaking.** → Translation (and thus the swap) happens per segment
  before line breaking; the marker and its punctuation stay adjacent within a segment, so
  the swap is unaffected by row wrapping (confirmed by formatter probe).

## Open Questions

- Exact `charAtts` value Liblouis assigns to the inserted end-marker — determines the
  cleanest guard for D2/the risk above. To be resolved by a small probe during
  implementation before finalizing the detection predicate.
- Final punctuation set for the swap: start from `SWEDISH_TERMINAL_PUNCT_CELLS` mapped to
  source chars; confirm against regression samples whether closing quotes/parentheses
  inside a span also need it.
