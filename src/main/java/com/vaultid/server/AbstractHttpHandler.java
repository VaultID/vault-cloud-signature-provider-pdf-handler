package com.vaultid.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description of AbstractHttpHandler
 * 
 * @author Paulo Filipe Macedo dos Santos <paulo.filipe@live.com>
 * @author Luan Lino Matias dos Santos <luansantosmatias@hotmail.com>
 */
public abstract class AbstractHttpHandler implements HttpHandler {

    public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    public static final String DEFAULT_CONTENT_TYPE = "application/json";
    public static final String DEFAULT_SERVER_NAME = "RF Server (Java) ";
    public static final String DEFAULT_ALLOW_ORIGIN = "*";

    /**
     * Generic Handler Interceptor
     */
    @Override
    public void handle(HttpExchange he) throws IOException {

        //Trigger event Media Type
        eventMediaType(he);

        //Trigger event (Call Handlers methods using context)
        eventHandle(he);

    }

    public void eventHandle(HttpExchange he) throws IOException {

    }

    /**
     * Event Media Type (Can Override This on Handle)
     *
     * @param he
     */
    public void eventMediaType(HttpExchange he) throws IOException {

        //Set Default Headers
        he.getResponseHeaders().add("Content-Type", DEFAULT_CONTENT_TYPE);
        he.getResponseHeaders().add("Server", DEFAULT_SERVER_NAME);
        he.getResponseHeaders().add("Access-Control-Allow-Origin", DEFAULT_ALLOW_ORIGIN); //Enable CORS for browsers
    }

    /**
     * Parse Query Request - Using QueryString
     *
     * @param query String
     * @param parameters Map<String, Object>
     * @throws java.io.UnsupportedEncodingException
     */
    public static void parseQueryRequest(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");

            for (String pair : pairs) {
                String param[] = pair.split("[=]");

                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);
                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }

    /**
     * Parse Query Request- Using QueryString
     *
     * @param query String
     * @param parameters Map<String, Object>
     * @throws java.io.UnsupportedEncodingException
     * @throws com.plcryptopocket.json.JSONException
     */
    public static void parseJsonRequest(String query, Map<String, Object> parameters) throws UnsupportedEncodingException,JSONException {
        JSONObject jsonObj = new JSONObject(query);
        Map<String, Object> mappedParams = Helper.toMap(jsonObj);
        for (Entry<String, Object> param : mappedParams.entrySet()) {
            parameters.put(param.getKey(), param.getValue());
        }
    }

    public static String getStringFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            throw e;
            //LOGGER.error("IOException on reading input stream: " + e.getMessage()); 
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw e;
                    //LOGGER.error("Error closing bufferedReader in test"); 
                }
            }
        }
        return stringBuilder.toString();
    }
}
