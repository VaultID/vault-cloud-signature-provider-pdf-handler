package com.vaultid.server;

/**
 * Description of ApiProblem
 * 
 * @author Paulo Filipe Macedo dos Santos <paulo.filipe@live.com>
 */
public class ApiProblem {
    
    private int code;
    
    private String status;
    
    private String message;

    public ApiProblem(String message,int code) {
        this.message = message;
        this.code = code;
    }
    
    public ApiProblem(String status, String message,int code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
