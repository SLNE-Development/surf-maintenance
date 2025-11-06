plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withCloudClientCommon()
}

dependencies {
    api(project(":surf-cloud-plugin-template-api:surf-cloud-plugin-template-client-api:surf-cloud-plugin-template-client-common-api"))
    api(project(":surf-cloud-plugin-template-core:surf-cloud-plugin-template-core-common"))
}