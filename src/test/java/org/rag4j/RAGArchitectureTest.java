package org.rag4j;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "org.rag4j")
public class RAGArchitectureTest {

    @ArchTest
    public static final ArchRule weaviatePackageRule = classes()
            .that().resideInAPackage("..weaviate..")
            .should().onlyBeAccessed().byAnyPackage("org.rag4j.examples", "org.rag4j.weaviate..");
    @ArchTest
    public static final ArchRule layerRules = layeredArchitecture().consideringOnlyDependenciesInLayers()
            .layer("Chat").definedBy("org.rag4j.chat..")
            .layer("Domain").definedBy("org.rag4j.domain..")
            .layer("Generation").definedBy("org.rag4j.generation..")
            .layer("Indexing").definedBy("org.rag4j.indexing..")
            .layer("Localembedder").definedBy("org.rag4j.localembedder..")
            .layer("Main").definedBy("org.rag4j")
            .layer("OpenAI").definedBy("org.rag4j.openai..")
            .layer("Quality").definedBy("org.rag4j.quality..")
            .layer("Resources").definedBy("org.rag4j.resources..")
            .layer("Retrieval").definedBy("org.rag4j.retrieval..")
            .layer("Store").definedBy("org.rag4j.store..")
            .layer("Tracker").definedBy("org.rag4j.tracker..")
            .layer("Util").definedBy("org.rag4j.store..")
            .layer("Weaviate").definedBy("org.rag4j.weaviate..")
            .whereLayer("Chat").mayOnlyBeAccessedByLayers("Main", "OpenAI", "Quality")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Main","Indexing", "Retrieval", "Weaviate", "Tracker", "Generation", "Store", "Quality")
            .whereLayer("Generation").mayOnlyBeAccessedByLayers("Main", "OpenAI", "Quality")
            .whereLayer("Indexing").mayOnlyBeAccessedByLayers("Main", "Weaviate", "OpenAI", "Localembedder", "Store", "Quality")
            .whereLayer("Localembedder").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("OpenAI").mayOnlyBeAccessedByLayers("Main", "Generation", "Indexing", "Retrieval")
            .whereLayer("Quality").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("Resources").mayOnlyBeAccessedByLayers("Main","Indexing")
            .whereLayer("Retrieval").mayOnlyBeAccessedByLayers("Main","Weaviate", "Store", "Quality")
            .whereLayer("Store").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("Tracker").mayOnlyBeAccessedByLayers("Main", "Generation", "Retrieval", "Quality")
            .whereLayer("Util").mayOnlyBeAccessedByLayers("Main","Indexing", "Retrieval", "Weaviate", "Tracker", "Generation", "OpenAI")
            .whereLayer("Weaviate").mayOnlyBeAccessedByLayers("Main");

    @ArchTest
    public static final ArchRule utilPackageRule = classes()
            .that().resideInAPackage("org.rag4j.util..")
            .should().onlyDependOnClassesThat().resideInAnyPackage("org.rag4j.util..", "java..", "org.slf4j..", "javax.crypto..", "lombok..", "org.mockito..", "org.junit..");

    @ArchTest
    public static final ArchRule indexingPackageRule = classes()
            .that().resideInAPackage("org.rag4j.indexing..")
            .should().onlyDependOnClassesThat().resideInAnyPackage("org.rag4j.indexing..", "org.rag4j.util..", "org.rag4j.domain..", "org.rag4j.resources..", "opennlp..","java..", "org.slf4j..", "lombok..", "org.mockito..", "org.junit..", "com.knuddels.jtokkit..");

}
