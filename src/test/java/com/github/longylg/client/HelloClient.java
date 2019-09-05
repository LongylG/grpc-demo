package com.github.longylg.client;

import com.github.longylg.grpc.FileReply;
import com.github.longylg.grpc.FileRequest;
import com.github.longylg.grpc.GreeterGrpc;
import com.github.longylg.grpc.HelloReply;
import com.github.longylg.grpc.HelloRequest;
import com.google.protobuf.ByteString;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class HelloClient {

    private final ManagedChannel channel;

    private final GreeterGrpc.GreeterBlockingStub blockingStub;
    private final GreeterGrpc.GreeterStub greeterStub;

    public HelloClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                                  // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                                  // needing certificates.
                                  .enableRetry()
                                  .keepAliveTimeout(5000, TimeUnit.SECONDS)
                                  .idleTimeout(5000, TimeUnit.SECONDS)
                                  .usePlaintext()
                                  .build());
    }

    /**
     * Construct client for accessing HelloWorld server using the existing channel.
     */
    HelloClient(ManagedChannel channel) {
        this.channel = channel;

        blockingStub = GreeterGrpc.newBlockingStub(channel);
        greeterStub = GreeterGrpc.newStub(channel);

    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Say hello to server.
     */
    public void greet(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
            System.out.println("got msg:" + response.getMessage());
        } catch (StatusRuntimeException e) {
            return;
        }
    }

    public void upload(List<byte[]> byteList) throws InterruptedException {
        final CountDownLatch finishLatch = new CountDownLatch(byteList.size());

//        FileReply reply = greeterStub.uploadFile(ResponseObserver);
//        System.out.println(reply.getPath());
        StreamObserver<FileRequest> streamObserver = greeterStub.uploadFile(new StreamObserver<FileReply>() {
            @Override
            public void onNext(FileReply value) {
                System.out.println(value.getPath());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
                finishLatch.countDown();

            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
                finishLatch.countDown();

            }
        });
        AtomicInteger id = new AtomicInteger();
        byteList.forEach(bytes -> {
            streamObserver.onNext(FileRequest.newBuilder().setBlob(ByteString.copyFrom(bytes)).setId(id.get() + "").build());
            id.getAndIncrement();
        });
        streamObserver.onCompleted();

    }

    public void reqStreamGreet(List<String> name) {
        final CountDownLatch finishLatch = new CountDownLatch(name.size());
        StreamObserver<HelloRequest> streamObserver = greeterStub.reqStreamSayHello(new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply value) {
                System.out.println(value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
                finishLatch.countDown();
            }
        });
        AtomicInteger id = new AtomicInteger();

        name.forEach(string -> {
            streamObserver.onNext(HelloRequest.newBuilder().setName(string).setId(id.get() + "").build());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            id.getAndIncrement();
        });
        streamObserver.onCompleted();
    }

    public void respStreamGreet(String name, String id) {
        Iterator<HelloReply> iterator = blockingStub.respStreamSayHello(HelloRequest.newBuilder().setName(name).setId(id).build());
        while (iterator.hasNext()) {
            System.out.println("msg:" + iterator.next().getMessage());
        }
    }


}

