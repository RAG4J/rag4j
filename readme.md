# RAG4J - Retrieval Augmented Generation for Java
Welcome to the repository for our project [RAG4J.org](https://rag4j.org). This project is a Java implementation of the
Retrieval Augmented Generation framework. It is a framework that is simple to use and understand. But powerful 
enough to extend for your own projects. The framework is designed to use as the base for a project, ideal to learn
about the different components of a RAG system. You should be able to read all the code in an hour and change the code
to learn about the different aspects of a RAG system. This project is perfect to use in a workshop to learn about RAG.

If you prefer to do this in Python, you can use the [RAG4P](https://github.com/RAG4J/rag4p) project.

## Structure of the project

The project is divided into a number of packages. Each package contains a different aspect of the RAG system.

### applications
This package contains a number of applications that use the different components of the RAG system. The applications
are used to demonstrate the different components of the RAG system. The applications are not meant to be used in a
production environment. They are meant to be used to learn about the different components of the RAG system.
- `complete` - Application that uses all the components of the RAG system, there is one that uses the local store, and one that uses Weaviate.
- `generation` - Applications that show how to generate answers and questions. There is a special Vasa question generator. This generator loops over all the available
  `chunks` in a content store and generates questions for each chunk. The questions are stored in a file. This file is
  used to verify the quality of the retriever. This is also called a `synthetic judgement list`.
- `indexing` - Applications that show how to index content in the local store and in Weaviate.
- `integration` - Application that shows how to connect with Weaviate and OpenAI.
- `retrieval` - Applications that show how to retrieve chunks from the local store and from Weaviate.

### indexing
This package contains the components used to index content into the content store. It provides the `IndexingService`, which is used to index content using a `ContentReader` or a single InputDocument. Both methods require a splitter to split the content into chunks. Multiple splitters are available out of the box. 
- `MaxTokenSplitter` - Splits the content into chunks based on the maximum number of tokens.
- `SentenceSplitter` - Splits the content into chunks based on the sentences in the content.
- `SingleChunkSplitter` - Does not split the content.

### integrations
This package contains the components used to integrate with external services. It uses the `KeyLoader` to load the API keys for OpenAI and Weaviate. At the moment Weaviate is used as the external VectorStore and OpenAI is used as the external Large Language Model. 

### rag
This package contains the main components of the RAG system. It contains the `store`, the `embedding`, the `retreival`, the `generation`, the `model` and the `tracker` packages. In case of local components, the implementations are also in this package. For the remote integrations, the implementations are in the `integrations` package.

### util
This package contains a number of utility classes. The most important class is the `KeyLoader`. This class is used to load the API keys for OpenAI and Weaviate. It can load the keys from environment variables, from a file on the classpath, or from a file on the classpath with a secret key for access to a remote file.

## Setting up your environment

### Java
Use one of the installers like:
- [jenv](https://www.jenv.be)
- [sdkman](https://sdkman.io)
- [jbang](https://www.jbang.dev)

Oh and we use maven, so install that if you do not have access to it yet.

[Apache Maven](https://maven.apache.org)

Use an IDE of your choice, we use IntelliJ IDEA, but something like Eclipse or VS Code will work as well.

## Test the installation
To test your installation, run the following command:
```shell
mvn clean package
```

## Loading API keys
We try to limit accessing Large Language Models and vector stores to a minimum. This is enough to learn about all the
elements of the Retrieval Augmented Generation framework, except for the generation part. For some of the applications 
you require access to OpenAI. You need a paid account to access the OpenAI apis. There are a number of ways to set make
the keys available to the project. The same mechanism is used for the (Weaviate)[https://weaviate.io] vector store. 

Properties take precedence in the following order:
1. Environment variables
2. A file on the classpath
3. A file on the classpath with a secret key for access to a remote file

### Environment variables
The easiest way to load the API key is to set an environment variable for each required key. The names of the
environment variables are:
- `openai_api_key`
- `weaviate_api_key`
- `weaviate_url`

### A file on the classpath
Another way of doing this is through a file on your classpath in the resources folder. This file is
called `env.properties`. It contains the following lines:
```properties
openai_api_key=sk-...
weaviate_api_key=...
weaviate_url=...
```

### A file on the classpath with a secret key for access to a remote file
The final method is to provide an environment variable or a line in the mentioned file containing the following line:
```properties
secret_key=...
```
This secret key is used to decrypt the remote file containing the API keys. We use this mechanism during our workshops.
That way attendees do not have to create their own API keys. If you are in our workshop, use this key for the workshop 
only, and limit the amount of interaction, or we get blocked for exceeding our limits. The API key is obtained through 
a remote file, which is encrypted. Of course you can also use your own key if you have it.

Use at the class `KeyLoader` to learn more about loading the properties.