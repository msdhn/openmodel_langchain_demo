# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot + LangChain4j banking customer support assistant that runs fully local using Ollama. The assistant handles card declines, disputes, branch FAQs, and credit card queries within strict safety guardrails. Policy documents (markdown) are ingested into a Qdrant vector store for RAG retrieval.

## Build & Run Commands

```bash
# Build
./mvnw compile

# Run the application (requires Ollama and Qdrant running locally)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=BankAssistantDemoApplicationTests
```

## Prerequisites

- **Java 21**
- **Ollama** running at `localhost:11434` with models: `llama3.1:8b` (chat), `nomic-embed-text:latest` (embeddings)
- **Qdrant** running at `localhost:6334` (gRPC) / `localhost:6333` (HTTP dashboard)

## Architecture

Request flow: `ChatController` → `AssistantService` → `BankingAssistant` (LangChain4j AI Service) → Ollama

- **`BankingAssistant`** — LangChain4j `@AiService` interface wired to `ollamaChatModel` with tool support. Uses `@SystemMessage`/`@UserMessage` template variables for prompt injection.
- **`BankingSupportTools`** — LangChain4j `@Tool`-annotated methods providing static guidance for card declines, disputes, card blocking, branch FAQs, and credit card outstanding queries. The `creditCardOutstandingDetails` tool uses `ReturnBehavior.IMMEDIATE` (skips LLM re-processing).
- **Safety pipeline** — `SafetyService` coordinates `InjectionDetector`, `ContentFilter`, and `PolicyRuleEngine` for input classification. Currently not wired into the main chat flow.
- **Memory** — `MemoryService` manages `SessionMemoryStore` (per-session message history) and `UserProfileMemoryStore` (tone preferences). `MemorySummarizer` compresses session history. Currently not wired into the main chat flow.
- **RAG ingestion** — `IngestionController` (`POST /api/ingestion/initiate`) triggers async policy markdown ingestion. `PolicyMarkdownIngestionJob` reads `.md` files from a configurable folder, chunks them via `SlidingWindowTextChunker`, embeds with Ollama, and stores in Qdrant via `QdrantPolicyVectorStore`.

## Key Configuration

All in `src/main/resources/application.properties`. App runs under context path `/banking-assistant`.

- `assistant.*` — custom config bound via `AssistantProperties`
- `langchain4j.ollama.*` — LangChain4j Spring Boot auto-configuration for Ollama
- `assistant.rag.*` — RAG chunk size/overlap, embedding dimension (1536), Qdrant connection details
- `assistant.rag.policy-folder` — absolute path to policy markdown docs (currently hardcoded to local machine)

## API Endpoints

- `POST /banking-assistant/api/chat?message=...` — main chat endpoint (query param, not JSON body)
- `POST /banking-assistant/api/ingestion/initiate` — start async policy ingestion
- `GET /banking-assistant/api/ingestion/status/{jobId}` — check ingestion job status