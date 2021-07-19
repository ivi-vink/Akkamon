package akkamon.domain.iot;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akkamon.domain.iot.supervision.MainSupervize;

public class ActorHierarchyExperiments {
  public static void main(String[] args) {
    // ActorRef<String> testSystem = ActorSystem.create(Main.create(), "testSystem");
    // testSystem.tell("start");
    // ActorRef<String> testSystemStartStop = ActorSystem.create(MainStartStop.create(), "test-supervize");
    // testSystemStartStop.tell("start");
    ActorRef<String> testSupervize = ActorSystem.create(MainSupervize.create(), "test-supervize");
    testSupervize.tell("start");
  }
}
