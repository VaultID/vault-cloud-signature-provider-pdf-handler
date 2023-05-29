package com.vaultid.main;

import com.vaultid.server.BuiltInHttpServer;
import java.util.Arrays;

/**
 *
 */
public class ArgsManager {
    
    static final String  HELP         = "help";
    
    static final String  START        = "start";
    static final String  SECURE_START = "securestart";
    static final String  STOP         = "stop";
    static final String  RESTART      = "restart";
        
    String[] commands = new String[]{
        START,
        SECURE_START,
        STOP,
        RESTART,
        
        //Valid commands
        "p",
        "f"
    };
    
    String[] descriptions = new String[]{
        "Start built-in HTTP Webservice (-p HTTP Port)",    //START
        "Start built-in HTTPS Webservice (-p HTTPS Port)",  //SECURE_START
        "Stop Webservice (-f Force stop)",                  //STOP
        "Restart built-in Webservice (-f Force stop)",      //RESTART   
    }; 
    
    /**
     * Manage application args
     * @param args List
     */
    public void manager(String[] args){
        
        //Not send argument or send help request
        if(args.length == 0 || args[0].equals("-help")){
            System.out.print(getAvailable());
        }
        else{
            
            //Validate args
            String isValidMessage = validateArgs(args);
            if(isValidMessage != null){
                System.err.println(isValidMessage);
            }
            else{
                route(args);
            }
        }
    }
    
    /**
     * Return a list Avaliable Services
     * @return String
     */
    public String getAvailable(){
        String returnStr =  "###################################################### \n##### AVAILABLE SERVICES  - VAULT CLOUD SIGNATURE PROVIDER - PDF HANDLER #####\n######################################################\n\n";
        
        for(int i = 0; i < commands.length ; i++){
            if(descriptions.length > i){
                returnStr += "-" + commands[i];

                for(int j = commands[i].length(); j < 20; j++){
                    returnStr += " ";
                }
                returnStr += descriptions[i] + "\n";   
            }
        }
        return returnStr;
    }
    
    /***
     * Validate args
     * @param args String[]
     * @return String
     */
    public String validateArgs(String[] args){
        for(String arg: args){
            if(arg.startsWith("-") && Arrays.asList(commands).contains(arg.replace("-", "")) == false){
                return "\nInvalid argument: " + arg + "\n\n" + getAvailable();
            }
        }
        return null;
    }
    
    /**
     * Find Arg Value
     * @param arg String Arg to get value
     * @param args String[] List args
     * @return String
     */
    public String findArgValue(String arg,String[] args){

        for(int i = 0; i < args.length ; i++){
            //Is a param/command
            if(args[i].startsWith("-") && args[i].replace("-", "").equals(arg)){
                if(i + 1 <= args.length && args[i + 1].startsWith("-") == false){
                    return args[i + 1];
                }
            }
        }
        
        return null;
    }
    
    /**
     * Route
     * @param args String[]
     */
    public void route(String[] args){
        for(String arg: args){
            
            //Search main arg
            if(arg.startsWith("-")){
                
                //START HTTP SERVER
                if(arg.replace("-", "").equals(START)){
                    BuiltInHttpServer httpServer = new BuiltInHttpServer();
                    String port = findArgValue("p",args);
                    //Start default port
                    if(port == null){
                        httpServer.Start(httpServer.DEFAULT_PORT);
                    }
                    else{
                        httpServer.Start(Integer.parseInt(port));
                    }
                }
            }
        }
    }
}
