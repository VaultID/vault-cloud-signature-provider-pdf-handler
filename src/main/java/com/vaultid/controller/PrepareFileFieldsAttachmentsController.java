package com.vaultid.controller;

import com.vaultid.engine.PreparePdfFieldsLogic;
import com.vaultid.engine.PreparePdfToSignLogic;
import com.vaultid.main.Constants;
import com.vaultid.server.AbstractController;
import com.vaultid.server.ApiProblem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.sf.jsignpdf.BasicSignerOptions;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.lang3.StringUtils;


public class PrepareFileFieldsAttachmentsController extends AbstractController {

    /**
     * Create a resource
     *
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    @Override
    public Object create(Map<String, Object> data) {
        //https://developers.itextpdf.com/examples/miscellaneous-itext5/embedded-files
        /**
         * Validate Params
         */
        if (data.get("pdfFile") == null) {
            return new ApiProblem(Constants.ERROR_VALIDATION, "Invalid 'pdfFile' param", 422);
        }
        
        //Append mode
        String append = data.get("append") == null ? "true" :  "" + data.get("append");
        
        //AutoFixDocument mode
        String autoFixDocument = data.get("autoFixDocument") == null ? "true" : "" + data.get("autoFixDocument");
                
        try {
            BasicSignerOptions options = new BasicSignerOptions();
        
            //Set In file name
            String inFileName = (String) data.get("pdfFile");
            options.setInFile(inFileName);
            
            //Set Out file name
            String tmpExtension = "";
            String tmpNameBase = StringUtils.defaultIfBlank(inFileName, null);
            if (tmpNameBase.toLowerCase().endsWith(".pdf")) {
                final int tmpBaseLen = tmpNameBase.length() - 4;
                tmpExtension = tmpNameBase.substring(tmpBaseLen);
                tmpNameBase = tmpNameBase.substring(0, tmpBaseLen);
            }
            String outFileName = tmpNameBase + "_prepared" + tmpExtension;
            options.setOutFile(outFileName);
            
            //Append document
            if(append.equals("true")){
                options.setAppend(true);
            }
            
            final PreparePdfFieldsLogic handler = new PreparePdfFieldsLogic(options);

            handler.setAutofixDocument(autoFixDocument.equals("true")); //Enable update document before sign (if needed)
            //Validate fields
            if(data.get("fields") != null){
                ArrayList fields = (ArrayList) data.get("fields");
                handler.setFields(fields);
            }
            
            handler.prepareFile();
            
            
            JSONObject obj = new JSONObject();
            obj.put("status", Constants.OK);
            obj.put("message", "File prepareted!!!!");
            obj.put("file", outFileName);

            return obj;

        } catch (Exception e) {
            System.out.println("Erro:" + e);
            return new ApiProblem(Constants.ERROR, "Error to prepare signature", 400);
        }
    }
    
    /**
     * Fetch all or a subset of resources
     *
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    public Object fetchAll(Map<String, Object> data) {

        try {

            JSONObject response = new JSONObject();

            JSONArray items = new JSONArray();

            response.put("items", items);
            response.put("status", Constants.OK);
            response.put("message", "All Loaded.");

            return response;

        } catch (Exception e) {
            return new ApiProblem(Constants.ERROR, e.getMessage(), 400);
        }

    }

}
