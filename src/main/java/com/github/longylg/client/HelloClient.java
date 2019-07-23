package com.github.longylg.client;

import com.github.longylg.grpc.GreeterGrpc;
import com.github.longylg.grpc.HelloReply;
import com.github.longylg.grpc.HelloRequest;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class HelloClient {

    private final ManagedChannel channel;

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                                  // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                                  // needing certificates.
                                  .usePlaintext()
                                  .build());
    }

    /**
     * Construct client for accessing HelloWorld server using the existing channel.
     */
    HelloClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = GreeterGrpc.newBlockingStub(channel);
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

    public static void main(String[] args) throws Exception {
        HelloClient client = new HelloClient("localhost", 49176);
        try {
            /* Access a service running on the local machine on port 50051 */
            String user = "world";
            if (args.length > 0) {
                user = args[0]; /* Use the arg as the name to greet if provided */
            }
            client.greet(user);

        } finally {
            client.shutdown();
        }
    }

}
