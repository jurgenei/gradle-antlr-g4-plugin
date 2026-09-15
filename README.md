# antlr-grammars-g4 (obsolete: functionality has moved into antlr-grammars)

![Conformance](https://img.shields.io/badge/Conformance-Check--All%20Passing-brightgreen)

[![Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/name.jurgenei.gradle.antlr.g4?label=Plugin%20Portal)](https://plugins.gradle.org/plugin/name.jurgenei.gradle.antlr.g4)
[![Build and Test](https://github.com/jurgenei/gradle-antlr-g4-plugin/actions/workflows/ci.yml/badge.svg?branch=release%2F0.1.3)](https://github.com/jurgenei/gradle-antlr-g4-plugin/actions/workflows/ci.yml?query=branch%3Arelease%2F0.1.3)
[![Coverage CI](https://github.com/jurgenei/gradle-antlr-g4-plugin/actions/workflows/coverage.yml/badge.svg?branch=release%2F0.1.3)](https://github.com/jurgenei/gradle-antlr-g4-plugin/actions/workflows/coverage.yml?query=branch%3Arelease%2F0.1.3)
[![Coverage](https://codecov.io/gh/jurgenei/gradle-antlr-g4-plugin/graph/badge.svg?branch=release%2F0.1.3)](https://app.codecov.io/gh/jurgenei/gradle-antlr-g4-plugin?branch=release%2F0.1.3)
[![Test on Push](https://github.com/jurgenei/gradle-antlr-g4-plugin/actions/workflows/test-on-push.yml/badge.svg?branch=release%2F0.1.3)](https://github.com/jurgenei/gradle-antlr-g4-plugin/actions/workflows/test-on-push.yml?query=branch%3Arelease%2F0.1.3)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-21+-green.svg)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/gradle-8+-blue.svg)](https://gradle.org/)
![ANTLR](https://img.shields.io/badge/ANTLR-4.13.x-blue)

ANTLR v4 self-grammar module extracted from `gradle-antlr-xml-plugin`.

This repository packages ANTLR v4 grammar (`ANTLRv4Lexer.g4` / `ANTLRv4Parser.g4`) and validates against real `.g4` samples in test resources.

## What this repo contains

- grammar sources: `src/main/antlr/name/jurgenei/parsers`
- lexer support class: `src/main/java/name/jurgenei/parsers/LexerAdaptor.java`
- sample inputs: `src/test/resources/antlr4`
- dynamic-loading parser test: `src/test/java/name/jurgenei/parsers/G4LexerParserTest.java`
- grammar-to-class task: `name.jurgenei.gradle.xml.G4toClassTask`

## G4 -> GrammarModel -> AST Classes

`G4toClassTask` converts `.g4` grammar files into two outputs per source file:

- `<name>.model.sexp` (normalized GrammarModel)
- `<name>.classes.sexp` (derived AST classes model)

Derivation semantics implemented:

- **1B**: parse-tree driven extraction (fallback text parser when parser classes unavailable)
- **2A**: top-level alternatives always create inheritance `(isa Child Parent)`
- **3A**: literal-only parser rules dropped from AST class output

Task implementation uses `name.jurgenei.ast:ast-classes-core` dependency.

## Input/Output Modes (`G4toClassTask`)

### 1) File-tree mode (`fileset`) with output directory

```groovy
tasks.named('g4ToClass', name.jurgenei.gradle.xml.G4toClassTask) {
    fileset('src/main/antlr') {
        include '**/*.g4'
        exclude '**/legacy/**'
    }
    outputDir.set(layout.buildDirectory.dir('g4-classes'))
}
```

### 2) Explicit single-file mode (`input` + `output`)

```groovy
tasks.register('deriveOne', name.jurgenei.gradle.xml.G4toClassTask) {
    input 'src/main/antlr/Mini.g4'
    output 'build/out/Mini.classes.sexp'
    modelOutput 'build/out/Mini.model.sexp'
}
```

Notes:

- In explicit mode, `input` and `output` must be set together.
- If `modelOutput` omitted, task auto-derives it from `output` using `.model.sexp` extension.
- In file-tree mode, `outputDir` is required.

## Quick start

```bash
./gradlew clean test
./gradlew g4ToClass
```

## Important tasks

- `generateLexerSources` - generates lexer sources from `ANTLRv4Lexer.g4`
- `generateParserSources` - generates parser sources from `ANTLRv4Parser.g4`
- `compileAntlrSources` - compiles generated sources + `LexerAdaptor`
- `test` - runs dynamic-loading parser tests over sample `.g4` files
- `xmlast` - converts sample `.g4` files to XML AST
- `g4ToClass` - converts sample `.g4` files to GrammarModel and AST classes outputs

## XML AST task

`xmlast` configured with:

- `parserClassName = name.jurgenei.parsers.ANTLRv4Parser`
- `lexerClassName = name.jurgenei.parsers.ANTLRv4Lexer`
- `startRule = grammarSpec`
- source directory: `src/test/resources/antlr4`
- output directory: `build/xmlast-samples`

Run manually:

```bash
./gradlew xmlast
```

## Notes

- `check` covers source presence verification (`verifyGrammarSources`), tests, coverage verification, jar layout verification, and XML AST validation.
- `g4ToClass` available as dedicated grammar-model derivation task.

## Project status

This module is actively wired for dynamic parser loading, sample-based grammar verification, and grammar-to-class derivation outputs aligned with local `gradle-antlr-plugin` integration.
