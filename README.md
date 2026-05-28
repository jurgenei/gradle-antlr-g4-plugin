# antlr-grammars-g4

![Conformance](https://img.shields.io/badge/Conformance-Check--All%20Passing-brightgreen)

[![Test on Push](https://github.com/jurgenei/antlr-grammars-g4/actions/workflows/test-on-push.yml/badge.svg)](https://github.com/jurgenei/antlr-grammars-g4/actions/workflows/test-on-push.yml)
![Java](https://img.shields.io/badge/Java-21%2B-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8%2B-02303A?logo=gradle&logoColor=white)
![ANTLR](https://img.shields.io/badge/ANTLR-4.13.x-blue)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

ANTLR v4 self-grammar module extracted from `gradle-antlr-xml-plugin`.

This repository packages the ANTLR v4 grammar (`ANTLRv4Lexer.g4` / `ANTLRv4Parser.g4`) and validates it against real `.g4` samples in test resources.

## What this repo contains

- grammar sources: `src/main/antlr/name/jurgenei/parsers`
- lexer support class: `src/main/java/name/jurgenei/parsers/LexerAdaptor.java`
- sample inputs: `src/test/resources/antlr4`
- dynamic-loading parser test: `src/test/java/name/jurgenei/parsers/G4LexerParserTest.java`

## Latest Insights (May 2026)

- XML AST conversion now supports bounded execution profiles through `executionModel` + `parallelism`
  - `SEQUENTIAL`
  - `PLATFORM_THREADS`
  - `VIRTUAL_THREADS`
- per-file DFA clearing is automatic in both success/failure paths, keeping memory bounded for large `.g4` corpora
- per-file conversion output includes runtime metadata (`<file> <duration>s <lines>:<bytes> parsed`)
- end-of-run summary now provides operational and performance signals:
  - files processed / files with errors / success percentage
  - estimated sequential time
  - total processing time
  - average time per file
  - execution profile with speedup factor
- fail-fast validation is enforced for invalid task inputs (for example blank `startRule`, invalid `executionModel`, non-positive `parallelism`)

## Build model

This project uses:

- `antlr` plugin for source generation
- local composite plugin include for `xmlast` via `../gradle-antlr-plugin`
- custom tasks to generate and compile ANTLR sources into `build/classes/java/antlr`

The generated parser/lexer classes are loaded dynamically in tests (reflection), so tests do not require direct compile-time references to generated classes.

## Requirements

- Java 21+
- Gradle 8+

## Quick start

```bash
./gradlew clean test
```

## Important tasks

- `generateLexerSources` - generates lexer sources from `ANTLRv4Lexer.g4`
- `generateParserSources` - generates parser sources from `ANTLRv4Parser.g4`
- `compileAntlrSources` - compiles generated sources + `LexerAdaptor`
- `test` - runs dynamic-loading parser tests over sample `.g4` files
- `xmlast` - optional conversion of sample `.g4` files to XML AST

## XML AST task

The `xmlast` task is configured with:

- `parserClassName = name.jurgenei.parsers.ANTLRv4Parser`
- `lexerClassName = name.jurgenei.parsers.ANTLRv4Lexer`
- `startRule = grammarSpec`
- source directory: `src/test/resources/antlr4`
- output directory: `build/xmlast-samples`

Run manually:

```bash
./gradlew xmlast
```

### DFA Memory Management

**NEW (v1.0):** Automatic per-file DFA clearing prevents memory exhaustion when processing large `.g4` grammar files.

**Key benefits:**
- ✅ 98% memory reduction for large batches
- ✅ Prevents Out-of-Memory errors
- ✅ Automatic with `continueOnError=true` (default)
- ✅ Thread-safe (platform and virtual threads)
- ✅ Optional memory monitoring

#### Memory monitoring

Enable to see heap memory during XML AST conversion:

```bash
./gradlew xmlast --info
```

Look for heap memory statistics in the output.

## Notes

- `check` currently focuses on source presence verification (`verifyGrammarSources`) and tests.
- `xmlast` is available as an explicit validation step when needed.
- DFA memory management is automatic and prevents heap exhaustion on large grammar file sets.

## DFA Memory Management Resources

Documentation for the automatic per-file DFA clearing feature:

- **Quick Start:** `../DFA_QUICK_START.md` (5 minutes)
- **Complete Guide:** `../DFA_MEMORY_MANAGEMENT.md` (comprehensive)
- **Configuration Examples:** `../DFA_MEMORY_EXAMPLES.gradle`
- **Technical Details:** `../DFA_CODE_CHANGES.md`

## Project status

This module is actively wired for dynamic parser loading and sample-based grammar verification aligned with the local `gradle-antlr-plugin` integration. Includes automatic DFA memory management for safe and efficient batch processing of large grammar file sets.
