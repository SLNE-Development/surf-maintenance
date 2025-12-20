plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

velocityPluginFile {
    main = "dev.slne.surf.maintenance.velocity.VelocityMain"
    authors = listOf("red")
}