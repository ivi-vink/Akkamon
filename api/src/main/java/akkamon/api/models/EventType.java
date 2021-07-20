package akkamon.api.models;

import com.google.gson.annotations.SerializedName;

public enum EventType {
    @SerializedName("PlayerRegistrationEvent")
    TRAINER_REGISTRATION,

    @SerializedName("HeartBeat")
    HEART_BEAT
}
