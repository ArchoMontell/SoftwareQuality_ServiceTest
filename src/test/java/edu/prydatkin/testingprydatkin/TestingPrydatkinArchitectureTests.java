package edu.prydatkin.testingprydatkin;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.ArchTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.plantuml.rules.PlantUmlArchCondition.Configuration.consideringAllDependencies;


@SpringBootTest
class TestingPrydatkinArchitectureTests {
    private JavaClasses applicationClasses;

    @BeforeEach
    void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("edu.prydatkin.testingprydatkin");
    }

    @Test
    void shouldFollowLayerArchitecture()  {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                //
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                //
                .check(applicationClasses);
    }

    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("out of arch rules")
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..service..")
                .because("out of arch rules")
                .check(applicationClasses);
    }

    @Test
    void  controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .haveSimpleNameEndingWith("Controller")
                .check(applicationClasses);
    }

    @Test
    void controllerClassesShouldBeAnnotatedByControllerClass() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(RestController.class)
                .check(applicationClasses);
    }

    @Test
    void repositoryShouldBeInterface() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .beInterfaces()
                .check(applicationClasses);
    }

    @Test
    void anyControllerFieldsShouldNotBeAnnotatedAutowired() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(Autowired.class)
                .check(applicationClasses);
    }

    @Test
    void modelFieldsShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..model..")
                .should().notBePublic()
                .because("smth")
                .check(applicationClasses);

    }

    @Test
    void serviceClassesShouldBeNamedXService() {
        classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .check(applicationClasses);
    }

    @Test
    void serviceClassesShouldBeAnnotatedWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                .check(applicationClasses);
    }

    @Test
    void repositoryClassesShouldBeAnnotatedWithRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().beAnnotatedWith(org.springframework.stereotype.Repository.class)
                .check(applicationClasses);
    }

    @Test
    void noCyclicDependenciesBetweenPackages() {
        com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices()
                .matching("edu.prydatkin.testingprydatkin.(*)..")
                .should().beFreeOfCycles()
                .check(applicationClasses);
    }

    @Test
    void controllersShouldNotDependOnRepositories() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .because("Controllers should access repositories only through services")
                .check(applicationClasses);
    }

    @Test
    void servicesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    @Test
    void modelClassesShouldNotDependOnOtherLayers() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..controller..", "..service..", "..repository..")
                .check(applicationClasses);
    }

    @Test
    void serviceClassesShouldBeClassesNotInterfaces() {
        classes()
                .that().resideInAPackage("..service..")
                .should().beTopLevelClasses()
                .andShould().notBeInterfaces()
                .check(applicationClasses);
    }

    @Test
    void controllerClassesShouldBePublic() {
        classes()
                .that().resideInAPackage("..controller..")
                .should().bePublic()
                .check(applicationClasses);
    }

    @Test
    void serviceFieldsShouldNotBeAutowired() {
        noFields()
                .that().areDeclaredInClassesThat().resideInAPackage("..service..")
                .should().beAnnotatedWith(Autowired.class)
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..controller..")
                .check(applicationClasses);
    }

    @Test
    void modelClassesShouldBeConcreteClasses() {
        classes()
                .that().resideInAPackage("..model..")
                .and().areTopLevelClasses()
                .should().notBeInterfaces()
                .andShould().notBeEnums()
                .check(applicationClasses);
    }
}
