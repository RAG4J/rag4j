# rag4j BOM (Bill of Materials)

This module provides a Bill of Materials (BOM) for rag4j, making it easy for samples and external projects to use rag4j dependencies with consistent versions.

## Usage

To use the rag4j BOM in your Maven project, add it to your `dependencyManagement` section:

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

After importing the BOM, you can declare rag4j dependencies without specifying versions:

```xml
<dependencies>
    <dependency>
        <groupId>org.rag4j</groupId>
        <artifactId>rag4j-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.rag4j</groupId>
        <artifactId>rag4j-integration-openai</artifactId>
    </dependency>
    <dependency>
        <groupId>org.rag4j</groupId>
        <artifactId>rag4j-splitters</artifactId>
    </dependency>
</dependencies>
```

## Available Modules

The BOM includes all rag4j modules:

### Core Modules
- `rag4j-core` - Core functionality
- `rag4j-utils` - Utility classes

### Integration Modules
- `rag4j-integration-ollama` - Ollama integration
- `rag4j-integration-openai` - OpenAI integration
- `rag4j-integration-weaviate` - Weaviate vector store integration

### Feature Modules
- `rag4j-generator-question` - Question generation
- `rag4j-knowledge-extraction` - Knowledge extraction
- `rag4j-local-embedding` - Local embedding models
- `rag4j-local-store` - Local vector store
- `rag4j-quality` - Quality assessment
- `rag4j-splitters` - Text splitters

### Third-Party Dependencies

The BOM also manages versions for common third-party dependencies used by rag4j:
- OpenAI Java Client
- SLF4J
- Lombok
- Jackson
- Apache Commons CSV
- JUnit Jupiter
- Mockito

## Benefits

Using the BOM provides several advantages:
1. **Version Consistency** - All rag4j modules will use compatible versions
2. **Simplified Dependency Management** - No need to specify versions for each dependency
3. **Easier Updates** - Update all rag4j dependencies by changing a single version
4. **Reduced Conflicts** - Ensures third-party dependencies are compatible
