package fr.bahi27.app;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "fr.bahi27", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

  @ArchTest
  static final ArchRule domainSansFramework =
      noClasses()
          .that()
          .resideInAPackage("fr.bahi27.domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(
              "org.springframework..", "jakarta.persistence..", "com.fasterxml.jackson..")
          .because("le domaine doit rester en Java pur, sans framework");

  @ArchTest
  static final ArchRule webSansPersistence =
      noClasses()
          .that()
          .resideInAPackage("fr.bahi27.web..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("fr.bahi27.persistence..")
          .because("le web parle a l'application, jamais a la base");

  @ArchTest
  static final ArchRule applicationSansInfrastructureNiWeb =
      noClasses()
          .that()
          .resideInAPackage("fr.bahi27.application..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("fr.bahi27.persistence..", "fr.bahi27.web..")
          .because("l'application orchestre le domaine, elle ignore les adaptateurs")
          .allowEmptyShould(true); // la couche application est encore vide en phase 0

  @ArchTest
  static final ArchRule controllersSansRepository =
      noClasses()
          .that()
          .resideInAPackage("fr.bahi27.web..")
          .should()
          .dependOnClassesThat()
          .haveSimpleNameEndingWith("Repository")
          .because("un controller ne doit pas acceder directement a un Repository");

  @ArchTest
  static final ArchRule domainesIsolesEntreEux =
      slices()
          .matching("fr.bahi27.domain.(*)..")
          .should()
          .notDependOnEachOther()
          .because("un domaine passe par les services, jamais par les classes d'un autre domaine");
}
