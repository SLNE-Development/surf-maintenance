plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

velocityPluginFile {
    main = "dev.slne.surf.maintenance.velocity.VelocityMain"
    authors = listOf("red")
}

dependencies {
    implementation("dev.slne.surf:surf-redis:1.0.0-SNAPSHOT")
}