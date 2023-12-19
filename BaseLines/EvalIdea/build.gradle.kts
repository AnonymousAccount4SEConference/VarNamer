plugins {
    id("java") apply true
    id("org.jetbrains.intellij") version "1.10.0" apply true

}

group = "exp"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://plugins.jetbrains.com/maven") }
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { url = uri("https://maven.aliyun.com/repository/google")  }
    maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }

    mavenLocal()
    google()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2.3")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("java"/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.alibaba:fastjson:1.2.83")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.1.0.202203080745-r"){
        exclude("org.slf4j", "slf4j-api")
    }
}