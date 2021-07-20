package akkamon.domain;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;

public class AkkamonNexusTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void given_a_registration_request_when_no_scene_or_trainer_exists_in_the_system_then_create_scene_and_trainer_and_reply() {
        TestProbe<AkkamonNexus.TrainerRegistered> probe =
                testKit.createTestProbe(AkkamonNexus.TrainerRegistered.class);

        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroupActor =
                testKit.spawn(SceneTrainerGroup.create("start"));

        sceneTrainerGroupActor.tell(new AkkamonNexus.RequestTrainerRegistration("ash", "start", probe.getRef()));

        probe.expectMessageClass(AkkamonNexus.TrainerRegistered.class);

    }

}