package com.github.longylg.service;


import com.github.longylg.grpc.GreeterGrpc;
import com.github.longylg.grpc.HelloReply;
import com.github.longylg.grpc.HelloRequest;

import org.lognet.springboot.grpc.GRpcService;

import io.grpc.stub.StreamObserver;

@GRpcService
public class SpringbootService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("hi," + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
