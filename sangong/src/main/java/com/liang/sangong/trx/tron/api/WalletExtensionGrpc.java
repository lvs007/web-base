package com.liang.sangong.trx.tron.api;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

import io.grpc.stub.ClientCalls;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.9.0)",
    comments = "Source: api/api.proto")
public final class WalletExtensionGrpc {

  private WalletExtensionGrpc() {
  }

  public static final String SERVICE_NAME = "protocol.WalletExtension";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @Deprecated // Use {@link #getGetTransactionsFromThisMethod()} instead.
  public static final io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> METHOD_GET_TRANSACTIONS_FROM_THIS = getGetTransactionsFromThisMethod();

  private static volatile io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> getGetTransactionsFromThisMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> getGetTransactionsFromThisMethod() {
    io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated, com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> getGetTransactionsFromThisMethod;
    if ((getGetTransactionsFromThisMethod = WalletExtensionGrpc.getGetTransactionsFromThisMethod)
        == null) {
      synchronized (WalletExtensionGrpc.class) {
        if ((getGetTransactionsFromThisMethod = WalletExtensionGrpc.getGetTransactionsFromThisMethod)
            == null) {
          WalletExtensionGrpc.getGetTransactionsFromThisMethod = getGetTransactionsFromThisMethod =
              io.grpc.MethodDescriptor.<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated, com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(
                      "protocol.WalletExtension", "GetTransactionsFromThis"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new WalletExtensionMethodDescriptorSupplier("GetTransactionsFromThis"))
                  .build();
        }
      }
    }
    return getGetTransactionsFromThisMethod;
  }

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @Deprecated // Use {@link #getGetTransactionsFromThis2Method()} instead.
  public static final io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> METHOD_GET_TRANSACTIONS_FROM_THIS2 = getGetTransactionsFromThis2Method();

  private static volatile io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> getGetTransactionsFromThis2Method;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> getGetTransactionsFromThis2Method() {
    io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated, com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> getGetTransactionsFromThis2Method;
    if ((getGetTransactionsFromThis2Method = WalletExtensionGrpc.getGetTransactionsFromThis2Method)
        == null) {
      synchronized (WalletExtensionGrpc.class) {
        if ((getGetTransactionsFromThis2Method = WalletExtensionGrpc.getGetTransactionsFromThis2Method)
            == null) {
          WalletExtensionGrpc.getGetTransactionsFromThis2Method = getGetTransactionsFromThis2Method =
              io.grpc.MethodDescriptor.<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated, com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(
                      "protocol.WalletExtension", "GetTransactionsFromThis2"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new WalletExtensionMethodDescriptorSupplier("GetTransactionsFromThis2"))
                  .build();
        }
      }
    }
    return getGetTransactionsFromThis2Method;
  }

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @Deprecated // Use {@link #getGetTransactionsToThisMethod()} instead.
  public static final io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> METHOD_GET_TRANSACTIONS_TO_THIS = getGetTransactionsToThisMethod();

  private static volatile io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> getGetTransactionsToThisMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> getGetTransactionsToThisMethod() {
    io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated, com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> getGetTransactionsToThisMethod;
    if ((getGetTransactionsToThisMethod = WalletExtensionGrpc.getGetTransactionsToThisMethod)
        == null) {
      synchronized (WalletExtensionGrpc.class) {
        if ((getGetTransactionsToThisMethod = WalletExtensionGrpc.getGetTransactionsToThisMethod)
            == null) {
          WalletExtensionGrpc.getGetTransactionsToThisMethod = getGetTransactionsToThisMethod =
              io.grpc.MethodDescriptor.<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated, com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(
                      "protocol.WalletExtension", "GetTransactionsToThis"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new WalletExtensionMethodDescriptorSupplier("GetTransactionsToThis"))
                  .build();
        }
      }
    }
    return getGetTransactionsToThisMethod;
  }

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @Deprecated // Use {@link #getGetTransactionsToThis2Method()} instead.
  public static final io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> METHOD_GET_TRANSACTIONS_TO_THIS2 = getGetTransactionsToThis2Method();

  private static volatile io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> getGetTransactionsToThis2Method;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> getGetTransactionsToThis2Method() {
    io.grpc.MethodDescriptor<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated, com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> getGetTransactionsToThis2Method;
    if ((getGetTransactionsToThis2Method = WalletExtensionGrpc.getGetTransactionsToThis2Method)
        == null) {
      synchronized (WalletExtensionGrpc.class) {
        if ((getGetTransactionsToThis2Method = WalletExtensionGrpc.getGetTransactionsToThis2Method)
            == null) {
          WalletExtensionGrpc.getGetTransactionsToThis2Method = getGetTransactionsToThis2Method =
              io.grpc.MethodDescriptor.<com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated, com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(
                      "protocol.WalletExtension", "GetTransactionsToThis2"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new WalletExtensionMethodDescriptorSupplier("GetTransactionsToThis2"))
                  .build();
        }
      }
    }
    return getGetTransactionsToThis2Method;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static WalletExtensionStub newStub(io.grpc.Channel channel) {
    return new WalletExtensionStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the
   * service
   */
  public static WalletExtensionBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new WalletExtensionBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static WalletExtensionFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new WalletExtensionFutureStub(channel);
  }

  /**
   */
  public static abstract class WalletExtensionImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Please use GetTransactionsFromThis2 instead of this function.
     * </pre>
     */
    public void getTransactionsFromThis(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request,
        io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTransactionsFromThisMethod(), responseObserver);
    }

    /**
     * <pre>
     * Use this function instead of GetTransactionsFromThis.
     * </pre>
     */
    public void getTransactionsFromThis2(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request,
        io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTransactionsFromThis2Method(), responseObserver);
    }

    /**
     * <pre>
     * Please use GetTransactionsToThis2 instead of this function.
     * </pre>
     */
    public void getTransactionsToThis(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request,
        io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTransactionsToThisMethod(), responseObserver);
    }

    /**
     * <pre>
     * Use this function instead of GetTransactionsToThis.
     * </pre>
     */
    public void getTransactionsToThis2(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request,
        io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTransactionsToThis2Method(), responseObserver);
    }

    @Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
              getGetTransactionsFromThisMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
                      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList>(
                      this, METHODID_GET_TRANSACTIONS_FROM_THIS)))
          .addMethod(
              getGetTransactionsFromThis2Method(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
                      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention>(
                      this, METHODID_GET_TRANSACTIONS_FROM_THIS2)))
          .addMethod(
              getGetTransactionsToThisMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
                      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList>(
                      this, METHODID_GET_TRANSACTIONS_TO_THIS)))
          .addMethod(
              getGetTransactionsToThis2Method(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated,
                      com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention>(
                      this, METHODID_GET_TRANSACTIONS_TO_THIS2)))
          .build();
    }
  }

  /**
   */
  public static final class WalletExtensionStub extends
      io.grpc.stub.AbstractStub<WalletExtensionStub> {

    private WalletExtensionStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletExtensionStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected WalletExtensionStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletExtensionStub(channel, callOptions);
    }

    /**
     * <pre>
     * Please use GetTransactionsFromThis2 instead of this function.
     * </pre>
     */
    public void getTransactionsFromThis(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request,
        io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> responseObserver) {
      ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTransactionsFromThisMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * Use this function instead of GetTransactionsFromThis.
     * </pre>
     */
    public void getTransactionsFromThis2(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request,
        io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> responseObserver) {
      ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTransactionsFromThis2Method(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * Please use GetTransactionsToThis2 instead of this function.
     * </pre>
     */
    public void getTransactionsToThis(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request,
        io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> responseObserver) {
      ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTransactionsToThisMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * Use this function instead of GetTransactionsToThis.
     * </pre>
     */
    public void getTransactionsToThis2(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request,
        io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> responseObserver) {
      ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTransactionsToThis2Method(), getCallOptions()), request,
          responseObserver);
    }
  }

  /**
   */
  public static final class WalletExtensionBlockingStub extends
      io.grpc.stub.AbstractStub<WalletExtensionBlockingStub> {

    private WalletExtensionBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletExtensionBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected WalletExtensionBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletExtensionBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Please use GetTransactionsFromThis2 instead of this function.
     * </pre>
     */
    public com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList getTransactionsFromThis(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request) {
      return blockingUnaryCall(
          getChannel(), getGetTransactionsFromThisMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Use this function instead of GetTransactionsFromThis.
     * </pre>
     */
    public com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention getTransactionsFromThis2(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request) {
      return blockingUnaryCall(
          getChannel(), getGetTransactionsFromThis2Method(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Please use GetTransactionsToThis2 instead of this function.
     * </pre>
     */
    public com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList getTransactionsToThis(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request) {
      return blockingUnaryCall(
          getChannel(), getGetTransactionsToThisMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Use this function instead of GetTransactionsToThis.
     * </pre>
     */
    public com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention getTransactionsToThis2(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request) {
      return blockingUnaryCall(
          getChannel(), getGetTransactionsToThis2Method(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class WalletExtensionFutureStub extends
      io.grpc.stub.AbstractStub<WalletExtensionFutureStub> {

    private WalletExtensionFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private WalletExtensionFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected WalletExtensionFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new WalletExtensionFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Please use GetTransactionsFromThis2 instead of this function.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> getTransactionsFromThis(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request) {
      return futureUnaryCall(
          getChannel().newCall(getGetTransactionsFromThisMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Use this function instead of GetTransactionsFromThis.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> getTransactionsFromThis2(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request) {
      return futureUnaryCall(
          getChannel().newCall(getGetTransactionsFromThis2Method(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Please use GetTransactionsToThis2 instead of this function.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList> getTransactionsToThis(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request) {
      return futureUnaryCall(
          getChannel().newCall(getGetTransactionsToThisMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Use this function instead of GetTransactionsToThis.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention> getTransactionsToThis2(
        com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated request) {
      return futureUnaryCall(
          getChannel().newCall(getGetTransactionsToThis2Method(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_TRANSACTIONS_FROM_THIS = 0;
  private static final int METHODID_GET_TRANSACTIONS_FROM_THIS2 = 1;
  private static final int METHODID_GET_TRANSACTIONS_TO_THIS = 2;
  private static final int METHODID_GET_TRANSACTIONS_TO_THIS2 = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

    private final WalletExtensionImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(WalletExtensionImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_TRANSACTIONS_FROM_THIS:
          serviceImpl.getTransactionsFromThis(
              (com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated) request,
              (io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList>) responseObserver);
          break;
        case METHODID_GET_TRANSACTIONS_FROM_THIS2:
          serviceImpl.getTransactionsFromThis2(
              (com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated) request,
              (io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention>) responseObserver);
          break;
        case METHODID_GET_TRANSACTIONS_TO_THIS:
          serviceImpl.getTransactionsToThis(
              (com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated) request,
              (io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionList>) responseObserver);
          break;
        case METHODID_GET_TRANSACTIONS_TO_THIS2:
          serviceImpl.getTransactionsToThis2(
              (com.liang.sangong.trx.tron.api.GrpcAPI.AccountPaginated) request,
              (io.grpc.stub.StreamObserver<com.liang.sangong.trx.tron.api.GrpcAPI.TransactionListExtention>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class WalletExtensionBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {

    WalletExtensionBaseDescriptorSupplier() {
    }

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.liang.sangong.trx.tron.api.GrpcAPI.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("WalletExtension");
    }
  }

  private static final class WalletExtensionFileDescriptorSupplier
      extends WalletExtensionBaseDescriptorSupplier {

    WalletExtensionFileDescriptorSupplier() {
    }
  }

  private static final class WalletExtensionMethodDescriptorSupplier
      extends WalletExtensionBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {

    private final String methodName;

    WalletExtensionMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (WalletExtensionGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new WalletExtensionFileDescriptorSupplier())
              .addMethod(getGetTransactionsFromThisMethod())
              .addMethod(getGetTransactionsFromThis2Method())
              .addMethod(getGetTransactionsToThisMethod())
              .addMethod(getGetTransactionsToThis2Method())
              .build();
        }
      }
    }
    return result;
  }
}
