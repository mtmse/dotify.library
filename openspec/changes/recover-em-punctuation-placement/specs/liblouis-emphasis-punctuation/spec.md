## ADDED Requirements

### Requirement: Multi-word emphasis end-marker respects the emphasized span boundary

The system SHALL place the multi-word emphasis end-marker (`⠱`, U+2831) for italic
(`⠠⠤ … ⠱`) and bold (`⠨⠨ … ⠱`) at the true end of the emphasized span in the Liblouis
`sv-SE` uncontracted pipeline. When punctuation immediately follows the last emphasized
word, the end-marker MUST be emitted before that punctuation if the punctuation is outside
the emphasized span, and after that punctuation if the punctuation is part of the span
(i.e. carries the emphasis typeform). This matches the legacy `SwedishBrailleTranslator`
and MTM *Svenska skrivregler för punktskrift* (2009/2024) §3.4.2.

#### Scenario: Trailing comma outside the emphasized span

- **WHEN** the input is `två ord,` with italic emphasis covering only `två ord` (the
  comma is not emphasized)
- **THEN** the output is `⠠⠤⠞⠧⠡⠀⠕⠗⠙⠱⠂` (end-marker `⠱` before the comma `⠂`)

#### Scenario: Trailing comma inside the emphasized span

- **WHEN** the input is `två ord,` with italic emphasis covering `två ord,` (the comma is
  emphasized)
- **THEN** the output is `⠠⠤⠞⠧⠡⠀⠕⠗⠙⠂⠱` (end-marker `⠱` after the comma `⠂`)

#### Scenario: Whole sentence emphasized with a trailing period inside the span

- **WHEN** the input is `Till Sara Mondani, som alltid kunde nysta fram historier.` with
  italic emphasis covering the entire string including the final period
- **THEN** the emphasized run ends `…⠓⠊⠎⠞⠕⠗⠊⠑⠗⠄⠱` (end-marker `⠱` after the period `⠄`)

#### Scenario: Trailing period outside the emphasized span

- **WHEN** the input is `Till Sara Mondani, som alltid kunde nysta fram historier.` with
  italic emphasis covering everything except the final period
- **THEN** the emphasized run ends `…⠓⠊⠎⠞⠕⠗⠊⠑⠗⠱⠄` (end-marker `⠱` before the period `⠄`)

#### Scenario: Multi-word bold with trailing punctuation inside the span

- **WHEN** the input is `två feta,` with bold emphasis covering `två feta,` (the comma is
  emphasized)
- **THEN** the output is `⠨⠨⠞⠧⠡⠀⠋⠑⠞⠁⠂⠱` (end-marker `⠱` after the comma `⠂`)

### Requirement: Output is unchanged when no emphasized trailing punctuation is present

The end-marker repositioning SHALL apply only to a multi-word emphasis run that is
immediately followed by punctuation belonging to the span. For any other input the
Liblouis output MUST be byte-identical to the previous behavior, so the change is a strict
superset across locales.

#### Scenario: Emphasized phrase followed by a space and more text

- **WHEN** the input is `ord mer text` with italic emphasis covering only `ord`
- **THEN** the output is `⠠⠄⠕⠗⠙⠀⠍⠑⠗⠀⠞⠑⠭⠞`, unchanged from prior behavior

#### Scenario: Single-word emphasis with a following period

- **WHEN** the input is `ord.` with italic emphasis covering `ord`
- **THEN** the output is `⠠⠄⠕⠗⠙⠄`, unchanged from prior behavior (single-word emphasis has
  no end-marker)

### Requirement: Partial-word emphasis does not introduce a word-boundary space

The system SHALL NOT introduce a word-boundary blank when single-word emphasis (`⠨` bold /
`⠠⠄` italic) covers only part of a word. Liblouis injects such a blank where the emphasis
ends mid-word; it MUST be dropped so the word stays intact, matching the legacy
`SwedishBrailleTranslator`. A blank cell whose source character is whitespace is a real
space and MUST be preserved.

#### Scenario: Strong over the first letter of a word

- **WHEN** the input is `Den` with bold emphasis covering only `D`
- **THEN** the output is `⠨⠠⠙⠑⠝` (no blank between the emphasized `D` and the rest of the word)

#### Scenario: Emphasis ending after two letters of a word

- **WHEN** the input is `Den` with bold emphasis covering `De`
- **THEN** the output is `⠨⠠⠙⠑⠝`

#### Scenario: Real space after an emphasized word is preserved

- **WHEN** the input is `ord mer text` with italic emphasis covering `ord`
- **THEN** the output is `⠠⠄⠕⠗⠙⠀⠍⠑⠗⠀⠞⠑⠭⠞` (the space between `ord` and `mer` is kept)
