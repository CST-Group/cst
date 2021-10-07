/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Mind;
import com.google.gson.Gson;
import express.Express;
import express.middleware.CorsOptions;
import express.middleware.Middleware;

/**
 *
 * @author rgudwin
 */
public class RESTServer {
    
    public RESTServer(Mind m, int port) {
        Express app = new Express();
        Gson gson = new Gson();
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
