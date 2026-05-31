## 1. Confirm Liblouis cell/attribute behavior (probe before coding)

- [ ] 1.1 Write a throwaway probe (or a temporary unit test) that prints the Liblouis
      `getBraille()`, `getCharacterAttributes()` (output→input map) and
      `getInterCharacterAttributes()` for `två ord,` with the comma INSIDE the em span, and
      record the `charAtts` value assigned to the inserted `⠱` end-marker.
- [ ] 1.2 From 1.1, decide the guard that distinguishes the inserted end-marker from a
      literal `⠱`-producing source letter (e.g. `û`). Capture the conclusion in a comment.
- [ ] 1.3 Probe `<em>blaû,</em>` (literal `û` as the last emphasized letter before in-span
      punctuation) and confirm the chosen guard does not misfire on it.

## 2. Core implementation in LiblouisBrailleFilter

- [ ] 2.1 Add a private constant for the emphasis end-marker cell `⠱` and a helper
      `isSwappablePunctuation(int sourceCodePoint)` based on the Swedish terminal
      punctuation set.
- [ ] 2.2 Thread the emphasis information into `toBrailleFilterString`: pass `typeForm`
      (or a derived `boolean[] isEmphasized`, true when `typeForm[i] != null &&
      typeForm[i] != PLAIN_TEXT`) through both the `TranslationResult` overload and the
      array overload. Update both `filter(Translatable)` and
      `filter(TranslatableWithContext)` call sites.
- [ ] 2.3 Implement a pre-reconstruction pass over the Liblouis braille cells that, for
      each inserted `⠱` end-marker immediately followed by a contiguous run of in-span
      (emphasized) punctuation cells, rotates the `⠱` to the end of that run — moving the
      matching entries of `charAtts` and `interCharAttr` in lockstep.
- [ ] 2.4 Guard the swap: only perform it when every inter-character attribute in the
      affected region is `LIBLOUIS_NO_BREAKPOINT`; otherwise leave the cells unchanged.
- [ ] 2.5 Keep the existing soft-hyphen / zero-width-space reconstruction and
      `stripStraySpaceAfterEmphasizedWord` behavior intact and ordered correctly relative
      to the new swap.

## 3. Unit tests (EmphasisPunctuationMatrixTest + LiblouisBrailleFilterTest)

- [ ] 3.1 Update the "multi-word em, comma INSIDE" and "long em phrase, comma INSIDE" rows
      to assert the markup-faithful output (`…⠕⠗⠙⠂⠱`) instead of the always-swap value.
- [ ] 3.2 Update the "multi-word strong, comma INSIDE" row similarly (`…⠋⠑⠞⠁⠂⠱`).
- [ ] 3.3 Add a "whole-sentence em, period INSIDE" row for
      `Till Sara Mondani, som alltid kunde nysta fram historier.` asserting the run ends
      `…⠓⠊⠎⠞⠕⠗⠊⠑⠗⠄⠱`.
- [ ] 3.4 Add a "whole-sentence em, period OUTSIDE" row asserting it still ends
      `…⠓⠊⠎⠞⠕⠗⠊⠑⠗⠱⠄` (unchanged), proving only the inside case moved.
- [ ] 3.5 Update the test's class/row comments so they no longer describe the comma-INSIDE
      cases as a legitimate divergence/always-swap trade-off.
- [ ] 3.6 Add a regression test for the literal-`⠱`-letter edge case (`<em>…û,</em>`) from
      task 1.3, asserting no spurious swap.
- [ ] 3.7 Add a strict-superset guard test: an input with no emphasized trailing
      punctuation (e.g. `ord mer text`) produces byte-identical output to before.

## 4. Integration test (formatter pipeline)

- [ ] 4.1 Add an OBFL fixture
      `integrationtest/.../resource-files/em-punctuation-inside-input.obfl` with a block
      `<style name="em">Till Sara Mondani, som alltid kunde nysta fram historier.</style>`.
- [ ] 4.2 Generate the expected PEF and add it as
      `em-punctuation-inside-expected.pef`, with the final row ending `…⠞⠕⠗⠊⠑⠗⠄⠱`.
- [ ] 4.3 Add a test method in `EmTagTest` that runs the new fixture through `testPEF`.

## 5. Docs and verification

- [ ] 5.1 Update `BREAKING_CHANGES.md` item 6: record that the always-swap trade-off is
      reverted and that emphasized trailing punctuation now keeps the marker after it.
- [ ] 5.2 Run the in-repo suite (`EmphasisPunctuationMatrixTest`, `LiblouisBrailleFilterTest`,
      `EmTagTest`) and confirm green.
- [ ] 5.3 Sanity-check a couple of non-Swedish locales (or a no-emphasis Swedish sample) to
      confirm output is unchanged where no emphasized trailing punctuation is present.
