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
            .should().onlyBeAccessed().byAnyPackage("org.rag4j.applications..", "org.rag4j.integrations.weaviate..");

    @ArchTest
    public static final ArchRule openaiPackageRule = classes()
            .that().resideInAPackage("..openai..")
            .should().onlyBeAccessed().byAnyPackage("org.rag4j.applications..", "org.rag4j.integrations.openai..");

    @ArchTest
    public static final ArchRule indexingPackageRule = classes()
            .that().resideInAPackage("org.rag4j.indexing..")
            .should().onlyBeAccessed().byAnyPackage("org.rag4j.applications..", "org.rag4j.indexing..")
            .andShould().onlyDependOnClassesThat().resideInAnyPackage("org.rag4j.indexing..", "org.rag4j.util..", "org.rag4j.rag.model..", "org.rag4j.rag.store", "org.rag4j.rag.generation..", "opennlp..","java..", "org.slf4j..", "lombok..", "org.mockito..", "org.junit..", "com.knuddels.jtokkit..");

    @ArchTest
    public static final ArchRule utilPackageRule = classes()
            .that().resideInAPackage("org.rag4j.util..")
            .should().onlyDependOnClassesThat().resideInAnyPackage("org.rag4j.util..", "java..", "org.slf4j..", "javax.crypto..", "org.json..", "lombok..", "org.mockito..", "org.junit..");


    @ArchTest
    public static final ArchRule layerRules = layeredArchitecture().consideringOnlyDependenciesInLayers()
            .layer("Applications").definedBy("org.rag4j.applications..")
            .layer("Indexing").definedBy("org.rag4j.indexing..")
            .layer("Integrations").definedBy("org.rag4j.integrations..")
            .layer("Rag").definedBy("org.rag4j.rag..")
            .layer("Util").definedBy("org.rag4j.util..")
            .whereLayer("Applications").mayNotBeAccessedByAnyLayer()
            .whereLayer("Indexing").mayOnlyBeAccessedByLayers("Applications", "Integrations")
            .whereLayer("Integrations").mayOnlyBeAccessedByLayers("Applications")
            .whereLayer("Rag").mayOnlyBeAccessedByLayers("Applications", "Indexing", "Integrations")
            .whereLayer("Util").mayOnlyBeAccessedByLayers("Applications","Indexing", "Integrations", "Rag")
            .whereLayer("Util").mayNotAccessAnyLayer();

}
