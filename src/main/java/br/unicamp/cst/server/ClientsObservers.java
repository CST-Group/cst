package MindViewerTest;

import io.grpc.stub.StreamObserver;
import pb.Service;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientsObservers implements Serializable {
    private static ClientsObservers clientsObservers;
    private static ArrayList<StreamObserver<Service.codeletsResponse>> observers;

    public static ClientsObservers getInstance(){
        if (clientsObservers == null){
            synchronized (ClientsObservers.class) {
                observers = new ArrayList<StreamObserver<Service.codeletsResponse>>();
                clientsObservers = new ClientsObservers();
            }
        }
        return clientsObservers;
    }


    public void addObserver(StreamObserver<Service.codeletsResponse> observer){
        observers.add(observer);
    }

    public ArrayList<StreamObserver<Service.codeletsResponse>> getObservers(){
        return observers;
    }
}
