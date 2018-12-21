package com.vaultid.server;

import com.vaultid.main.RouteManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class BuiltInHttpServer {
    
    static final public int DEFAULT_PORT = 9090;

    private static int port;
    private static HttpServer server;
    private static boolean started = false;

    /**
     * Is started
     * @return boolean
     */
    public boolean isStarted(){
        return started;
    }
    
    /**
     * Set port
     * @param port
     */
    public void setPort(int port){
        this.port = port;
    }

    /**
     * Get port
     * @return int
     */
    public int getPort(){
        return port;
    }

    /**
     * Start
     * @param port
     */
    public void Start(int port) {
        try {
            this.port = port;
            server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println("HTTP server started at " + port);
            RouteManager rManager = new RouteManager();
            for(int i = 0; i < rManager.routes.length ; i++){
                server.createContext("/" + rManager.routes[i],rManager.handlers[i]);
            }
            server.setExecutor(null);
            server.start();
            started = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop
     */
    public void Stop() {
        server.stop(0);
        started = false;
        System.out.println("Server stopped");
    }
}
