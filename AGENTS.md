# AGENTS.md

Wmn4j is a java library for western music notation.

## Repository structure

```
├── build-resources: files for builds, e.g., checkstyle rules.
├── doc: high-level markdown documentation or the project.
├── src/test: unit tests.
└── src/main/java/org/wmn4j
    ├── analysis: contains algorithms for score-based music analysis.
    ├── io: contains input and output operations.
    ├── mir: contains algorithms for music information retrieval (MIR).
    ├── notation: classes and interfaces for representing the basic elements of western music notation.
    ├── representation: contains different representation of symbolic music data for use in algorithms.
    └── utils: collection of utilities mainly intended for use within wmn4j.
```

## Build

- Full build to verify code `./gradlew build`.

## Code style

- Classes should preferably be immutable.
- Builder classes used to construct immutable classes.
- Mutable classes need to clearly document thread-safety.
- Classes should be `final` by default unless designed for inheritance.
- Always include unit tests with code changes.
- Adhere to style guidelines in `CODING_CONVENTIONS.md`.
- Packages should have `package-info.java` files that describe the purpose of the package.

## Commits

- Submit PRs to `development` branch (not `main`).
- Commits have brief title lines that start with a verb.
- Commit bodies should be concise and explain why the changes are needed.
