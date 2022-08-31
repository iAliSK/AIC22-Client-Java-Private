package ir.sharif.aic.hideandseek.protobuf;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: AIProto.proto")
public final class GameHandlerGrpc {

  private GameHandlerGrpc() {}

  public static final String SERVICE_NAME = "ir.sharif.aic.hideandseek.protobuf.GameHandler";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand,
      com.google.protobuf.Empty> METHOD_DECLARE_READINESS =
      io.grpc.MethodDescriptor.<ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand, com.google.protobuf.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "ir.sharif.aic.hideandseek.protobuf.GameHandler", "DeclareReadiness"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand,
      ir.sharif.aic.hideandseek.protobuf.AIProto.GameView> METHOD_WATCH =
      io.grpc.MethodDescriptor.<ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand, ir.sharif.aic.hideandseek.protobuf.AIProto.GameView>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "ir.sharif.aic.hideandseek.protobuf.GameHandler", "Watch"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ir.sharif.aic.hideandseek.protobuf.AIProto.GameView.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand,
      com.google.protobuf.Empty> METHOD_MOVE =
      io.grpc.MethodDescriptor.<ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand, com.google.protobuf.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "ir.sharif.aic.hideandseek.protobuf.GameHandler", "Move"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand,
      com.google.protobuf.Empty> METHOD_SEND_MESSAGE =
      io.grpc.MethodDescriptor.<ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand, com.google.protobuf.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "ir.sharif.aic.hideandseek.protobuf.GameHandler", "SendMessage"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GameHandlerStub newStub(io.grpc.Channel channel) {
    return new GameHandlerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GameHandlerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GameHandlerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GameHandlerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GameHandlerFutureStub(channel);
  }

  /**
   */
  public static abstract class GameHandlerImplBase implements io.grpc.BindableService {

    /**
     */
    public void declareReadiness(ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_DECLARE_READINESS, responseObserver);
    }

    /**
     */
    public void watch(ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand request,
        io.grpc.stub.StreamObserver<ir.sharif.aic.hideandseek.protobuf.AIProto.GameView> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_WATCH, responseObserver);
    }

    /**
     */
    public void move(ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_MOVE, responseObserver);
    }

    /**
     */
    public void sendMessage(ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SEND_MESSAGE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_DECLARE_READINESS,
            asyncUnaryCall(
              new MethodHandlers<
                ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand,
                com.google.protobuf.Empty>(
                  this, METHODID_DECLARE_READINESS)))
          .addMethod(
            METHOD_WATCH,
            asyncServerStreamingCall(
              new MethodHandlers<
                ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand,
                ir.sharif.aic.hideandseek.protobuf.AIProto.GameView>(
                  this, METHODID_WATCH)))
          .addMethod(
            METHOD_MOVE,
            asyncUnaryCall(
              new MethodHandlers<
                ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand,
                com.google.protobuf.Empty>(
                  this, METHODID_MOVE)))
          .addMethod(
            METHOD_SEND_MESSAGE,
            asyncUnaryCall(
              new MethodHandlers<
                ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand,
                com.google.protobuf.Empty>(
                  this, METHODID_SEND_MESSAGE)))
          .build();
    }
  }

  /**
   */
  public static final class GameHandlerStub extends io.grpc.stub.AbstractStub<GameHandlerStub> {
    private GameHandlerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GameHandlerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameHandlerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GameHandlerStub(channel, callOptions);
    }

    /**
     */
    public void declareReadiness(ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DECLARE_READINESS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void watch(ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand request,
        io.grpc.stub.StreamObserver<ir.sharif.aic.hideandseek.protobuf.AIProto.GameView> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_WATCH, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void move(ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_MOVE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sendMessage(ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEND_MESSAGE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GameHandlerBlockingStub extends io.grpc.stub.AbstractStub<GameHandlerBlockingStub> {
    private GameHandlerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GameHandlerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameHandlerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GameHandlerBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty declareReadiness(ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand request) {
      return blockingUnaryCall(
          getChannel(), METHOD_DECLARE_READINESS, getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<ir.sharif.aic.hideandseek.protobuf.AIProto.GameView> watch(
        ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_WATCH, getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty move(ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand request) {
      return blockingUnaryCall(
          getChannel(), METHOD_MOVE, getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty sendMessage(ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SEND_MESSAGE, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GameHandlerFutureStub extends io.grpc.stub.AbstractStub<GameHandlerFutureStub> {
    private GameHandlerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GameHandlerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameHandlerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GameHandlerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> declareReadiness(
        ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DECLARE_READINESS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> move(
        ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_MOVE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> sendMessage(
        ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEND_MESSAGE, getCallOptions()), request);
    }
  }

  private static final int METHODID_DECLARE_READINESS = 0;
  private static final int METHODID_WATCH = 1;
  private static final int METHODID_MOVE = 2;
  private static final int METHODID_SEND_MESSAGE = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GameHandlerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GameHandlerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DECLARE_READINESS:
          serviceImpl.declareReadiness((ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_WATCH:
          serviceImpl.watch((ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand) request,
              (io.grpc.stub.StreamObserver<ir.sharif.aic.hideandseek.protobuf.AIProto.GameView>) responseObserver);
          break;
        case METHODID_MOVE:
          serviceImpl.move((ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_SEND_MESSAGE:
          serviceImpl.sendMessage((ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class GameHandlerDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ir.sharif.aic.hideandseek.protobuf.AIProto.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GameHandlerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GameHandlerDescriptorSupplier())
              .addMethod(METHOD_DECLARE_READINESS)
              .addMethod(METHOD_WATCH)
              .addMethod(METHOD_MOVE)
              .addMethod(METHOD_SEND_MESSAGE)
              .build();
        }
      }
    }
    return result;
  }
}
