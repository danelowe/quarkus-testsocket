package haus.lowe.quarkus.testsocket.spi;

import haus.lowe.quarkus.testsocket.spi.command.Command;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Test Director Wiring
 * 
 * The websocket needs to run in runtime. The test manager needs to run in deployment.
 * The two need to talk to one another. This class provides that.
 * 
 * It needs to be in a module that is listed as `parentFirstArtifact` in the Quarkus Maven plugin config,
 * So that it is loaded by the system classloader. See https://quarkus.io/guides/class-loading-reference
 */
public class TestSocketWiring {
    private static final CopyOnWriteArraySet<Consumer<String>> eventListeners = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArraySet<Consumer<String>> commandListeners = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArrayList<String> events = new CopyOnWriteArrayList<>();

    private static volatile Command testCommand = null;

    public static void addEventListener(Consumer<String> listener) {
        eventListeners.add(listener);
        events.forEach(listener);
    }
    public static void addCommandListener(Consumer<String> listener) {
        commandListeners.add(listener);
    }

    public static void clearEvents() {
        events.clear();
    }

    public static void sendEvent(String msg) {
        events.add(msg);
        for (Consumer<String> listener : eventListeners) {
            listener.accept(msg);
        }
    }
    public static void sendCommand(String msg) {
        for (Consumer<String> listener : commandListeners) {
            listener.accept(msg);
        }
    }

    public static void setTestCommand(Command command) {
        testCommand = command;
    }

    public static Command getTestCommand() {
        return testCommand;
    }
}
