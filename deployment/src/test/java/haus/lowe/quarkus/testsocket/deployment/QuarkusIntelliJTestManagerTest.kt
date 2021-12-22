package haus.lowe.quarkus.testsocket.deployment

import io.quarkus.test.QuarkusUnitTest
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class QuarkusIntelliJTestManagerTest {
    @Test
    fun testResponse() {
//        RestAssured.when().get("/q/dev/haus.lowe.quarkus-testsocket/tests/intellij").then().statusCode(200);
    }

    companion object {
        @RegisterExtension
        val unitTest = QuarkusUnitTest() // Start unit test with your extension loaded
            .setArchiveProducer { ShrinkWrap.create(JavaArchive::class.java) }
    }
}