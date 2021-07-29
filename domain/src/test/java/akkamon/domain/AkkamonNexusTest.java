package akkamon.domain;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.actors.SceneTrainerGroup;
import org.junit.ClassRule;
import org.junit.Test;

public class AkkamonNexusTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void given_a_registration_request_when_no_scene_or_trainer_exists_in_the_system_then_create_scene_and_trainer_and_reply() {
        TestProbe<AkkamonNexus.Command> probe =
                testKit.createTestProbe(AkkamonNexus.Command.class);

        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroupActor =
                testKit.spawn(SceneTrainerGroup.create("start"));

        // TODO use mockito to mock AkkamonSessions

        // sceneTrainerGroupActor.tell(new AkkamonNexus.RequestTrainerRegistration(
        //         "ash",
        //         "start",
        //         probe.getRef()
        // ));

        // probe.expectMessageClass(AkkamonNexus.TrainerRegistered.class);

    }

}