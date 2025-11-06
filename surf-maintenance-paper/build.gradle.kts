plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    withCloudClientPaper()
    mainClass("dev.slne.surf.maintenance.paper.PaperMain")
    bootstrapper("dev.slne.surf.maintenance.paper.PaperBootstrap")

    authors.add("red")
}

dependencies {
    api(project(":surf-maintenance-core:surf-maintenance-core-client"))
}