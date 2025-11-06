plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

surfRawPaperApi {
    withCloudClientPaper()
}

dependencies {
    api(project(":surf-cloud-plugin-template-api:surf-cloud-plugin-template-client-api:surf-cloud-plugin-template-client-common-api"))
}