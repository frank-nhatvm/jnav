plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("com.vanniktech.maven.publish") version "0.34.0"
}
group = "com.fatherofapps.jnav"
version = "2.0.2"



java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}


dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:2.2.20-2.0.2")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
}

mavenPublishing {
    val sdkVersion = project.version.toString()
    coordinates(
        groupId = "com.fatherofapps.jnav",
        artifactId = "jnav",
        version = sdkVersion
    )
    pom {
        name = "JNav"
        description =
            "A library to generate route for Jetpack Compose Navigation by using KSP"
        url = "https://github.com/frank-nhatvm/jnav"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "frank_vu"
                name = "Frank Vu"
                email = "nhat.thtb@gmail.com"
            }
        }
        scm {
            connection = "scm:git@github.com:frank-nhatvm/jnav.git"
            developerConnection = "scm:git@github.com:frank-nhatvm/jnav.git"
            url = "https://github.com/frank-nhatvm/jnav"
        }
    }
}
