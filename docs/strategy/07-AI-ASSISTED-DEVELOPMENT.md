# 07 — AI-Assisted Development

---

## AI Software Design Agent Approach

The AI assistant acts as a **continuous improvement agent** throughout the development lifecycle. It doesn't just write code — it reviews, suggests, tests, documents, and identifies risks proactively.

### Agent Roles

```
+--------------------------------------------------------------+
|                   AI AGENT CAPABILITIES                       |
|                                                               |
|  +------------------+  +------------------+  +-----------+   |
|  |  Architect Agent  |  |  Coding Agent    |  | QA Agent  |   |
|  |  - System design  |  |  - Feature impl  |  | - Tests   |   |
|  |  - Tech decisions |  |  - Bug fixes     |  | - Review  |   |
|  |  - API design     |  |  - Refactoring   |  | - Lint    |   |
|  +------------------+  +------------------+  +-----------+   |
|                                                               |
|  +------------------+  +------------------+  +-----------+   |
|  |  DevOps Agent     |  |  Docs Agent      |  | Security  |   |
|  |  - CI/CD setup    |  |  - Code docs     |  | Agent     |   |
|  |  - Docker config  |  |  - API docs      |  | - Vulns   |   |
|  |  - Deployment     |  |  - README        |  | - Audit   |   |
|  +------------------+  +------------------+  +-----------+   |
+--------------------------------------------------------------+
```

---

## AI Coding Workflow

### Daily Development Loop

```
1. PLAN    -> Describe what you want to build (natural language)
2. DESIGN  -> AI proposes architecture / approach
3. REVIEW  -> You approve or adjust the approach
4. CODE    -> AI generates implementation
5. TEST    -> AI generates tests, you verify
6. REVIEW  -> AI reviews its own code for issues
7. COMMIT  -> Commit with descriptive message
8. REPEAT  -> Next feature / fix
```

### Prompt Patterns for Each Phase

#### Pattern 1: Feature Implementation

```
Context: I'm building an Android event discovery app using Kotlin +
Jetpack Compose. The architecture follows Clean Architecture with
MVVM. I use Hilt for DI, Ktor Client for networking, and Room for
local storage.

Task: Implement the [FEATURE_NAME] feature.

Requirements:
- [Specific requirement 1]
- [Specific requirement 2]

Constraints:
- Follow existing code patterns in the project
- Write unit tests for business logic
- Use Kotlin coroutines/Flow for async operations

Existing relevant files:
- [file paths or code snippets]

Please provide:
1. Implementation plan
2. Code for each file
3. Unit tests
4. Any necessary DI module updates
```

#### Pattern 2: Bug Fix

```
Context: [Same project context as above]

Bug: [Description of the bug]

Expected behavior: [What should happen]
Actual behavior: [What happens instead]

Relevant code:
[Paste relevant code or file paths]

Error log:
[Paste error output if available]

Please:
1. Identify the root cause
2. Propose a fix
3. Explain why the fix works
4. Add a regression test
```

#### Pattern 3: Code Review

```
Please review this code for:
1. Correctness — does it do what it should?
2. Performance — any N+1 queries, unnecessary allocations?
3. Security — input validation, injection risks?
4. Clean Architecture — proper layer separation?
5. Kotlin idioms — using language features effectively?
6. Error handling — edge cases covered?

[Paste code]
```

#### Pattern 4: API Design

```
Context: FastAPI backend for event discovery app with PostGIS.

Design an API endpoint for: [DESCRIPTION]

Requirements:
- [Requirement 1]
- [Requirement 2]

Consider:
- Request/response schemas (Pydantic)
- Validation rules
- Error cases
- Rate limiting needs
- Caching strategy
- SQL query with PostGIS if spatial
```

#### Pattern 5: Database Query

```
Database: PostgreSQL 16 with PostGIS extension
ORM: SQLAlchemy 2.0 with GeoAlchemy2

Write a query to: [DESCRIPTION]

Table schemas:
[Paste relevant CREATE TABLE statements]

Requirements:
- Use SQLAlchemy ORM syntax (not raw SQL)
- Include spatial operations where needed
- Consider index usage
- Handle pagination
```

#### Pattern 6: Compose UI

```
Design system: Material 3 with custom theme
Navigation: Compose Navigation with bottom nav

Build a Compose screen for: [SCREEN_NAME]

Requirements:
- [UI requirements]
- [Behavior requirements]

State management:
- ViewModel with StateFlow
- Handle loading, success, error states

Include:
1. Composable function(s)
2. UI state data class
3. ViewModel
4. Preview function
```

---

## Code Review Automation

### Pre-Commit AI Review Checklist

Before committing, ask the AI to verify:

```
Review this changeset for:

[ ] No hardcoded secrets or API keys
[ ] No SQL injection vulnerabilities
[ ] Input validation on all user inputs
[ ] Error handling for network calls
[ ] Null safety (no !! in Kotlin)
[ ] Resource cleanup (coroutine scopes, streams)
[ ] Thread safety for shared state
[ ] Accessibility labels on UI elements
[ ] No unused imports or dead code
[ ] Consistent naming conventions
```

### Automated Linting (Non-AI)

```yaml
# .pre-commit-config.yaml
repos:
  - repo: https://github.com/astral-sh/ruff-pre-commit
    rev: v0.8.0
    hooks:
      - id: ruff
        args: [--fix]
      - id: ruff-format

  - repo: local
    hooks:
      - id: ktlint
        name: ktlint
        entry: ./android/gradlew -p android ktlintCheck
        language: system
        pass_filenames: false
        files: \.kt$
```

---

## Test Generation

### Unit Test Prompt

```
Generate unit tests for the following class/function:

[Paste code]

Test framework: JUnit 5 (Kotlin) / pytest (Python)
Mocking: MockK (Kotlin) / unittest.mock (Python)
Assertions: Use descriptive names following "should_expectedBehavior_when_condition" pattern

Cover:
1. Happy path
2. Edge cases (empty input, null, max values)
3. Error cases (network failure, invalid data)
4. Boundary conditions
```

### Integration Test Prompt

```
Generate integration tests for this API endpoint:

Endpoint: [METHOD /path]
Handler code: [paste]
Database queries: [paste]

Test with:
- httpx.AsyncClient (FastAPI TestClient)
- Test database with PostGIS
- Fixtures for test data
- Both success and error scenarios
```

### Android UI Test Prompt

```
Generate Compose UI tests for this screen:

Screen composable: [paste]
ViewModel: [paste]

Test:
1. Initial state renders correctly
2. Loading state shows progress indicator
3. Error state shows error message and retry button
4. Success state shows expected content
5. User interactions trigger correct callbacks
```

---

## Documentation Generation

### Inline Documentation

```
Add KDoc/docstring comments to this code. Focus on:
- "Why" not "what" for non-obvious logic
- Parameter constraints and valid ranges
- Return value semantics
- Exception conditions
- Thread safety notes if applicable

Do NOT add comments to self-explanatory code.

[Paste code]
```

### API Documentation

FastAPI auto-generates OpenAPI/Swagger docs. Enhance with:

```
Add detailed descriptions to these FastAPI endpoint definitions:
- Summary (short, for endpoint list)
- Description (detailed, for endpoint page)
- Parameter descriptions with examples
- Response descriptions for each status code
- Example request/response bodies

[Paste endpoint code]
```

### Architecture Decision Records (ADRs)

```
Write an ADR for this decision:

Title: [Decision title]
Context: [Why this decision was needed]
Decision: [What was decided]
Alternatives: [What else was considered]
Consequences: [Trade-offs and implications]

Format: Use Michael Nygard's ADR template.
```

---

## AI-Assisted Debugging

### Systematic Debug Prompt

```
I'm encountering this issue:

Symptom: [What's happening]
Expected: [What should happen]
Environment: [Android emulator / physical device / backend]
Steps to reproduce: [1, 2, 3...]

Logs/Error:
[Paste error output]

Relevant code:
[Paste code]

Recent changes:
[What changed before the bug appeared]

Help me:
1. Identify possible root causes (ranked by likelihood)
2. Suggest diagnostic steps for each
3. Provide the fix once identified
```

### Performance Debug Prompt

```
This operation is slow:

Operation: [Description]
Current time: [Xms]
Target time: [Yms]

Code:
[Paste code]

Database query (if applicable):
[EXPLAIN ANALYZE output]

Help me:
1. Profile the bottleneck
2. Suggest optimizations
3. Estimate improvement for each
```

---

## Prompt Engineering Best Practices

### Do

- **Be specific** about the project context (language, framework, architecture)
- **Provide relevant code** that the new code needs to integrate with
- **State constraints** clearly (no external dependencies, must be backward-compatible)
- **Ask for explanations** to learn, not just code
- **Iterate** — refine the output through follow-up questions
- **Request tests** alongside implementation

### Don't

- Don't ask for entire project scaffolds in one prompt (break it down)
- Don't skip reviewing generated code (AI can produce plausible but wrong code)
- Don't ignore warnings or caveats in AI responses
- Don't paste massive code blocks without highlighting the relevant parts
- Don't assume generated code handles all edge cases

### Context Management

When working on a large feature across multiple sessions:

```
Session start prompt:
"I'm continuing work on EventBuzz. Here's where I left off:

Project: Android event discovery app (Kotlin/Compose + FastAPI)
Current feature: [Feature name]
Completed: [What's done]
Next step: [What to do now]
Relevant files: [List key files]

Architecture decisions so far: [Key decisions]"
```

---

## AI Tool Integration

### Claude Code (Primary)

```bash
# Use Claude Code for:
# - Feature implementation with full context awareness
# - Multi-file changes with project understanding
# - Database migrations
# - Docker/deployment configuration
# - Code review of entire changesets

# Example workflow:
# 1. Open project in terminal
# 2. Describe the task
# 3. Claude Code reads relevant files, implements, and tests
```

### GitHub Copilot (Optional, Paid)

Best for:
- Line-by-line code completion in IDE
- Boilerplate generation
- Test case generation from function signatures

### Open-Source Alternatives

| Tool | Purpose | License |
|------|---------|---------|
| **Continue.dev** | IDE AI assistant (works with local models) | Apache 2.0 |
| **Tabby** | Self-hosted code completion | Apache 2.0 |
| **Aider** | CLI pair programming tool | Apache 2.0 |
| **Ollama** | Local LLM runtime | MIT |

---

## Continuous Improvement Agent Behavior

The AI should proactively:

1. **Flag potential issues** before they become bugs
2. **Suggest refactoring** when code duplication exceeds 3 occurrences
3. **Recommend tests** for untested critical paths
4. **Identify security risks** in code changes
5. **Propose performance improvements** when patterns are suboptimal
6. **Update documentation** when behavior changes
7. **Track technical debt** and suggest when to address it

### Weekly AI Review Prompt

```
Review the EventBuzz codebase for:

1. Technical debt:
   - TODOs and FIXMEs
   - Deprecated API usage
   - Code duplication
   - Overly complex functions (>50 lines)

2. Security:
   - Dependency vulnerabilities (check latest CVEs)
   - Hardcoded values that should be configs
   - Missing input validation

3. Performance:
   - N+1 query patterns
   - Missing database indexes for common queries
   - Unnecessary re-compositions in Compose

4. Architecture:
   - Layer violations (data layer importing presentation)
   - Missing abstractions
   - Tightly coupled components

Provide a prioritized list of improvements.
```

---

*Next: [08 — Roadmap & Learning Plan](./08-ROADMAP-LEARNING-PLAN.md)*
