package com.vaultid.main;

import com.sun.net.httpserver.HttpHandler;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Luan Lino Matias dos Santos <luan.matias@soluti.com.br>
 */
public class RouteManager {
    
    static final String  DEFAULT_NOTFOUND = "route-not-found";
    
    static final String  DEFAULT                  = "";
    static final String  STOP                     = "stop";
    static final String  RESTART                  = "restart";
        
    public String[] routes = new String[]{
        DEFAULT,
        STOP,
        RESTART,
    };
    
    public static List<String> PUBLIC_ROUTES = Arrays.asList(
            "prepare-file-to-signature",
            "prepare-file-form-fields",
            "prepare-file-fields-attachments"
    );

    
    public HttpHandler[] handlers = new HttpHandler[]{
        new Handlers.RootHandler(),//DEFAULT
        new Handlers.StopHandler(),//STOP
        new Handlers.RestartHandler()
    };
}
