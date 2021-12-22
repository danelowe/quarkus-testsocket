package haus.lowe.quarkus.testsocket.runtime;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class TestWebSocketRecorder {
    public TestWebSocketHandler websocketHandler() {
        return new TestWebSocketHandler();
    }
}
