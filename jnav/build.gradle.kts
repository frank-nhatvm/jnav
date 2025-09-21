plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    signing
}
group = "com.fatherofapps.jnav"
version = "1.0.2"

java {
    withJavadocJar()
    withSourcesJar()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin{
    jvmToolchain(11)
}


dependencies {
    implementation ("com.google.devtools.ksp:symbol-processing-api:2.2.20-2.0.2")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
}


publishing{

    publications{
        create<MavenPublication>("jnav"){
            groupId = "com.fatherofapps"
            artifactId = "jnav"
            version = "1.0.2"
            from(components["java"])
            pom{
                name = "JNav"
                description = "A library to generate route for Jetpack Compose Navigation by using KSP"
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
    }
    repositories {
        maven {
            name = "centralStaging"
            url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("mavenCentralUsername") as String? ?: System.getenv("MAVEN_CENTRAL_USERNAME")
                password = findProperty("mavenCentralPassword") as String? ?: System.getenv("MAVEN_CENTRAL_PASSWORD")
            }
        }

        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = "frank_vu"
                password = "Frank932714@"
            }
        }
    }
}

signing {
    sign(publishing.publications["jnav"])
}

// publish library manually : ./gradlew publishJnavPublicationToMavenRepository