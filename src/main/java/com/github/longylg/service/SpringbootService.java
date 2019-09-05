package com.github.longylg.service;


import com.github.longylg.grpc.FileReply;
import com.github.longylg.grpc.FileRequest;
import com.github.longylg.grpc.GreeterGrpc;
import com.github.longylg.grpc.HelloReply;
import com.github.longylg.grpc.HelloRequest;

import org.lognet.springboot.grpc.GRpcService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.grpc.stub.StreamObserver;

@GRpcService
public class SpringbootService extends GreeterGrpc.GreeterImplBase {

    List<HelloReply> replies = new ArrayList<>();

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("hi," + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<FileRequest> uploadFile(StreamObserver<FileReply> responseObserver) {
        //双向流式
        return new StreamObserver<FileRequest>() {
            @Override
            public void onNext(FileRequest value) {
                System.out.println(value.getId());
                byte[] file = value.getBlob().toByteArray();
                String path = "/home/liyulong/Desktop/" + new Random().nextInt(1000) + ".xml";
                try (OutputStream out = new FileOutputStream(path)) {
                    out.write(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                responseObserver.onNext(FileReply.newBuilder().setPath(path).build());
//                System.out.println(value.getBlob());
//                responseObserver.onNext(FileReply.newBuilder().setPath(new Random().nextLong()+ "").build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }


    @Override
    public StreamObserver<HelloRequest> reqStreamSayHello(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<HelloRequest>() {
            StringBuffer sb = new StringBuffer();

            @Override
            public void onNext(HelloRequest value) {
                System.out.println(value.getId());
                //因为响应非流式，不能爱此此处调用responseObserver.onNext();而是处理完所有接受的数据在关闭时一次返回
                sb.append(value.getName() + "|");
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(HelloReply.newBuilder().setMessage("hi," + sb.toString()).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void respStreamSayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        //流式返回多条数据
        List<HelloRequest> features = Arrays.asList(
            HelloRequest.newBuilder().setName("lee").setId("1").build(),
            HelloRequest.newBuilder().setName("tam").setId("2").build(),
            HelloRequest.newBuilder().setName("ti").setId("3").build()

        );
        for (HelloRequest feature : features) {

            System.out.println(feature.getId());
            HelloReply reply = HelloReply.newBuilder().setMessage("hi," + feature.getName()).build();
            responseObserver.onNext(reply);
        }
        responseObserver.onCompleted();
    }
}
