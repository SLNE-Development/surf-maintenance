plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

surfVelocityApi {
    withSurfRedis()
}

velocityPluginFile {
    main = "dev.slne.surf.maintenance.velocity.VelocityMain"
    authors = listOf("red")
}