import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

tasks.named<Test>("test") {
    finalizedBy(tasks.named("jacocoTestReport"))
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
