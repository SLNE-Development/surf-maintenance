plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withCloudServer()
}

dependencies {
    api(project(":surf-cloud-plugin-template-core:surf-cloud-plugin-template-core-common"))
    api(project(":surf-cloud-plugin-template-api:surf-cloud-plugin-template-server-api"))
}