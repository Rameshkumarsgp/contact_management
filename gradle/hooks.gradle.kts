tasks.register("installGitHooks") {
    description = "Configures git to use .githooks/ so hooks are shared via the repo"
    group = "setup"
    doLast {
        providers.exec { commandLine("git", "config", "core.hooksPath", ".githooks") }.result.get()
        providers.exec { commandLine("chmod", "+x", ".githooks/pre-commit") }.result.get()
        println("✅ Git hooks installed — .githooks/pre-commit is active")
    }
}

tasks.named("build") {
    dependsOn("installGitHooks")
}
