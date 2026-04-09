import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

tasks.named<Test>("test") {
    finalizedBy(tasks.named("jacocoTestReport"))
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))
    reports {
        xml.required = true
    }
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    violationRules {
        rule {
            limit {
                minimum = "0.75".toBigDecimal()
            }
        }
    }
}

tasks.register("printTestResults") {
    dependsOn(tasks.named("test"))
    doLast {
        val resultsDir = file("build/test-results/test")
        resultsDir.walkTopDown()
            .filter { it.name.startsWith("TEST-") && it.extension == "xml" }
            .sortedBy { it.name }
            .forEach { file ->
                val xml = file.readText()
                Regex("""<testcase[^>]+name="([^"]+)"[^>]+classname="([^"]+)"[^/]*/?>""")
                    .findAll(xml)
                    .forEach { match ->
                        val name      = match.groupValues[1]
                        val classname = match.groupValues[2].substringAfterLast(".")
                        val block     = xml.substring(match.range.first).substringBefore("</testcase>")
                        val icon = when {
                            block.contains("<failure") || block.contains("<error") -> "❌"
                            block.contains("<skipped") -> "⚠️ "
                            else -> "✅"
                        }
                        println("  $icon $classname > $name")
                    }
            }
    }
}

tasks.register("printCoverage") {
    dependsOn(tasks.named("jacocoTestReport"))
    doLast {
        val report = file("build/reports/jacoco/test/jacocoTestReport.xml")
        val xml = report.readText()
        val match = Regex("""<counter type="INSTRUCTION" missed="(\d+)" covered="(\d+)"/>""")
            .findAll(xml).last()
        val missed  = match.groupValues[1].toLong()
        val covered = match.groupValues[2].toLong()
        val pct     = covered.toDouble() / (missed + covered) * 100
        println("📊 Coverage: ${"%.1f".format(pct)}%  (threshold: 75%)")
    }
}
