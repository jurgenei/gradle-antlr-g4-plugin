# Gradle ANTLR G4 Plugin

[![Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/name.jurgenei.gradle.antlr.g4?label=Plugin%20Portal)](https://plugins.gradle.org/plugin/name.jurgenei.gradle.antlr.g4)
![Java](https://img.shields.io/badge/Java-21%2B-007396?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8%2B-02303A?logo=gradle&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

`gradle-antlr-g4-plugin` provides a preconfigured XML AST task for ANTLRv4 grammar files (`*.g4`).

It builds on `name.jurgenei.gradle.antlr` and gives you a ready-to-use task type and plugin for grammar-centric workflows.

## Use Cases

- Validate large sets of `.g4` grammar files in CI
- Generate XML AST artifacts for grammar analysis pipelines
- Run parser checks with preconfigured ANTLRv4 defaults
- Keep parser invocation as a standard Gradle task

## Install

```groovy
plugins {
    id 'name.jurgenei.gradle.antlr.g4' version '0.1.1'
}
```

Plugin Portal page: https://plugins.gradle.org/plugin/name.jurgenei.gradle.antlr.g4

Legacy id remains available for compatibility:

```groovy
plugins {
    id 'name.jurgenei.grammars.g4' version '0.1.1'
}
```

## What the Plugin Adds

- `XmlAstG4GradleTask` task type
- `g4XmlAst` pre-registered task
- Auto-wiring to Java `classes` and runtime classpath when `java` plugin is present

Default task conventions:

- `grammar = antlr4`
- `parserClassName = name.jurgenei.parsers.ANTLRv4Parser`
- `lexerClassName = name.jurgenei.parsers.ANTLRv4Lexer`
- `startRule = grammarSpec`
- `includes = ['**/*.g4']`

## Quick Start

```groovy
plugins {
    id 'java'
    id 'name.jurgenei.gradle.antlr.g4' version '0.1.1'
}

tasks.named('g4XmlAst', name.jurgenei.grammars.g4.XmlAstG4GradleTask) {
    sourceDirectory.set(layout.projectDirectory.dir('src/test/resources/antlr4'))
    destinationDirectory.set(layout.buildDirectory.dir('xmlast-g4'))
    targetExtension.set('.xml')
    continueOnError.set(true)
}
```

Run:

```bash
./gradlew g4XmlAst
```

## Typical CI Flow

```bash
./gradlew clean check g4XmlAst
```

Useful helper tasks in this project:

- `generateLexerSources`
- `generateParserSources`
- `compileAntlrSources`
- `verifyGrammarSources`
- `test`

## Repository Rename

This project is renamed from:

- `https://github.com/jurgenei/antlr-grammars-g4`

to:

- `https://github.com/jurgenei/gradle-antlr-g4-plugin`

## Development

```bash
./gradlew clean test
./gradlew publishToMavenLocal
```

## Troubleshooting

- `ClassNotFoundException` for parser/lexer classes:
  - Run `compileAntlrSources`
  - Ensure `runtimeClasspath` includes generated classes
- No output files:
  - Confirm `sourceDirectory` and include patterns
  - Set `force = true` for a full pass
