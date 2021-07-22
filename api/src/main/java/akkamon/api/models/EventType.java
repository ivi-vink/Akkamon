package akkamon.api.models;

import com.google.gson.annotations.SerializedName;

public enum EventType {
    @SerializedName("PlayerRegistrationEvent")
    TRAINER_REGISTRATION,

    @SerializedName("HeartBeat")
    HEART_BEAT,

    @SerializedName("StartMoving")
    START_MOVING,

    @SerializedName("NewTilePos")
    NEW_TILE_POS,

    @SerializedName("StopMoving")
    STOP_MOVING,

    @SerializedName("SocketClosed")
    SOCKET_CLOSED,

}
