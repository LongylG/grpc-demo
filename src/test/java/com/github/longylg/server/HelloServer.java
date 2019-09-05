package com.github.longylg.server;


import com.github.longylg.service.HelloServiceImpl;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class HelloServer {
    Server server;

    public static void main(String[] args) throws IOException, InterruptedException {

        final HelloServer server = new HelloServer();
        server.start();
        server.blockUntiShutdown();

    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(6565)
                              .addService(new HelloServiceImpl())
                              .build()
                              .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                HelloServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }


    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntiShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

}
