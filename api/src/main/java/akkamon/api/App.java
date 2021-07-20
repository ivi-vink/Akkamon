package akkamon.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;


public class App {

    public static MessagingEngine messagingEngine;

    public static void main(String[] args) {
        Server server = startServer(8080);
        ServletContextHandler context = createStatefulContext(server);

        messagingEngine = new MessagingEngine();



        // websocket behaviour
        // Configure specific websocket behavior
        JettyWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) ->
        {
            // Configure default max size
            wsContainer.setMaxTextMessageSize(65535);

            // Add websockets
            wsContainer.addMapping("/", EventSocket.class);
        });

        // registerServlets(context);

        try {
            server.start();
            System.out.println("Started server.");
            System.out.println("Listening on http://localhost:8080/");
            System.out.println("Press CTRL+C to exit.");
            server.join();
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
        }

    }

    private static ServletContextHandler createStatefulContext(Server server) {
        ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        return context;
    }

    private static Server startServer(int port) {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);
        return server;
    }

    private static void registerServlets(ServletContextHandler context) {
        // Use the Jersey framework to translate the classes in the
        // mancala.api package to server endpoints (servlets).
        // For example, the StartMancala class will become an endpoint at
        // http://localost:8080/mancala/api/start
        ServletHolder serverHolder = context.addServlet(JettyWebSocketServlet.class, "/");
        serverHolder.setInitOrder(1);
        serverHolder.setInitParameter("jersey.config.server.provider.packages",
                "akkamon.api");
    }

}
