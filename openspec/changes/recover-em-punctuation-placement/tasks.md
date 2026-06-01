## 1. Confirm Liblouis cell/attribute behavior (probe before coding)

- [x] 1.1 Probed `två ord,` (comma INSIDE). Finding: inserted `⠱` end-marker's
      `charAtt` points to the FOLLOWING punctuation's source index (marker and comma both
      map to source index 7 = `,`). Cells/charAtt are identical for inside vs outside; only
      the emphasis mask differs. interCharAtt is all 0 in the region.
- [x] 1.2 Guard chosen: treat a `⠱` cell as the emphasis end-marker only when its
      `charAtt` source char is swappable punctuation AND emphasized. A literal `⠱`-letter
      would map to itself (a letter, not punctuation), so it cannot misfire.
- [x] 1.3 Probed `blaû,`: `û` renders as `⠈⠥` (not `⠱`) and single words use single-word
      emphasis (no end-marker). The spec's "û = ⠱" is the unbraille-display direction only.

## 2. Core implementation in LiblouisBrailleFilter

- [x] 2.1 Add a private constant for the emphasis end-marker cell `⠱` and a helper
      `isSwappablePunctuation(int sourceCodePoint)` based on the Swedish terminal
      punctuation set.
- [x] 2.2 Thread the emphasis information into `toBrailleFilterString`: pass `typeForm`
      (or a derived `boolean[] isEmphasized`, true when `typeForm[i] != null &&
      typeForm[i] != PLAIN_TEXT`) through both the `TranslationResult` overload and the
      array overload. Update both `filter(Translatable)` and
      `filter(TranslatableWithContext)` call sites.
- [x] 2.3 Implement a pre-reconstruction pass over the Liblouis braille cells that, for
      each inserted `⠱` end-marker immediately followed by a contiguous run of in-span
      (emphasized) punctuation cells, rotates the `⠱` to the end of that run — moving the
      matching entries of `charAtts` and `interCharAttr` in lockstep.
- [x] 2.4 Guard the swap: only perform it when every inter-character attribute in the
      affected region is `LIBLOUIS_NO_BREAKPOINT`; otherwise leave the cells unchanged.
- [x] 2.5 Keep the existing soft-hyphen / zero-width-space reconstruction and
      `stripStraySpaceAfterEmphasizedWord` behavior intact and ordered correctly relative
      to the new swap.

## 3. Unit tests (EmphasisPunctuationMatrixTest + LiblouisBrailleFilterTest)

- [x] 3.1 Update the "multi-word em, comma INSIDE" and "long em phrase, comma INSIDE" rows
      to assert the markup-faithful output (`…⠕⠗⠙⠂⠱`) instead of the always-swap value.
- [x] 3.2 Update the "multi-word strong, comma INSIDE" row similarly (`…⠋⠑⠞⠁⠂⠱`).
- [x] 3.3 Add a "whole-sentence em, period INSIDE" row for
      `Till Sara Mondani, som alltid kunde nysta fram historier.` asserting the run ends
      `…⠓⠊⠎⠞⠕⠗⠊⠑⠗⠄⠱`.
- [x] 3.4 Add a "whole-sentence em, period OUTSIDE" row asserting it still ends
      `…⠓⠊⠎⠞⠕⠗⠊⠑⠗⠱⠄` (unchanged), proving only the inside case moved.
- [x] 3.5 Update the test's class/row comments so they no longer describe the comma-INSIDE
      cases as a legitimate divergence/always-swap trade-off.
- [x] 3.6 Add a regression test for the literal-`⠱`-letter edge case (`<em>…û,</em>`) from
      task 1.3, asserting no spurious swap.
- [x] 3.7 Add a strict-superset guard test: an input with no emphasized trailing
      punctuation (e.g. `ord mer text`) produces byte-identical output to before.

## 4. Integration test (formatter pipeline)

- [x] 4.1 Add an OBFL fixture
      `integrationtest/.../resource-files/em-punctuation-inside-input.obfl` with a block
      `<style name="em">Till Sara Mondani, som alltid kunde nysta fram historier.</style>`.
- [x] 4.2 Generate the expected PEF and add it as
      `em-punctuation-inside-expected.pef`, with the final row ending `…⠞⠕⠗⠊⠑⠗⠄⠱`.
- [x] 4.3 Add a test method in `EmTagTest` that runs the new fixture through `testPEF`.

## 6. Partial-word emphasis injected-space fix (folded in)

- [x] 6.1 Probe confirmed: liblouis injects a blank cell (⠀) when single-word emphasis
      ends mid-word; the blank's source char is the emphasized letter (non-whitespace).
      Legacy translator produces no such blank.
- [x] 6.2 In the reconstruction loop, drop an output ⠀ cell whose source character is
      non-whitespace AND emphasized (the injected mid-word boundary). Real spaces have a
      whitespace source char and are preserved by the existing branch.
- [x] 6.3 Matrix rows: partial-word strong (D]en, De]n) and partial-word em (d]et) assert
      the intact-word output; the existing `ord mer text` row guards real-space preservation.
- [x] 6.4 Integration: `em-partial-word-input.obfl` + expected PEF, exercised by a new
      `EmTagTest` method (the user's `<strong>D</strong>en lysande …` case).
- [x] 6.5 Full in-repo suite green after the change.

## 5. Docs and verification

- [~] 5.1 BREAKING_CHANGES.md is NOT on this branch — it lives on
      `docs/breaking-changes-mtm-discussion`. Cross-branch update required there: item 6's
      "Trade-off: comma-INSIDE-em cases lose markup-literal placement" section is now
      outdated. The trade-off is REVERTED — the liblouis pipeline recovers the markup
      boundary via an attribute-derived emphasis mask (NOT the "option D" sentinel design
      they expected), placing the end-marker after in-span punctuation, matching the legacy
      translator and MTM §3.4.2. Deferred to the docs branch owner; flagged to the user.
- [x] 5.2 Run the in-repo suite (`EmphasisPunctuationMatrixTest`, `LiblouisBrailleFilterTest`,
      `EmTagTest`) and confirm green.
- [x] 5.3 Sanity-check a couple of non-Swedish locales (or a no-emphasis Swedish sample) to
      confirm output is unchanged where no emphasized trailing punctuation is present.
