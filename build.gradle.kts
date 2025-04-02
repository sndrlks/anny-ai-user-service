plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"

	id("org.owasp.dependencycheck") version "12.0.2"
}

group = "com.annyai"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	val openapiVersion = "2.8.5"
	val jjwtVersion = "4.5.0"
	val hibernateValidatorVersion = "8.0.2.Final"
	val logstashVersion = "8.0"

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("jakarta.validation:jakarta.validation-api")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${openapiVersion}")
	implementation("com.auth0:java-jwt:${jjwtVersion}")
	implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
	implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")

	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyCheck {
	format = "HTML"
	outputDirectory = "/reports/dependency-check"
}

tasks.withType<Test> {
	useJUnitPlatform()
}
