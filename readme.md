# RAG4J - Retrieval Augmented Generation for Java

Welcome to the repository for our project [RAG4J.org](https://rag4j.org). This project is a Java implementation of the
Retrieval Augmented Generation framework. It is a framework that is simple to use and understand, but powerful 
enough to extend for your own projects. The framework is designed to be used as the base for a project, ideal to learn
about the different components of a RAG system. You should be able to read all the code in an hour and change the code
to learn about the different aspects of a RAG system. This project is perfect to use in a workshop to learn about RAG.

If you prefer to do this in Python, you can use the [RAG4P](https://github.com/RAG4J/rag4p) project.

## Architecture Overview

RAG4J follows a modular architecture where each module focuses on a specific aspect of the RAG system. This design allows you to:
- Use only the modules you need
- Easily swap implementations (e.g., local embeddings vs. OpenAI, in-memory store vs. Weaviate)
- Extend the framework with your own implementations
- Learn about RAG components in isolation

## Project Structure

The project is organized into multiple Maven modules:

### Core Modules

#### `core`
The foundation of RAG4J containing all core abstractions and domain models:
- **Indexing**: `IndexingService` for ingesting and processing content
- **Embedding**: `Embedder` interface for text vectorization
- **Retrieval**: `Retriever` interface and strategies (TopN, Window, Hierarchical, Document)
- **Generation**: `QuestionAnsweringService` and `QuestionGenerator` interfaces
- **Store**: `ContentStore` interface for vector storage
- **Model**: Core domain objects (`Chunk`, `RetrievalOutput`, etc.)
- **Tracker**: `RAGTracker` for observability and monitoring

#### `utils`
Utility classes used across the project:
- `KeyLoader`: Load API keys from environment variables, properties files, or encrypted remote files
- Helper classes for configuration and common operations

### Integration Modules

These modules provide implementations for external services:

#### `integration-openai`
OpenAI integration for embeddings and language models:
- `OpenAIEmbedder`: Generate embeddings using OpenAI's API
- `OpenAIQuestionAnsweringService`: Answer generation using GPT models
- `OpenAIQuestionGenerator`: Question generation for evaluation

#### `integration-ollama`
Ollama integration for local language models:
- `OllamaQuestionAnsweringService`: Answer generation using locally-run Ollama models
- `OllamaQuestionGenerator`: Local question generation
- Support for various open-source models (Llama, Mistral, etc.)

#### `integration-weaviate`
Weaviate vector database integration:
- `WeaviateContentStore`: Store and retrieve embeddings using Weaviate
- Schema management and configuration
- Support for Weaviate's vectorization capabilities

### Local Implementation Modules

#### `local-embedding`
Local embedding models using ONNX Runtime:
- `LocalEmbedder`: Generate embeddings locally without external API calls
- Suitable for development, testing, and privacy-sensitive applications

#### `local-store`
In-memory vector store implementation:
- `InternalContentStore`: Simple in-memory storage using cosine similarity
- Perfect for learning, testing, and small datasets
- Supports persistence via serialization

### Feature Modules

#### `splitters`
Text splitting strategies for chunking documents:
- `MaxTokenSplitter`: Split by maximum token count
- `SentenceSplitter`: Split by sentences using NLP
- `SingleChunkSplitter`: No splitting (whole document)
- `SemanticSplitter`: Split based on semantic boundaries

#### `generator-question`
Question generation utilities:
- Generate synthetic questions from content for evaluation
- Create test datasets (synthetic judgement lists)
- Vasa Museum question generator example

#### `knowledge-extraction`
Extract structured knowledge from unstructured text:
- Knowledge graph construction
- Entity and relationship extraction
- Metadata enrichment

#### `quality`
Quality assessment and evaluation:
- Retrieval quality metrics
- Answer quality evaluation
- Support for judgement lists

### Supporting Modules

#### `examples`
Runnable example applications demonstrating RAG4J capabilities:
- **Complete**: End-to-end RAG applications (local and Weaviate)
- **Indexing**: Index content into stores
- **Retrieval**: Retrieve and test different retrieval strategies
- **Generation**: Answer and question generation examples
- **Integration**: Test connections to OpenAI, Ollama, and Weaviate
- **Knowledge**: Knowledge extraction examples

#### `rag4j-bom`
Bill of Materials for dependency management:
- Simplifies version management for all RAG4J modules
- Ensures compatible versions across dependencies
- See `rag4j-bom/README.md` for usage instructions

## Getting Started

### Prerequisites

**Java 21** is required. You can install it using:
- [SDKMAN](https://sdkman.io): `sdk install java 21-tem`
- [jenv](https://www.jenv.be)
- [jbang](https://www.jbang.dev)

**Apache Maven** is used for building. Install from [maven.apache.org](https://maven.apache.org) or use your package manager.

**IDE**: Use IntelliJ IDEA, Eclipse, VS Code, or your preferred Java IDE.

### Building the Project

Clone the repository and build all modules:
```shell
git clone https://github.com/RAG4J/rag4j.git
cd rag4j-project
mvn clean install
```

To skip tests:
```shell
mvn clean install -DskipTests
```

### Running Examples

Examples are located in the `examples` module. Each example is a runnable Java application demonstrating specific RAG4J features.

#### Local-Only Example (No API Keys Required)
```shell
# Run a local RAG system using in-memory store and local embeddings
java -cp examples/target/rag4j-examples-2.0.0-SNAPSHOT.jar \
  org.rag4j.applications.complete.AppQualityLLMLocal
```

#### Using External Services
For examples using OpenAI, Ollama, or Weaviate, see the API Keys section below.

## Configuration and API Keys

### Using Local Components Only

RAG4J can run entirely locally without any external API keys using:
- `local-embedding`: Local embeddings via ONNX Runtime
- `local-store`: In-memory vector storage
- `integration-ollama`: Local LLMs via Ollama (requires [Ollama](https://ollama.ai) installed)

This is perfect for learning, development, and privacy-sensitive applications.

### Using External Services

For production use or to access more powerful models, you can use:
- **OpenAI**: State-of-the-art embeddings and language models
- **Weaviate**: Production-grade vector database

#### API Key Configuration

The `KeyLoader` utility supports multiple ways to provide API keys, in order of precedence:

**1. Environment Variables** (recommended for production):
```shell
export openai_api_key=sk-...
export weaviate_api_key=...
export weaviate_url=https://your-instance.weaviate.network
```

**2. Properties File** (good for development):

Create `env.properties` in your resources folder:
```properties
openai_api_key=sk-...
weaviate_api_key=...
weaviate_url=https://your-instance.weaviate.network
```

**3. Encrypted Remote Configuration** (for workshops):

Provide a secret key to access encrypted remote configuration:
```properties
secret_key=workshop-key
```

This mechanism is used during RAG4J workshops to share temporary API access without exposing keys.

### Ollama Setup

To use local LLMs with Ollama:

1. Install Ollama from [ollama.ai](https://ollama.ai)
2. Pull a model:
   ```shell
   ollama pull llama2
   ollama pull mistral
   ```
3. Run examples using Ollama (no API keys needed):
   ```shell
   java -cp examples/target/rag4j-examples-2.0.0-SNAPSHOT.jar \
     org.rag4j.applications.generation.AppOllamaAnswerGenerator
   ```

## Using RAG4J in Your Project

### With Maven

Add the RAG4J BOM to your `pom.xml`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.rag4j</groupId>
            <artifactId>rag4j-bom</artifactId>
            <version>2.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Then add only the modules you need:

```xml
<dependencies>
    <!-- Core module (required) -->
    <dependency>
        <groupId>org.rag4j</groupId>
        <artifactId>rag4j-core</artifactId>
    </dependency>
    
    <!-- Choose your embedding implementation -->
    <dependency>
        <groupId>org.rag4j</groupId>
        <artifactId>rag4j-local-embedding</artifactId>
    </dependency>
    
    <!-- Choose your vector store -->
    <dependency>
        <groupId>org.rag4j</groupId>
        <artifactId>rag4j-local-store</artifactId>
    </dependency>
    
    <!-- Choose your LLM integration -->
    <dependency>
        <groupId>org.rag4j</groupId>
        <artifactId>rag4j-integration-ollama</artifactId>
    </dependency>
    
    <!-- Add splitters if needed -->
    <dependency>
        <groupId>org.rag4j</groupId>
        <artifactId>rag4j-splitters</artifactId>
    </dependency>
</dependencies>
```

## Learning Path

RAG4J is designed for progressive learning:

1. **Start with the Core**: Read the interfaces in the `core` module to understand RAG abstractions
2. **Run Local Examples**: Use `local-embedding`, `local-store`, and `integration-ollama` to avoid API costs
3. **Explore Strategies**: Try different retrieval strategies (TopN, Window, Hierarchical, Document)
4. **Try Splitters**: Experiment with different text splitting approaches
5. **Evaluate Quality**: Use the `quality` module to assess retrieval and generation performance
6. **Scale Up**: Move to `integration-openai` and `integration-weaviate` for production use

## Contributing

Contributions are welcome! The modular architecture makes it easy to:
- Add new integrations (new LLM providers, vector databases)
- Implement new retrieval strategies
- Create new splitters
- Improve existing implementations

Please see the examples module for patterns and best practices.

## License

RAG4J is licensed under the Apache License 2.0. See the LICENSE file for details.

## Resources

- Website: [rag4j.org](https://rag4j.org)
- Python Version: [RAG4P](https://github.com/RAG4J/rag4p)
- Issues: [GitHub Issues](https://github.com/RAG4J/rag4j/issues)
