package com.vaultid.controller;

import com.itextpdf.text.pdf.PdfName;
import com.vaultid.engine.PreparePdfToSignLogic;
import com.vaultid.main.Constants;
import com.vaultid.server.AbstractController;
import com.vaultid.server.ApiProblem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.sf.jsignpdf.BasicSignerOptions;
import net.sf.jsignpdf.SecundarySignerOptions;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.commons.lang3.StringUtils;

public class PrepareFileToSignatureController extends AbstractController {

    /**
     * Create a resource
     *
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    @Override
    public Object create(Map<String, Object> data) {

        /**
         * Validate Params
         */
        if (data.get("pdfFile") == null) {
            return new ApiProblem(Constants.ERROR_VALIDATION, "Invalid 'pdfFile' param", 422);
        }

        //Append mode
        String append = data.get("append") == null ? "true" : "" + data.get("append");

        //AutoFixDocument mode
        String autoFixDocument = data.get("autoFixDocument") == null ? "true" : "" + data.get("autoFixDocument");

        //IsVisibleSignature
        String isVisibleSignature = data.get("isVisibleSignature") == null ? "true" : "" + data.get("isVisibleSignature");

        /**
         * Visible signature params
         */
        if (isVisibleSignature.equals("true")) {
            if (data.get("imageFile") == null) {
                return new ApiProblem(Constants.ERROR_VALIDATION, "Invalid 'imageFile' param", 422);
            }
            if (data.get("page") == null) {
                return new ApiProblem(Constants.ERROR_VALIDATION, "Invalid 'page' param", 422);
            }
            if (data.get("x") == null) {
                return new ApiProblem(Constants.ERROR_VALIDATION, "Invalid 'x' param", 422);
            }
            if (data.get("y") == null) {
                return new ApiProblem(Constants.ERROR_VALIDATION, "Invalid 'y' param", 422);
            }
            if (data.get("width") == null) {
                return new ApiProblem(Constants.ERROR_VALIDATION, "Invalid 'width' param", 422);
            }
            if (data.get("height") == null) {
                return new ApiProblem(Constants.ERROR_VALIDATION, "Invalid 'height' param", 422);
            }
        }

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
            if (append.equals("true")) {
                options.setAppend(true);
            }

            //Set visible signature
            if (isVisibleSignature.equals("true")) {
                options.setVisible(true); //Const
                options.setBgImgPath((String) data.get("imageFile"));
                options.setPage((Integer) data.get("page"));
                options.setPositionLLX((Integer) data.get("x"));
                options.setPositionLLY((Integer) data.get("y"));
                options.setPositionURX((Integer) data.get("width"));
                options.setPositionURY((Integer) data.get("height"));
                options.setL2Text("");
                
                if (data.get("visibleSignatureField") != "") {
                    options.setVisibleSignatureField((String) data.get("visibleSignatureField"));
                }
                
            }
            
            //Set secundary visible signature
            if (data.get("visibleSignatureSecundaryFields") != null) {
                ArrayList secundarySignatures = (ArrayList) data.get("visibleSignatureSecundaryFields");
                for (int i = 0; i < secundarySignatures.size(); i++) {
                    HashMap<String, Object> field = (HashMap<String, Object>) secundarySignatures.get(i);                
                    SecundarySignerOptions option = new SecundarySignerOptions();
                    option.setBgImgPath((String) field.get("img"));
                    option.setPage((Integer) field.get("page"));
                    option.setPositionLLX((Integer) field.get("x"));
                    option.setPositionLLY((Integer) field.get("y"));
                    option.setPositionURX((Integer) field.get("width"));
                    option.setPositionURY((Integer) field.get("height"));
                    options.addSecundarySignature(option);
                }
            }

            if (data.get("reason") != null) {
                options.setReason((String) data.get("reason"));
            }

            if (data.get("location") != null) {
                options.setLocation((String) data.get("location"));
            }

            if (data.get("contact") != null) {
                options.setContact((String) data.get("contact"));
            }

            final PreparePdfToSignLogic signer = new PreparePdfToSignLogic(options);

            if (data.get("signerName") != null) {
                signer.setSignerName((String) data.get("signerName"));
            }

            if (data.get("subfilter") != null) {
                signer.setSubfilter((String) data.get("subfilter"));
            }
            
            if (data.get("filter") != null) {
                String filter = (String) data.get("filter");
                if( filter.equalsIgnoreCase("PBAD.PAdES") ) {
                    signer.setFilter( new PdfName("PBAD_PAdES") );
                }
            }
            
            if (data.get("type") != null) {
                signer.setType((String) data.get("type"));
            }
            signer.setAutofixDocument(autoFixDocument.equals("true")); //Enable update document before sign (if needed)
            //Validate fields
            if (data.get("fields") != null) {
                ArrayList fields = (ArrayList) data.get("fields");
                signer.setFields(fields);
            }
            
            //Validate extraInfo
            if (data.get("extraInfo") != null) {
                ArrayList extraInfo = (ArrayList) data.get("extraInfo");
                signer.setExtraInfo(extraInfo);
            }
            
            signer.signFile();

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
