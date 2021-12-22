package haus.lowe.quarkus.testsocket.deployment;

import haus.lowe.quarkus.testsocket.runtime.TestWebSocketRecorder;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.*;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.dev.testing.TestListenerBuildItem;
import io.quarkus.deployment.dev.testing.TestSupport;
import io.quarkus.dev.spi.DevModeType;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;

import java.util.Optional;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

public class TestSocketProcessor {

    @BuildStep
    TestListenerBuildItem sharedStateListener(LaunchModeBuildItem launchModeBuildItem) {
        Optional<TestSupport> ts = TestSupport.instance();
        if (testsDisabled(launchModeBuildItem, ts)) {
            return null;
        }
        /*
         * Adds a new test listener to the TestSupport.
         * This needs to run in deployment, and can't be passed to a recorder.
         */
        return new TestListenerBuildItem(new TestManager(ts.get()));
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(RUNTIME_INIT)
    RouteBuildItem websocketRoute(
            NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            TestWebSocketRecorder recorder
    ) {
        /*
         * Websocket doesn't work in the deployment-side router set up by DevConsoleProcessor.
         * Whenever a dev websocket is set up in Quarkus internal code, it is done this way,
         * which sets up a route on the runtime side.
         *
         * This means that we have to share events and commands via a shared class,
         * which is loaded via the system classloader. This is achieved by `parentFirstArtifacts`
         * in the Maven plugin config
         */
        return nonApplicationRootPathBuildItem.routeBuilder()
                .route("dev/haus.lowe.testsocket")
                .handler(recorder.websocketHandler())
                .displayOnNotFoundPage()
                .build();
    }

    private boolean testsDisabled(LaunchModeBuildItem launchModeBuildItem, Optional<TestSupport> ts) {
        return ts.isEmpty() || launchModeBuildItem.getDevModeType().orElse(null) != DevModeType.LOCAL;
    }
}
