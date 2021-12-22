package haus.lowe.quarkus.testsocket.deployment;

import com.fasterxml.jackson.databind.ObjectMapper;
import haus.lowe.quarkus.testsocket.deployment.filter.WiringTestFilter;
import haus.lowe.quarkus.testsocket.spi.TestSocketWiring;
import haus.lowe.quarkus.testsocket.spi.command.Command;
import io.quarkus.deployment.dev.testing.*;
import io.quarkus.vertx.http.runtime.devmode.Json;
import org.jboss.logging.Logger;
import org.junit.platform.commons.util.ClassLoaderUtils;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.junit.platform.launcher.TestIdentifier;
import org.opentest4j.AssertionFailedError;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class TestManager implements TestListener, TestRunListener, Consumer<String> {
    private TestSupport ts;

    public TestManager(TestSupport ts) {
        this.ts = ts;
        TestSocketWiring.addCommandListener(this);
    }

    @Override
    public void testRunStarted(Consumer<TestRunListener> listenerConsumer) {
        listenerConsumer.accept(this);
    }

    @Override
    public void runStarted(long toRun) {
        TestSocketWiring.clearEvents();
        Json.JsonObjectBuilder e = Json.object();
        e.put("event", "runStarted");
        e.put("toRun", toRun);
        TestSocketWiring.sendEvent(e.build());
    }

    @Override
    public void testStarted(TestIdentifier testIdentifier, String className) {
        Json.JsonObjectBuilder e = Json.object();
        e.put("event", "testStarted");
        e.put("name", testIdentifier.getDisplayName());
        e.put("id", testIdentifier.getUniqueId());
        if (testIdentifier.getParentIdObject().isPresent()) {
            e.put("parentId", testIdentifier.getParentIdObject().get().toString());
        }
        if (testIdentifier.getSource().isPresent()) {
            TestSource src = testIdentifier.getSource().get();
            if (src instanceof MethodSource) {
                MethodSource methodSource = (MethodSource) src;
                e.put("className", methodSource.getClassName());
                e.put("methodName", methodSource.getMethodName());
            } else if (src instanceof ClassSource) {
                ClassSource classSource = (ClassSource) src;
                e.put("className", classSource.getClassName());
            }
        }
        TestSocketWiring.sendEvent(e.build());
    }


    @Override
    public void testComplete(TestResult result) {
        Json.JsonObjectBuilder e = Json.object();
        e.put("event", "testComplete");
        e.put("id", result.getUniqueId().toString());
        Json.JsonArrayBuilder log = Json.array();
        result.getLogOutput().stream().forEach(log::add);
        e.put("log", log);
        Json.JsonArrayBuilder problems = Json.array();
        result.getProblems().stream().map(this::buildException).forEach(problems::add);
        e.put("duration", result.getTime());
        e.put("status", result.getTestExecutionResult().getStatus().toString().toLowerCase());
        if (result.getTestExecutionResult().getThrowable().isPresent()) {
            Throwable ex = result.getTestExecutionResult().getThrowable().get();
            e.put("details", buildException(ex));
        }
        TestSocketWiring.sendEvent(e.build());
    }


    @Override
    public void runComplete(TestRunResults results) {
        Json.JsonObjectBuilder e = Json.object();
        e.put("event", "runComplete");
        e.put("duration", results.getCompletedTime());
        Json.JsonObjectBuilder counts = Json.object();
        counts.put("passed", results.getPassedCount());
        counts.put("failed", results.getFailedCount());
        counts.put("skipped", results.getSkippedCount());
        counts.put("total", results.getTotalCount());
        e.put("counts", counts);
        TestSocketWiring.sendEvent(e.build());
    }

    @Override
    public void runAborted() {
        Json.JsonObjectBuilder e = Json.object();
        e.put("event", "runAborted");
        TestSocketWiring.sendEvent(e.build());
    }

    @Override
    public void noTests(TestRunResults results) {
        Json.JsonObjectBuilder e = Json.object();
        e.put("duration", results.getCompletedTime());
        e.put("event", "noTests");
        TestSocketWiring.sendEvent(e.build());
    }

    @Override
    public void accept(String s) {
        TestSocketWiring.setTestCommand(Command.fromString(s));
        if (ts.isStarted()) {
            ts.getTestRunner().runTests();
        } else {
            ts.start();
        }
    }

    private Json.JsonObjectBuilder buildException(Throwable ex) {
        Json.JsonObjectBuilder exo = Json.object();
        exo.put("message", ex.getMessage());
        String type = "error";
        if ((ex instanceof AssertionFailedError)) {
            AssertionFailedError assertionError = (AssertionFailedError) ex;
            if (assertionError.isActualDefined() && assertionError.isExpectedDefined()) {
                type = "comparison";
                exo.put("expected", assertionError.getExpected().toString());
                exo.put("actual", assertionError.getActual().toString());
            }
        }
        exo.put("type", type);

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        ex.printStackTrace(writer);
        exo.put("stackTrace", sw.toString());
        return exo;
    }
}
