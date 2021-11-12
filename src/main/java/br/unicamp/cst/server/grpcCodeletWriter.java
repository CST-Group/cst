package MindViewerTest;

import br.unicamp.cst.core.entities.Codelet;
import io.grpc.stub.StreamObserver;
import pb.Service;

import java.util.ArrayList;

public class grpcCodeletWriter extends Codelet {
    private ArrayList<Integer> clientesAtivos;
    private Boolean send = Boolean.FALSE;
    private ClientsObservers clientsObservers;

    public grpcCodeletWriter(String name, ArrayList<Integer> clientesAtivos){
        setName(name);
        this.clientesAtivos = clientesAtivos;
        this.clientsObservers = ClientsObservers.getInstance();
    }

    @Override
    public void accessMemoryObjects() {

    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {

    }
    

}
