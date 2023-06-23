package br.unicamp.cst.core.entities;

import br.unicamp.cst.io.rest.MemoryJson;
import br.unicamp.cst.support.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import express.Express;
import express.middleware.CorsOptions;
import express.middleware.Middleware;

public class RESTMemoryContainer extends MemoryContainer {
    long refresh = 0; // A refresh of 0 means that every call will generate a new probe in mind
    long lastaccess = 0;
    String lastmessage = "";
    Memory internalMemory;

    public RESTMemoryContainer(int port) {
        this(port,false);
    }

    /**
     *
     * @param hostname hostname of the REST server
     * @param port the port to install the REST server
     */
    public RESTMemoryContainer(String hostname, int port) {
        this(hostname, port,false);
    }

    /**
     *
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     */
    public RESTMemoryContainer(int port, boolean pretty) {
        this(port,pretty,"*");
    }


    /**
     * @param hostname the hostname of the REST server
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     */
    public RESTMemoryContainer(String hostname, int port, boolean pretty) {
        this(hostname, port,pretty,"*", 0L);
    }

    /**
     *
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     * @param origin a pattern for users allowed to access the server - use "*" to allow everyone
     */
    public RESTMemoryContainer(int port, boolean pretty, String origin) {
        this("localhost", port,pretty,origin,0L);
    }

    /**
     * @param hostname the hostname of the REST server
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     * @param origin a pattern for users allowed to access the server - use "*" to allow everyone
     * @param nrefresh the refresh period in milliseconds
     */
    public RESTMemoryContainer(String hostname, int port, boolean pretty, String origin, long nrefresh) {
        refresh = nrefresh;
        Express app = new Express(hostname);
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
            long currentaccess = System.currentTimeMillis();
            long diff = currentaccess - lastaccess;
            if (diff > refresh) {
                MemoryJson myJson = new MemoryJson(this);
                lastmessage = gson.toJson(myJson);
                lastaccess = currentaccess;
            }
            res.send(lastmessage);
        });
        app.post("/", (req, res) -> {
            // Will match every request which uses the 'POST' method and matches the /login' path
            long currentaccess = System.currentTimeMillis();
            long diff = currentaccess - lastaccess;
            if (diff > refresh) {
                String I = req.getFormQuery("I");
                double evaluation = Double.parseDouble(req.getFormQuery("evaluation"));
                // Process data
                this.setI(I);
                this.setEvaluation(evaluation);
                lastmessage = "I: " + I + ", Evaluation: " + evaluation;
                lastaccess = currentaccess;
            }
            res.send(lastmessage);
        });
        app.listen(port);
    }

}

