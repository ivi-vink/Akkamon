package akkamon.domain.iot;

import akka.actor.typed.ActorSystem;

public class IotEntryPoint {
    public static void main(String[] args) {
        ActorSystem.create(IotSupervisor.create(), "iot-system");
    }
}
