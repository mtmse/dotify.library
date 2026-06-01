## Why

For `sv-SE` uncontracted, the Liblouis braille pipeline always places the multi-word
emphasis end-marker `⠱` (U+2831) *before* trailing punctuation, discarding whether the
punctuation was inside or outside the emphasized span. The MTM spec (*Svenska skrivregler
för punktskrift* 2009/2024, §3.4.2) and the legacy `SwedishBrailleTranslator` keep the
distinction: the end-marker follows punctuation that is part of the emphasized span. The
divergence breaks thousands of whole-sentence-emphasis cases in the external regression
suite, blocking any further regression analysis until it is resolved.

## What Changes

- Recover the inside/outside distinction in the Liblouis pipeline: when trailing
  punctuation carries the emphasis typeform (it is *inside* the em/strong span), emit the
  multi-word end-marker `⠱` *after* that punctuation instead of before it. The
  per-character typeform/attribute data needed to decide this is already available in
  `LiblouisBrailleFilter`.
- Leave the punctuation-OUTSIDE case unchanged — both the Liblouis and legacy paths
  already agree there (marker before punctuation).
- Applies to multi-word italic (`⠠⠤ … ⠱`) and multi-word bold (`⠨⠨ … ⠱`).
- **BREAKING** (intended): reverts the documented "always-swap" trade-off. Output for
  emphasized spans that include trailing punctuation changes from `…⠱<punct>` to
  `…<punct>⠱`. This realigns the Liblouis output with the legacy translator, the MTM
  spec, and the regression baseline.
- Update `EmphasisPunctuationMatrixTest` so the comma-INSIDE rows and a new
  whole-sentence period-INSIDE row assert the markup-faithful output (`⠂⠱` / `⠄⠱`).
- Add an integration PEF fixture and `EmTagTest` case for the whole-sentence em with a
  period inside the span.
- Update `BREAKING_CHANGES.md` item 6 to record that the always-swap trade-off is reverted.
- Fix a related, pre-existing Liblouis bug: **partial-word emphasis** (emphasis covering
  only part of a word, e.g. `<strong>D</strong>en`) injected a word-boundary blank cell
  mid-word (`⠨⠠⠙⠀⠑⠝`). Drop that injected blank so the word stays intact (`⠨⠠⠙⠑⠝`),
  matching the legacy translator. Real spaces (whitespace source character) are preserved.

## Capabilities

### New Capabilities
- `liblouis-emphasis-punctuation`: Defines where the Swedish multi-word emphasis
  end-marker `⠱` is placed relative to adjacent punctuation in the Liblouis pipeline,
  based on whether the punctuation is inside or outside the emphasized span.

### Modified Capabilities
<!-- No existing specs in openspec/specs/; nothing to modify. -->

## Impact

- **Code**: `src/org/daisy/dotify/translator/impl/liblouis/LiblouisBrailleFilter.java`
  (post-process Liblouis output to move the end-marker past trailing in-span punctuation).
- **Tests**: `test/.../liblouis/EmphasisPunctuationMatrixTest.java`;
  new `integrationtest/.../formatter/test/EmTagTest.java` case + PEF fixture.
- **Docs**: `BREAKING_CHANGES.md` item 6.
- **Output behavior**: changes only for emphasized spans whose trailing punctuation is
  inside the span. No change when there is no emphasized trailing punctuation, so the
  change is a strict superset cross-locale.
- **Out of scope / unaffected**: the separate caps end-marker gap (e.g. `ISBN-centralen`,
  where Liblouis omits `⠱` entirely); translator selection/priority between the legacy and
  Liblouis factories; any public API signatures.
