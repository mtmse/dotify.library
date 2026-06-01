# CLAUDE.md

Project-specific notes for working in this repository.

## Building and testing

This is an old Java codebase built with **Gradle 6.7** (wrapper included). It must be
built and tested with **JDK 11** — newer JDKs break the Gradle 6.7 compiler worker
(`IllegalAccessError` against `com.sun.tools.javac`).

Run the full test suite:

```sh
JAVA_HOME=/home/woden/jdk-11.0.2/ ./gradlew test
```

Run a single test class (or method):

```sh
JAVA_HOME=/home/woden/jdk-11.0.2/ ./gradlew test --tests "org.daisy.dotify.translator.impl.liblouis.EmphasisPunctuationMatrixTest"
```

In-repo tests live under `test/` and `integrationtest/` (both are part of the same `test`
source set). Gradle swallows test stdout; to inspect output from a probe, write it to a
file (e.g. under `/tmp`) rather than `System.out`.

## Liblouis tables (Swedish)

The Swedish liblouis translator resolves its table (`sv-mtm-g0.utb`) through the native
liblouis library. By default that picks up the **system** table at
`/usr/share/liblouis/tables/`, which is **not** the same as the table used by the external
regression server.

The authoritative, deployed table is the **staging** copy at `/tmp/sv-louis-staging/`. To
run tests against the table the regression server actually uses, point liblouis at it with
`LOUIS_TABLEPATH`:

```sh
JAVA_HOME=/home/woden/jdk-11.0.2/ LOUIS_TABLEPATH=/tmp/sv-louis-staging ./gradlew test
```

Always verify Swedish emphasis/caps behaviour against the staging table — the system table
differs (e.g. `lencapsphrase` value, `noemphchars` directives) and can give misleading
results.

Notes on the staging table relevant to emphasis handling:

- `noemphchars italic/bold \s.,:;!?` is deliberately **removed** (it would re-introduce the
  "röra" phrase-end-marker mispositioning). Its removal makes liblouis emit stray spaces /
  word-boundary blanks around emphasis, which are cleaned up in Java in
  `LiblouisBrailleFilter`.
- A block of `noback pass2 @…-156` rules moves the phrase-end marker `⠱` **before** trailing
  punctuation at the table level (an always-swap). The inside-vs-outside-span distinction is
  recovered in `LiblouisBrailleFilter` (see `moveEndMarkerPastInSpanPunctuation`).
- `lencapsphrase 2` (vs upstream `4`) — a deliberate trade-off for caps phrases, noted in
  `BREAKING_CHANGES.md`.
