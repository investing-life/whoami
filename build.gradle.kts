plugins {
	java
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.0"
	id("application")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.1")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-devtools:3.0.6")
	implementation("ch.qos.logback:logback-classic:1.4.7")
	implementation("org.slf4j:slf4j-api:2.0.7")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.1")
	implementation("org.apache.commons:commons-collections4:4.4")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-web") // 웹 의존성 추가
	implementation("org.springframework.boot:spring-boot-starter-security") // 보안 의존성 추가
	implementation("org.apache.commons:commons-text:1.10.0")
	implementation("org.hibernate.orm:hibernate-hikaricp:6.2.6.Final")
	testImplementation("org.projectlombok:lombok:1.18.26")
	testImplementation("org.projectlombok:lombok:1.18.26")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
	mainClass.set("com.example.whoami.WhoamiApplication")
}

tasks.withType<Test> {
	useJUnitPlatform()
}