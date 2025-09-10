@echo off
(
echo plugins {
echo     id 'java'
echo     id 'org.springframework.boot' version '3.5.5'
echo     id 'io.spring.dependency-management' version '1.1.7'
echo     id 'org.openapi.generator' version '7.15.0'
echo }
echo.
echo group = 'org.rauka.dm'
echo version = '0.0.1-SNAPSHOT'
echo description = 'msa-movement-wflux'
echo.
echo java {
echo     toolchain {
echo         languageVersion = JavaLanguageVersion.of^(21^)
echo     }
echo }
echo.
echo def packageName = 'org.rauka.dm.msamovementwflux'
echo import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
echo.
echo tasks.register^('buildSpringServer', GenerateTask^) {
echo     generatorName.set^("spring"^)
echo     inputSpec.set^("$rootDir/src/main/resources/openapi.yaml"^)
echo     outputDir.set^("$buildDir/generated"^)
echo     apiPackage.set^("${packageName}.controller"^)
echo     modelPackage.set^("${packageName}.service.models"^)
echo     library.set^("spring-boot"^)
echo     configOptions.set^([
echo             useSpringBoot3: "true",
echo             useJakartaEe: "true",
echo             serializableModel: "true",
echo             dateLibrary: "java8",
echo             openApiNullable: "false",
echo             apiFirst: "false",
echo             delegatePattern: "true",
echo             sourceFolder: "src/main/java",
echo             interfaceOnly: "true",
echo             configPackage: "${packageName}.config",
echo             basePackage: "${packageName}",
echo             disallowAdditionalPropertiesIfNotPresent: "true",
echo             reactive: "true",
echo             useTags: "true",
echo             singleContentTypes: "true"
echo     ]^)
echo }
echo.
echo sourceSets {
echo     main {
echo         java {
echo             srcDirs += ["$buildDir/generated/src/main/java"]
echo         }
echo     }
echo }
echo.
echo tasks.named^('compileJava'^) {
echo     dependsOn tasks.named^('buildSpringServer'^)
echo }
echo.
echo configurations {
echo     compileOnly {
echo         extendsFrom annotationProcessor
echo     }
echo }
echo.
echo repositories {
echo     mavenCentral^(^)
echo }
echo.
echo dependencies {
echo     implementation 'org.springframework.boot:spring-boot-starter-actuator'
echo     implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
echo     implementation 'org.springframework.boot:spring-boot-starter-webflux'
echo     implementation 'org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0'
echo     implementation 'org.postgresql:r2dbc-postgresql:1.0.5.RELEASE'
echo     implementation 'io.r2dbc:r2dbc-pool:1.0.1.RELEASE'
echo     implementation 'org.mapstruct:mapstruct:1.5.5.Final'
echo.
echo     compileOnly 'org.projectlombok:lombok'
echo     developmentOnly 'org.springframework.boot:spring-boot-devtools'
echo     annotationProcessor 'org.projectlombok:lombok'
echo     annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'
echo     testImplementation 'org.springframework.boot:spring-boot-starter-test'
echo     testImplementation 'io.projectreactor:reactor-test'
echo     testImplementation 'io.r2dbc:r2dbc-h2'
echo     testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
echo }
echo.
echo tasks.named^('test'^) {
echo     useJUnitPlatform^(^)
echo }
) > build.gradle

