# Repository Guidelines

## Project Structure & Module Organization

This is a small Java project configured as an IntelliJ IDEA module.

- `src/` contains application source code. The current entry point is `src/Main.java`.
- `out/` is generated compiler output and must not be committed.
- `.idea/` and `primer_estudio_caso.iml` define the IDE project/module configuration.
- There is no dedicated `test/` directory yet. Add tests under `test/` or `src/test/` once a test framework is introduced.

Keep production code in `src/`. Avoid placing generated files, IDE output, or local machine files in source directories.

## Build, Test, and Development Commands

This repository does not currently use Maven or Gradle. Use the JDK directly:

```bash
javac --enable-preview --source 26 -d out src/Main.java
java --enable-preview -cp out Main
```

- `javac ...` compiles the current Java source into `out/`.
- `java ...` runs the compiled entry point.

The project uses Java 26 preview features, so keep `--enable-preview --source 26` in compile commands and `--enable-preview` in run commands.

## Coding Style & Naming Conventions

- Use 4 spaces for indentation.
- Use Java naming conventions: `PascalCase` for classes, `camelCase` for methods and variables, and `UPPER_SNAKE_CASE` for constants.
- Keep methods short and focused. If behavior grows, extract named methods instead of expanding `main`.
- Prefer clear names over comments that explain obvious code.

## Testing Guidelines

No automated test framework is configured yet. When adding tests, prefer JUnit 5 and place tests in a clear test source root such as `test/` or `src/test/java/`.

Name test classes after the unit under test, for example `MainTest`. Tests should describe observable behavior, not implementation details.

## Commit & Pull Request Guidelines

This repository has no existing commit history, so use conventional commits from the start:

```text
feat: add console greeting
fix: correct loop bounds
chore: update project ignore rules
```

Pull requests should include a short summary, verification steps, and any relevant screenshots or console output. Link related issues when available. Do not commit generated `out/` files or local IDE/runtime artifacts.

## Agent-Specific Instructions

Default generated technical artifacts to English. Do not add AI attribution or `Co-Authored-By` lines to commits.
