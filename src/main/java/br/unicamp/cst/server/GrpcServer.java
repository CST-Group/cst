package MindViewerTest;
import br.unicamp.cst.core.entities.CodeRack;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.core.entities.RawMemory;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import pb.Service;
import pb.grpcServiceGrpc;

import java.io.IOException;
import java.util.List;

public class GrpcServer extends grpcServiceGrpc.grpcServiceImplBase {

    private Mind m;
    private List<String> info;
    private Server server;

    public GrpcServer(Mind m){
        this.m = m;
    }

    public void start(int port) throws IOException {
        this.server = ServerBuilder.forPort(port).addService(this).build();
        this.server.start();
    }

    public void awaitTermination() throws InterruptedException {
        this.server.awaitTermination();
    }

    public void startAndAwaitTermination(int port) throws IOException, InterruptedException {
        this.start(port);
        this.awaitTermination();
    }

    @Override
    public void getMemoriesAndCodelets(Service.Empty request, StreamObserver<Service.APIResponse> responseObserver) {

        Service.APIResponse.Builder response = Service.APIResponse.newBuilder();
        CodeRack codeRack = m.getCodeRack();
        RawMemory rawMemory = m.getRawMemory();
        APIResponseObject apiResponseObject = new APIResponseObject(rawMemory.getAllMemoryObjects(), codeRack.getAllCodelets());
        for (int i = 0; i< rawMemory.getAllMemoryObjects().size(); i++){
            response.addMemories(i, apiResponseObject.getMemorie(i).getIMemoryProperties());
        }
        for (int i = 0; i< codeRack.getAllCodelets().size(); i++){
            response.addCodelets(i, apiResponseObject.getCodelet(i).getICodeletProperties());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMemories(pb.Service.Empty request,io.grpc.stub.StreamObserver<pb.Service.memoriesResponse> responseObserver) {
        Service.memoriesResponse.Builder response = Service.memoriesResponse.newBuilder();
        RawMemory rawMemory = m.getRawMemory();

        APIResponseObject apiResponseObject = new APIResponseObject(rawMemory.getAllMemoryObjects(), null);

        for (int i = 0; i< rawMemory.getAllMemoryObjects().size(); i++){
            response.addMemories(i, apiResponseObject.getMemorie(i).getIMemoryProperties());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getCodelets(pb.Service.Empty request,io.grpc.stub.StreamObserver<pb.Service.codeletsResponse> responseObserver) {
        Service.codeletsResponse.Builder response = Service.codeletsResponse.newBuilder();
        CodeRack codeRack = m.getCodeRack();

        APIResponseObject apiResponseObject = new APIResponseObject(null, codeRack.getAllCodelets());

        for (int i = 0; i< codeRack.getAllCodelets().size(); i++){
            response.addCodelets(i, apiResponseObject.getCodelet(i).getICodeletProperties());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
    @Override
    public void getInfo(pb.Service.indexInfo request,io.grpc.stub.StreamObserver<pb.Service.responseInfo> responseObserver) {
        Service.responseInfo.Builder response = Service.responseInfo.newBuilder();
        if (this.info.size()-1 < request.getIndex()) {
            response.setResponse(Service.jsonRetornado.newBuilder().setJson("{\"Erro\": \"Posicao invalida\"}").build());
        } else
            response.setResponse(Service.jsonRetornado.newBuilder().setJson(this.info.get((int)request.getIndex())).build());

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();

    }

    @Override
    public void setInfo(pb.Service.infoAdd request,io.grpc.stub.StreamObserver<pb.Service.responseOK> responseObserver){
        Service.responseOK.Builder response = Service.responseOK.newBuilder();
        this.info.add(request.getJson());
        response.setMensagem("Json armazenado na posição: " + (this.info.size()-1));
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }


}
