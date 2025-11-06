plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

surfVelocityApi {
    withCloudClientVelocity()
}

velocityPluginFile {
    main = "dev.slne.surf.maintenance.velocity.VelocityMain"
    authors = listOf("red")
}

dependencies {
    api(project(":surf-maintenance-core:surf-maintenance-core-client"))
}