package com.vaultid.main;

import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import com.vaultid.server.Helper;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.vaultid.server.ApiProblem;
import com.vaultid.server.AbstractHttpHandler;
import com.vaultid.server.BuiltInHttpServer;
import com.vaultid.server.UnsupportedMediaTypeException;
import com.vaultid.server.adapter.exception.InvalidSessionException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;

public class Handlers {

    public static final String CONTROLLER_PATH = "com.vaultid.controller";

    public static class RootHandler extends AbstractHttpHandler {

        @Override
        public void eventHandle(HttpExchange he) throws IOException {

            try {

                //Rest Route and ID
                String routePath = he.getRequestURI().getPath();
                String[] route = routePath.split("/");
                
                System.out.println("Route:"+Arrays.toString(route));

                if(route.length<=0){
                    JSONObject object = new JSONObject();
                    object.put("type", "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html");
                    object.put("status", "success");
                    object.put("detail", "VaultID Handler");
                    String response = object.toString();
                    byte[] responseBytes = response.getBytes("UTF-8");
                    he.sendResponseHeaders(200, responseBytes.length);
                    OutputStream os = he.getResponseBody();
                    os.write(responseBytes);
                    os.close();
                    return;
                }
//                
                if(!RouteManager.PUBLIC_ROUTES.contains(route[1])){
                    
                JSONObject object = new JSONObject();
                object.put("type", "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html");
                object.put("title", "Access Denied");
                object.put("status", 403);
                object.put("detail", "Access Denied");
                String response = object.toString();
                byte[] responseBytes = response.getBytes("UTF-8");
                he.sendResponseHeaders(403, responseBytes.length);
                OutputStream os = he.getResponseBody();
                os.write(responseBytes);
                os.close();
                return;    
                }
                
            
                //Controller Name
                String controllerName = Helper.getControllerName(route[1]);

                //Create route by reflection class
                Object routeReflection = null;
                routeReflection = Class.forName(CONTROLLER_PATH + "." + controllerName).newInstance();
                Object controllerReturn;

                Map<String, Object> parameters = new HashMap<String, Object>();

                //Parameters from URI
                URI requestedUri = he.getRequestURI();
                String queryString = requestedUri.getRawQuery();
                parseQueryRequest(queryString, parameters);

                
                System.out.println("parameres :"+parameters.size());
                //Get ContentType from request
                String ContentType = he.getRequestHeaders().getFirst("Content-Type") == null ? "" : he.getRequestHeaders().getFirst("Content-Type");
                
                InputStream inputStream = he.getRequestBody();
                String requestBody = getStringFromInputStream(inputStream);
                
                System.out.println("request body:"+requestBody);
                
                if(ContentType.equals(CONTENT_TYPE_APPLICATION_JSON)){
                    if(requestBody != null && !requestBody.isEmpty()){
                        parseJsonRequest(requestBody, parameters);
                    }
                }
                else if(ContentType.equals(CONTENT_TYPE_FORM_URLENCODED)){
                    if(requestBody != null && !requestBody.isEmpty()){
                        parseQueryRequest(requestBody, parameters);
                    }
                }
                else{
                    throw new UnsupportedMediaTypeException();
                }
                
                System.out.println("parameters:"+parameters.size());
                //Find CallbackMethod
                String restFulMethod = "fetchAll";//Default method
                if (route.length == 3) {//Entity
                    boolean sendData = false;
                    if (he.getRequestMethod().equals("GET")) {
                        restFulMethod = "fetch";
                    } else if (he.getRequestMethod().equals("PUT")) {
                        restFulMethod = "update";
                        sendData = true;
                    } else if (he.getRequestMethod().equals("PATCH")) {
                        restFulMethod = "patch";
                        sendData = true;
                    } else if (he.getRequestMethod().equals("DELETE")) {
                        restFulMethod = "delete";
                    }

                    if (sendData) {
                        Method s = routeReflection.getClass().getMethod(restFulMethod, String.class, Map.class);
                        controllerReturn = s.invoke(routeReflection, route[2], parameters);
                    } else {
                        Method s = routeReflection.getClass().getMethod(restFulMethod, String.class);
                        controllerReturn = s.invoke(routeReflection, route[2]);
                    }

                } else {//Collection

                    if (he.getRequestMethod().equals("GET")) {
                        restFulMethod = "fetchAll";
                    } else if (he.getRequestMethod().equals("POST")) {
                        restFulMethod = "create";
                    } else if (he.getRequestMethod().equals("PUT")) {
                        restFulMethod = "replaceList";
                    } else if (he.getRequestMethod().equals("PATCH")) {
                        restFulMethod = "patchList";
                    } else if (he.getRequestMethod().equals("DELETE")) {
                        restFulMethod = "deleteList";
                    }
                    System.out.println("parameter:"+parameters.size());
                    
                    Method s = routeReflection.getClass().getMethod(restFulMethod, Map.class);
                    controllerReturn = s.invoke(routeReflection, parameters);
                }

                //If JSONObject
                if (controllerReturn.getClass().equals(JSONObject.class)) {

                    //Get Status code by requestMethod
                    String response = controllerReturn.toString();
                    byte[] responseBytes = response.getBytes("UTF-8");
                    he.sendResponseHeaders(200, responseBytes.length);
                    OutputStream os = he.getResponseBody();
                    os.write(responseBytes);
                    os.close();
                }
                //IF String
                else if (controllerReturn.getClass().equals(String.class)) {
                    JSONObject object = new JSONObject();
                    object.put("status", controllerReturn);
                    String response = object.toString();
                    byte[] responseBytes = response.getBytes("UTF-8");
                    he.sendResponseHeaders(200, responseBytes.length);
                    OutputStream os = he.getResponseBody();
                    os.write(responseBytes);
                    os.close();
                } 
                //If Api Problem
                else if (controllerReturn.getClass().equals(ApiProblem.class)) {

                    //Get Status code and detail
                    String apiProblemCode = "" + controllerReturn.getClass().getMethod("getCode").invoke(controllerReturn);
                    String apiProblemStatus = (String) controllerReturn.getClass().getMethod("getStatus").invoke(controllerReturn);
                    String apiProblemMessage = (String) controllerReturn.getClass().getMethod("getMessage").invoke(controllerReturn);
                    
                    JSONObject object = new JSONObject();
                    object.put("type", "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html");
                    object.put("status", apiProblemStatus);
                    object.put("detail", apiProblemMessage);
                    String response = object.toString();
                    byte[] responseBytes = response.getBytes("UTF-8");
                    he.sendResponseHeaders(Integer.parseInt(apiProblemCode), responseBytes.length);
                    OutputStream os = he.getResponseBody();
                    os.write(responseBytes);
                    os.close();
                } else {
                    throw new Exception("Invalid response: " + controllerReturn.getClass());
                }

            }catch(InvalidSessionException e){
             
                JSONObject object = new JSONObject();
                object.put("type", "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html");
                object.put("title", "Access Denied");
                object.put("status", 403);
                object.put("detail", "Invalid Session");
                String response = object.toString();
                byte[] responseBytes = response.getBytes("UTF-8");
                he.sendResponseHeaders(403, responseBytes.length);
                OutputStream os = he.getResponseBody();
                os.write(responseBytes);
                os.close();
                
                
            }catch (ClassNotFoundException e) {
                JSONObject object = new JSONObject();
                object.put("type", "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html");
                object.put("title", "Page not found");
                object.put("status", 404);
                object.put("detail", "Page not found");
                String response = object.toString();
                byte[] responseBytes = response.getBytes("UTF-8");
                he.sendResponseHeaders(404, responseBytes.length);
                OutputStream os = he.getResponseBody();
                os.write(responseBytes);
                os.close();

            } catch (JSONException e){
                JSONObject object = new JSONObject();
                object.put("type", "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html");
                object.put("title", "invalid body format");
                object.put("status", 400);
                object.put("detail", "Invalid JSON format sent");
                String response = object.toString();
                byte[] responseBytes = response.getBytes("UTF-8");
                he.sendResponseHeaders(400, responseBytes.length);
                OutputStream os = he.getResponseBody();
                os.write(responseBytes);
                os.close();
            } catch (UnsupportedMediaTypeException e) {
                JSONObject object = new JSONObject();
                object.put("type", "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html");
                object.put("title", "Unsupported Media Type");
                object.put("status", 415);
                object.put("detail", "Invalid content-type specified");
                String response = object.toString();
                byte[] responseBytes = response.getBytes("UTF-8");
                he.sendResponseHeaders(415, responseBytes.length);
                OutputStream os = he.getResponseBody();
                os.write(responseBytes);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject object = new JSONObject();
                object.put("type", "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html");
                object.put("title", "Internal Server Error");
                object.put("status", 500);
                object.put("detail", "Internal Server Error");
                String response = object.toString();
                byte[] responseBytes = response.getBytes("UTF-8");
                he.sendResponseHeaders(500, responseBytes.length);
                OutputStream os = he.getResponseBody();
                os.write(responseBytes);
                os.close();
            }
        }
    }

    public static class StopHandler extends AbstractHttpHandler {

        @Override
        public void eventHandle(HttpExchange he) throws IOException {

            JSONObject object = new JSONObject();
            object.put("status", "success");
            object.put("message", "Stopping service(s)");

            String response = object.toString();

            BuiltInHttpServer httpServer = new BuiltInHttpServer();
            if (httpServer.isStarted()) {
                System.out.println("Stopping http server");
                httpServer.Stop();
            }

            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class RestartHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {

            BuiltInHttpServer httpServer = new BuiltInHttpServer();

            if (httpServer.isStarted()) {

                System.out.println("Stopping http server");
                JSONObject object = new JSONObject();
                object.put("status", "success");
                object.put("message", "Restart services");
                String response = object.toString();
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
                httpServer.Stop();
                while (httpServer.isStarted()) {

                }
                httpServer.Start(httpServer.getPort());
            }
        }
    }
}
