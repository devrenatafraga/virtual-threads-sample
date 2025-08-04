plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "5.1.0.4882"
	jacoco
}

group = "edu.renata.fraga"
version = "0.0.1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
		html.required = true
	}
}

sonar {
	properties {
		property("sonar.projectKey", "devrenatafraga_virtual-threads-sample")
		property("sonar.organization", "devrenatafraga")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.projectName", "Virtual Threads Sample")
		property("sonar.projectVersion", "1.0")
		
		// Source and test directories
		property("sonar.sources", "src/main/java")
		property("sonar.tests", "src/test/java")
		property("sonar.java.source", "21")
		property("sonar.sourceEncoding", "UTF-8")
		
		// Binary directories (important for proper analysis)
		property("sonar.java.binaries", "build/classes/java/main")
		property("sonar.java.test.binaries", "build/classes/java/test")
		// Libraries will be resolved automatically by Gradle
		
		// Coverage report paths
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
		property("sonar.junit.reportPaths", "build/test-results/test")
		
		// Exclusions
		property("sonar.exclusions", "**/build/**,**/target/**")
		property("sonar.test.exclusions", "**/*Test.java,**/*Tests.java")
		
		// JaCoCo plugin configuration
		property("sonar.java.coveragePlugin", "jacoco")
	}
}

// Task dependencies to ensure proper execution order
tasks.named("sonar") {
	dependsOn("test", "jacocoTestReport")
}

tasks.named("jacocoTestReport") {
	dependsOn("test")
}
