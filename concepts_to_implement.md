to build the # Concepts To Implement

Ordered roadmap to evolve this demo into a full application.

> Legend: ✅ Done · 🔶 Partial · ⬜ Not started

## Foundation

- ✅ Config structure and profiles — `AssistantProperties` + `ExecutorConfig`
- 🔶 Unified error model — `GlobalExceptionHandler` handles `AssistantException`; no validation errors or error codes
- 🔶 Logging baseline — SLF4j + `@Slf4j` in place; no structured/contextual logging
- ⬜ API contracts and validation — no `@Valid` or bean validation annotations
- ⬜ Health/readiness endpoints — Actuator dependency exists but not configured

## Core Assistant and Tooling

- ✅ Stable chat endpoint behavior — `POST /api/chat?message=...`
- ⬜ Deterministic routing for critical intents
- ✅ `@Tool` usage for assistive intents — 5 tools in `BankingSupportTools`
- ✅ Clear tool result handling — `AssistantService.extractAnswer()` handles content + `ToolExecution`
- ⬜ RAG as a tool (LLM queries Qdrant on demand for policy documents)
- Read-only data tools
  - 🔶 Credit card outstanding details — implemented; others below not started
  - ⬜ Account balance lookup
  - ⬜ Transaction history search
  - ⬜ Credit card statement details
  - ⬜ Loan status check
  - ⬜ Branch/ATM locator
- Action tools (require confirmation/authorization)
  - ⬜ Block/unblock card
  - ⬜ Raise a dispute ticket
  - ⬜ Schedule a callback
  - ⬜ Request a statement
  - ⬜ Update contact details
- Multi-step agents
  - ⬜ Fraud investigation (check transactions → flag → block card → raise ticket)
  - ⬜ Loan eligibility (pull history → check score → calculate → present options)
  - ⬜ Dispute resolution (look up transaction → check policy → create case → notify)

## Conversation Management

- ⬜ Multi-turn conversation handling (context window management, summarize vs drop strategy)
- ⬜ Conversation state machine (greeting → identify intent → resolve → confirm → close)
- ⬜ Human agent escalation (live handoff with full context transfer)
- ⬜ Conversation history accessible to the user (retrieve past sessions)
- ⬜ Multi-language support / i18n

## Customer Identity and Context

- ⬜ Customer authentication within chat (verify identity before showing account data)
- ⬜ Customer 360 context loading (pull profile, products, recent interactions before responding)
- ⬜ Channel awareness (different behavior on web, mobile app, WhatsApp, IVR)

## Prompt System

- ✅ Externalized prompt templates — `PromptService.systemPrompt()` with template variables
- ⬜ Prompt versioning
- ⬜ Per-intent prompt variants
- ⬜ Prompt test fixtures

## Guardrails

- 🔶 Input safety checks — `ContentFilter` + `InjectionDetector` implemented but not wired into chat flow
- ⬜ Output safety checks — no output filtering
- ✅ Prompt injection defenses — `InjectionDetector` detects common patterns
- 🔶 Policy engine with refusal and escalation flows — `PolicyRuleEngine.classify()` returns BLOCK/SENSITIVE/SAFE; no refusal enforcement
- ⬜ Hallucination detection (verify LLM claims against actual account/policy data)

## Memory

- ✅ Session memory — `SessionMemoryStore` with `ConcurrentHashMap`, max-messages limit
- ✅ User profile memory — `UserProfileMemoryStore` stores support tone per session
- ✅ Memory summarization — `MemorySummarizer` compresses session history
- ⬜ TTL and PII-safe memory persistence — in-memory only, no expiration or redaction

> Note: Memory service is implemented but not wired into the main chat flow.

## RAG Backend

- Backend
  - ✅ Document ingestion pipeline — `LocalFileIngestionService` + `TikaDocumentParser` (14 formats)
  - ✅ Chunking strategy — 4 strategies: recursive, sliding-window, sentence, paragraph (configurable via property)
  - ✅ Embedding model selection — Ollama `nomic-embed-text` via LangChain4j auto-config
  - ✅ Vector store integration — `QdrantPolicyVectorStore` with auto-collection creation
  - ⬜ Duplicate detection / dedup on re-ingestion
  - 🔶 Metadata enrichment — stores source, chunkIndex, fileName; no title/heading/date extraction
  - ⬜ Incremental ingestion (skip unchanged files by hash or modified timestamp)
  - ⬜ Embedding dimension alignment (config says 1536, `nomic-embed-text` produces 768)
- Retrieval
  - ⬜ Semantic search endpoint (query Qdrant, return top-K chunks)
  - ⬜ Context injection into LLM prompt
  - ⬜ Relevance score threshold (discard low-similarity chunks)
  - ⬜ Reranking (cross-encoder or LLM-based)
  - ⬜ Citations and source attribution
- Chunking improvements
  - ✅ Sentence-aware or section-aware splitting — `SentenceTextChunker` + `RecursiveTextChunker`
  - ⬜ Per-document-type chunk strategies (tables, slides vs prose)

## Integration Layer

- ⬜ Core banking system integration (mock account/transaction APIs with realistic data)
- ⬜ Event-driven architecture (listen to transaction events, trigger proactive alerts)
- ⬜ API gateway / rate limiting per customer
- ⬜ Webhook support for async operations (dispute filed → notify when resolved)

## User Experience

- ⬜ Streaming responses (SSE / WebFlux for token-by-token output)
- ⬜ Rich message formats (cards, buttons, carousels for product options)
- ⬜ Suggested replies / quick actions
- ⬜ Feedback collection (thumbs up/down on responses)
- ⬜ Typing indicators

## Evaluation

- ⬜ Golden dataset for expected behavior
- ⬜ Safety regression tests
- ⬜ Tool-call accuracy checks
- ⬜ Retrieval quality tests
- ⬜ Conversation-level testing (full multi-turn scenario tests)
- ⬜ A/B testing framework for prompts and model versions
- ⬜ Shadow mode (run new model alongside old, compare outputs before switching)

## Observability

- 🔶 Structured logs — SLF4j present, minimal instrumentation
- ⬜ Tracing and correlation IDs
- ⬜ Token and latency metrics
- ⬜ Stage-wise performance visibility

## Operational Intelligence

- ⬜ Intent analytics (what are customers asking about most)
- ⬜ Escalation analytics (why/when does the bot fail)
- ⬜ Customer satisfaction tracking (CSAT tied to conversations)
- ⬜ Cost tracking (tokens per conversation, cost per resolution)

## Auth and Security

- ⬜ OAuth2/OIDC integration
- ⬜ RBAC
- ⬜ Per-user/session data isolation
- ⬜ Secrets management and rate limits

## Reliability

- 🔶 Timeouts and retries — Ollama timeout configured (`PT60S`); no retry logic
- ⬜ Circuit breakers
- ⬜ Fallback model/tool behavior
- ⬜ Idempotency for critical operations

## Disaster Recovery and Business Continuity

- ⬜ Graceful degradation when LLM is down (scripted fallback responses)
- ⬜ Multi-region deployment strategy
- ⬜ Data backup and recovery for conversation history
- ⬜ SLA definitions and monitoring

## Compliance and Privacy

- ⬜ PII redaction
- ⬜ Retention and deletion policy
- ⬜ Encryption at rest and in transit
- ⬜ Audit trails
- ⬜ Consent management (opt-in/out of AI interactions)
- ⬜ Regulatory disclosures (auto-append disclaimers for financial guidance)
- ⬜ Record keeping (retain conversations for regulatory audit — typically 5-7 years)

## Model Governance

- ⬜ Approval process for prompt/model changes in production
- ⬜ Model version registry and rollback capability
- ⬜ Prompt change impact analysis (test against golden dataset before deploy)
- ⬜ Bias and fairness testing

## Productionization

- ⬜ CI/CD pipeline
- ⬜ Containerization
- ⬜ Environment promotion strategy
- ⬜ Runbooks and operational checklist
- ⬜ Load and resilience testing
