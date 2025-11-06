plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withCloudCommon()
}

dependencies {
    api(project(":surf-cloud-plugin-template-api:surf-cloud-plugin-template-common-api"))
}
