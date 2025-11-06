plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

surfVelocityApi {
    withCloudClientVelocity()
}

dependencies {
    api(project(":surf-cloud-plugin-template-core:surf-cloud-plugin-template-core-client"))
    api(project(":surf-cloud-plugin-template-api:surf-cloud-plugin-template-client-api:surf-cloud-plugin-template-client-velocity-api"))
}