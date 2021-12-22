package haus.lowe.quarkus.testsocket.runtime;

import haus.lowe.quarkus.testsocket.spi.TestSocketWiring;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TestWebSocketHandler implements Handler<RoutingContext>, Consumer<String> {
    private static final Logger log = Logger.getLogger(TestWebSocketHandler.class);
    private static final Set<ServerWebSocket> sockets = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void handle(RoutingContext context) {
        if ("websocket".equalsIgnoreCase(context.request().getHeader(HttpHeaderNames.UPGRADE))) {
            TestSocketWiring.addEventListener(this);
            context.request().toWebSocket(event -> {
                if (event.succeeded()) {
                    ServerWebSocket socket = event.result();
                    sockets.add(socket);
                    socket.closeHandler(e -> sockets.remove(socket));
                    socket.frameHandler(frame -> {
                        TestSocketWiring.sendCommand(frame.textData());
                    });
                } else {
                    log.error("Failed to connect to test server", event.cause());
                }
            });
        } else {
            context.next();
        }
    }

    @Override
    public void accept(String s) {
        for (ServerWebSocket i : sockets) {
            i.writeTextMessage(s);
        }
    }
}
