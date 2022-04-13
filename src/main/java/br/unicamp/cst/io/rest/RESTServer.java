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
 *
 * @author rgudwin
 */
public class RESTServer {
    
    public RESTServer(Mind m, int port) {
        this(m,port,false);
    }
    
    public RESTServer(Mind m, int port, boolean pretty) {
        Express app = new Express();
        Gson gson;
        if (pretty)
            gson = new GsonBuilder().registerTypeAdapter(Memory.class, new InterfaceAdapter<MemoryObject>())
                             .registerTypeAdapter(Memory.class, new InterfaceAdapter<MemoryContainer>())
                             .setPrettyPrinting().create();
        else 
            gson = new Gson();
        CorsOptions corsOptions = new CorsOptions();
        corsOptions.setOrigin("*");
        app.use(Middleware.cors(corsOptions));
        app.get("/", (req, res) -> {
            MindJson myJson = new MindJson(m.getRawMemory().getAllMemoryObjects(),m.getCodeRack().getAllCodelets());
            res.send(gson.toJson(myJson));
        });
        app.listen(port);
    }
    
}
