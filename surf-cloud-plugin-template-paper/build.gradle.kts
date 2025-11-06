plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    withCloudClientPaper()
    mainClass("dev.slne.surf.template.paper.PaperMain")
    bootstrapper("dev.slne.surf.template.paper.PaperBootstrap")
}

dependencies {
    api(project(":surf-cloud-plugin-template-core:surf-cloud-plugin-template-core-client"))
    api(project(":surf-cloud-plugin-template-api:surf-cloud-plugin-template-client-api:surf-cloud-plugin-template-client-paper-api"))
}