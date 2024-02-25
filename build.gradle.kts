import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.8.22"
}

group = "br.pucpr"
version = "0.0.3-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("com.h2database:h2")

	val jjwtVersion = "0.11.5"
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
	implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")

	val kotestVersion = "5.6.2"
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.7")
	//testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion ")
	//runtimeOnly("io.kotest:kotest-assertions-core:$kotestVersion ")

	//MySQL Connector
	// https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
	implementation("com.mysql:mysql-connector-j:8.3.0")

	//val awsVersion = "1.12.586"
	val awsVersion = "1.12.655"
	implementation("com.amazonaws:aws-java-sdk-bom:$awsVersion")
	implementation("com.amazonaws:aws-java-sdk-s3:$awsVersion")
	implementation("com.amazonaws:aws-java-sdk-sns:$awsVersion")
	implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")

	// https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload
	//implementation("commons-fileupload:commons-fileupload:1.5")
	// https://mvnrepository.com/artifact/org.springframework/spring-web
	//implementation("org.springframework:spring-web:4.3.11.RELEASE")




}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

allOpen {
	annotation("jakarta.persistence.Entity")
}