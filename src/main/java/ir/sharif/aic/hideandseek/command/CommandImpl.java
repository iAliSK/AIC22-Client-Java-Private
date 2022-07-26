package ir.sharif.aic.hideandseek.command;


import ir.sharif.aic.hideandseek.protobuf.AIProto.ChatCommand;
import ir.sharif.aic.hideandseek.protobuf.AIProto.DeclareReadinessCommand;
import ir.sharif.aic.hideandseek.protobuf.AIProto.MoveCommand;
import ir.sharif.aic.hideandseek.protobuf.AIProto.WatchCommand;

public class CommandImpl {

    private final String token;

    public CommandImpl(String token) {
        this.token = token;
    }

    public WatchCommand createWatchCommand() {
        return WatchCommand.newBuilder()
                .setToken(token)
                .build();
    }

    public DeclareReadinessCommand declareReadinessCommand(int startingNodeId) {
        return DeclareReadinessCommand.newBuilder()
                .setStartNodeId(startingNodeId)
                .setToken(token)
                .build();
    }

    public MoveCommand createMoveCommand(int nodeId)  {
        return MoveCommand.newBuilder()
                .setToken(token)
                .setToNodeId(nodeId)
                .build();
    }

    public ChatCommand createChatCommand(String message) {
        return ChatCommand.newBuilder()
                .setToken(token)
                .setText(message)
                .build();
    }

}
