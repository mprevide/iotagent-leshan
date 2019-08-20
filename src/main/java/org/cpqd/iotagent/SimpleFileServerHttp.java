/**
 * A simple HTTP file server.
 * Implementation based on jetty documentation:
 * https://www.eclipse.org/jetty/documentation/current/embedded-examples.html#embedded-file-server
 * accessed on 20 ago 2019
 */
package org.cpqd.iotagent;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;


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
            this.mServer = new Server(this.mServerPort);

            ResourceHandler resourceHandler = new ResourceHandler();

            resourceHandler.setDirectoriesListed(true);

            resourceHandler.setResourceBase(this.mDataPath);

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
            this.mServer.setHandler(handlers);

            // Start the http server
            this.mServer.start();
            this.mServer.join();
        } catch ( Exception e ) {
            throw new RuntimeException();
        }
    }
}
