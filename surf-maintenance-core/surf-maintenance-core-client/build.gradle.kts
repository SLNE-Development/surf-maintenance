plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withCloudClientCommon()
}

dependencies {
    api(project(":surf-maintenance-core"))
}