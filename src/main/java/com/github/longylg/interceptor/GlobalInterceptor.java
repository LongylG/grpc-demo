package com.github.longylg.interceptor;

import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.springframework.stereotype.Component;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

@GRpcGlobalInterceptor
@Component
public class GlobalInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        System.out.println("global interceptor...");
        return next.startCall(call, headers);
    }
}
