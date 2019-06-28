/**
 * A simple HTTP file server.
 * Implementation based on MG4J implementation:
 * https://raw.githubusercontent.com/bantudb/mg4j/master/src/it/unimi/di/big/mg4j/query/HttpFileServer.java
 * accessed on 25 jun 2019
 */
package org.cpqd.iotagent;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.bio.SocketConnector;


public class SimpleFileServerHttp extends Thread {
    /** The server itself. */
    private Server mServer;
    /** The server's port. */
    private int mServerPort;
    /** The server's port. */
    private String mDataPath;
    
    public SimpleFileServerHttp(int port, String path) {
        this.mServerPort = port;
        this.mDataPath = path;
        this.mServer = null;
    }

    @Override
    public void run() {
        try {
            // Create the server
            this.mServer = new Server();

            // Create a port listener
            SocketConnector connector = new SocketConnector();
            connector.setPort(this.mServerPort);
            this.mServer.addConnector(connector);

            // Create a context
            ContextHandler context = new ContextHandler();
            context.setContextPath("/");
            context.setResourceBase(this.mDataPath);

            // Add a resource handler
            ResourceHandler resourceHandler = new ResourceHandler();
            context.addHandler(resourceHandler);
            
            this.mServer.addHandler(context);

            // Start the http server
            mServer.start();
        } catch ( Exception e ) {
            throw new RuntimeException();
        }
    }
}
