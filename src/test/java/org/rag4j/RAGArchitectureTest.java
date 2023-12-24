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
            .should().onlyBeAccessed().byAnyPackage("org.rag4j", "org.rag4j.weaviate..");
    @ArchTest
    public static final ArchRule utilPackageRule = classes()
            .that().resideInAPackage("org.rag4j.util..")
            .should().onlyDependOnClassesThat().resideInAnyPackage("org.rag4j.util..", "java..", "org.slf4j..", "javax.crypto..", "lombok..", "org.mockito..", "org.junit..");

    @ArchTest
    public static final ArchRule indexingPackageRule = classes()
            .that().resideInAPackage("org.rag4j.indexing..")
            .should().onlyDependOnClassesThat().resideInAnyPackage("org.rag4j.indexing..", "org.rag4j.util..", "org.rag4j.domain..", "org.rag4j.resources..", "opennlp..","java..", "org.slf4j..", "lombok..", "org.mockito..", "org.junit..");

    @ArchTest
    public static final ArchRule layerRules = layeredArchitecture().consideringOnlyDependenciesInLayers()
            .layer("Main").definedBy("org.rag4j")
            .layer("Util").definedBy("org.rag4j.util..")
            .layer("Domain").definedBy("org.rag4j.domain..")
            .layer("Resources").definedBy("org.rag4j.resources..")
            .layer("Indexing").definedBy("org.rag4j.indexing..")
            .layer("Retrieval").definedBy("org.rag4j.retrieval..")
            .layer("Weaviate").definedBy("org.rag4j.weaviate..")
            .layer("OpenAI").definedBy("org.rag4j.openai..")
            .layer("Tracker").definedBy("org.rag4j.tracker..")
            .layer("Generation").definedBy("org.rag4j.generation..")
            .whereLayer("Util").mayOnlyBeAccessedByLayers("Main","Indexing", "Retrieval", "Weaviate", "Tracker", "Generation", "OpenAI")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Main","Indexing", "Retrieval", "Weaviate", "Tracker", "Generation")
            .whereLayer("Resources").mayOnlyBeAccessedByLayers("Main","Indexing")
            .whereLayer("Indexing").mayOnlyBeAccessedByLayers("Main", "Weaviate", "OpenAI")
            .whereLayer("Retrieval").mayOnlyBeAccessedByLayers("Main","Weaviate")
            .whereLayer("Weaviate").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("OpenAI").mayOnlyBeAccessedByLayers("Main", "Generation", "Indexing", "Retrieval")
            .whereLayer("Tracker").mayOnlyBeAccessedByLayers("Main", "Generation", "Retrieval");
}
