package com.vaultid.server;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;

/**
 * Description of AbstractController
 *
 * @author Paulo Filipe Macedo dos Santos <paulo.filipe@live.com>
 */
public abstract class AbstractController {

    private HttpExchange he;
    
    /**
     * Get HttpExchange
     * @return HttpExchange
     */
    public HttpExchange getHttpExchange(){
        return this.he;
    }
    
    /**
     * Set HttpExchange
     * @param he
     */
    public void setHttpExchange(HttpExchange he){
        this.he = he;
    }
    
    /**
     * Create a resource
     *
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    public Object create(Map<String, Object> data) {
        return new ApiProblem("The POST method has not been defined",405);
    }

    /**
     * Delete a resource
     *
     * @param id
     * @return ApiProblem|mixed
     */
    public Object delete(String id) {
        return new ApiProblem("The DELETE method has not been defined for individual resources",405);
    }

    /**
     * Delete a collection, or members of a collection
     *
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    public Object deleteList(Map<String, Object> data) {
        return new ApiProblem("The DELETE method has not been defined for collections",405);
    }

    /**
     * Fetch a resource
     *
     * @param id
     * @return ApiProblem|mixed
     */
    public Object fetch(String id) {
        return new ApiProblem("The GET method has not been defined for individual resources",405);
    }

    /**
     * Fetch all or a subset of resources
     *
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    public Object fetchAll(Map<String, Object> data) {
        return new ApiProblem("The GET method has not been defined for collections",405);
    }

    /**
     * Patch (partial in-place update) a resource
     *
     * @param id
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    public Object patch(String id, Map<String, Object> data) {
        return new ApiProblem("The PATCH method has not been defined for individual resources",405);
    }

    /**
     * Patch (partial in-place update) a collection or members of a collection
     *
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    public Object patchList(Map<String, Object> data) {
        return new ApiProblem("The PATCH method has not been defined for collections",405);
    }

    /**
     * Replace a collection or members of a collection
     *
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    public Object replaceList(Map<String, Object> data) {
        return new ApiProblem("The PUT method has not been defined for collections",405);
    }

    /**
     * Update a resource
     *
     * @param id
     * @param data Map<String, Object>
     * @return ApiProblem|mixed
     */
    public Object update(String id, Map<String, Object> data) {
        return new ApiProblem("The PUT method has not been defined for individual resources",405);
    }

}
