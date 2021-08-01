package akkamon.domain.model.battle;

import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.model.battle.events.BattleEvent;
import akkamon.domain.model.battle.events.Introduction;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {

    @Test
    void the_engine_is_constructed_with_two_trainer_ids() {
        AkkamonNexus.TrainerID trainerID = new AkkamonNexus.TrainerID(
                "Ash",
                "Pallet Town"
        );

        AkkamonNexus.TrainerID trainerID1 = new AkkamonNexus.TrainerID(
                "Brock",
                "Pewter City"
        );

        Set<AkkamonNexus.TrainerID> participants = new HashSet();
        participants.add(trainerID);
        participants.add(trainerID1);

        BattleEngine engine = new BattleEngine(participants);
        Map<String, MonTeam> teams = engine.getState().teams;

        Set<String> participantids = new HashSet<>();

        for (AkkamonNexus.TrainerID id : participants) {
            participantids.add(id.id);
        }

        assertEquals(participantids, teams.keySet());
    }


    BattleEngine engine;
    Gson gson = new Gson();

    @BeforeEach
    void setUP() {
        AkkamonNexus.TrainerID trainerID = new AkkamonNexus.TrainerID(
                "Ash",
                "Pallet Town"
        );

        AkkamonNexus.TrainerID trainerID1 = new AkkamonNexus.TrainerID(
                "Brock",
                "Pewter City"
        );

        Set<AkkamonNexus.TrainerID> participants = new HashSet();
        participants.add(trainerID);
        participants.add(trainerID1);

        engine = new BattleEngine(participants);

    }

    @Test
    void given_the_battle_is_not_started_when_request_start_battle_is_called_then_return_initial_state_and_events_to_start_battle_in_the_client() {
        Map<AkkamonNexus.TrainerID, BattleMessage> message = engine.initialise();
        for (AkkamonNexus.TrainerID trainerID : message.keySet()) {
            System.out.println(gson.toJson(message.get(trainerID)));
        }
    }

}