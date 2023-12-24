# RAG4J - Retrieval Augmented Generation for Java
Welcome to the repository for our project [RAG4J.org](https://rag4j.org). This project is a Java implementation of the
Retrieval Augmented Generation framework. It is a framework that is simple to use and understand. But powerful 
enough to extend for your own projects.

## Setting up your environment

## Java
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
elements of the Retrieval Augmented Generation framework, except for the generation part. In the workshop we use the
LLM of Open AI, which is not publicly available. We will provide you with a key to access it.

Please use this key for the workshop only, and limit the amount of interaction, or we get blocked for exceeding our
limits. The API key is obtained through a remote file, which is encrypted. Of course you can also use your own key if
you have it.

### Environment variables
The easiest way to load the API key is to set an environment variable for each required key. The names of the
environment variables are:
- `openai_api_key`
- `weaviate_api_key`
- `weaviate_url`

Another way of doing this is through the file on your classpath in the resources folder. This file is
called `env.properties`. It contains the following lines:
```properties
openai_api_key=sk-...
weaviate_api_key=...
weaviate_url=...
```

The final method is to provide an environment variable or a line in the mentioned file containing the following line:
```properties
secret_key=...
```
This secret key is used to decrypt the remote file containing the API keys. We will provide the value for this key
during the workshop.
