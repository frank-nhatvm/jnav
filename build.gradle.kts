import com.vanniktech.maven.publish.SonatypeHost

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("com.android.library") version "8.2.0" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.21" apply false
    id("com.vanniktech.maven.publish") version "0.27.0"
}

mavenPublishing {
    coordinates("com.fatherofapps", "jnav", "1.0.2")
    publishToMavenCentral("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
    signAllPublications()
    pom {
        name.set("JNav")
        description.set("A library to generate route for Jetpack Compose Navigation by using KSP")
        inceptionYear.set("2024")
        url.set("https://github.com/frank-nhatvm/jnav")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("frank_vu")
                name.set("Frank Vu")
                email.set("nhat.thtb@gmail.com")
                url.set("https://fatherofapps.com/")
            }
        }
        scm {
            url.set("https://github.com/frank-nhatvm/jnav")
            connection.set("scm:git@github.com:frank-nhatvm/jnav.git")
            developerConnection.set("scm:git@github.com:frank-nhatvm/jnav.git")
        }
    }
}
