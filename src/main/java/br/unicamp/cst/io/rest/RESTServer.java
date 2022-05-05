/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.support.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import express.Express;
import express.middleware.CorsOptions;
import express.middleware.Middleware;

/**
 * This is the main class for using REST to monitor inner activities from CST
 * It is used to provide a REST server to monitor whatever is happening within a CST mind 
 * Depending on the constructor, the user can set the mind, the port to be used, if 
 * the JSON describing the inner details of the mind should be rendered with pretty printing
 * and a way to limit the access of external users to the server
 * @author rgudwin
 */
public class RESTServer {
    
    /**
     * 
     * @param m the mind to observe
     * @param port the port to install the REST server
     */
    public RESTServer(Mind m, int port) {
        this(m,port,false);
    }
    
    /**
     * 
     * @param m the mind to observe
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     */
    public RESTServer(Mind m, int port, boolean pretty) {
        this(m,port,pretty,"*");
    }
    
    /**
     * 
     * @param m the mind to observe
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     * @param origin a pattern for users allowed to access the server - use "*" to allow everyone
     */
    public RESTServer(Mind m, int port, boolean pretty, String origin) {
        Express app = new Express();
        Gson gson;
        if (pretty)
            gson = new GsonBuilder().registerTypeAdapter(Memory.class, new InterfaceAdapter<MemoryObject>())
                             .registerTypeAdapter(Memory.class, new InterfaceAdapter<MemoryContainer>())
                             .setPrettyPrinting().create();
        else 
            gson = new Gson();
        CorsOptions corsOptions = new CorsOptions();
        corsOptions.setOrigin(origin);
        app.use(Middleware.cors(corsOptions));
        app.get("/", (req, res) -> {
            MindJson myJson = new MindJson(m.getRawMemory().getAllMemoryObjects(),m.getCodeRack().getAllCodelets());
            res.send(gson.toJson(myJson));
        });
        app.listen(port);
    }
    
}
