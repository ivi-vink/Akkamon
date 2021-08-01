package akkamon.domain.model.battle;

import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.akkamon.MonStats;
import akkamon.domain.model.akkamon.abilities.AkkamonAbilities;
import akkamon.domain.model.akkamon.status.AkkamonStatus;
import akkamon.domain.model.akkamon.types.AkkamonTypes;
import akkamon.domain.model.battle.events.Introduction;
import akkamon.domain.model.battle.state.BattleState;

import java.util.*;

public class BattleEngine {

    private final Map<AkkamonNexus.TrainerID, List<AkkamonNexus.TrainerID>> trainerIDToOpponents = new HashMap<>();
    private final BattleEvents events;
    private final BattleState state;

    public BattleEngine(Set<AkkamonNexus.TrainerID> participants) {

        Random random = new Random();
        List<MonTeam> demoTeams = createDemoTeams();
        Map<AkkamonNexus.TrainerID, MonTeam> trainerIDtoTeam = new HashMap<>();

        for (AkkamonNexus.TrainerID trainerID : participants) {
            trainerIDtoTeam.put(trainerID, demoTeams.remove(random.nextInt(demoTeams.size())));

            List<AkkamonNexus.TrainerID> opponents = new ArrayList<>(participants);

            opponents.remove(trainerID);
            trainerIDToOpponents.put(trainerID, opponents);
        }

        this.events = new BattleEvents(participants);
        this.state = new BattleState(trainerIDtoTeam);

    }

    public Map<AkkamonNexus.TrainerID, List<AkkamonNexus.TrainerID>> getTrainerIDToOpponents() {
        return trainerIDToOpponents;
    }

    public BattleEvents getEvents() {
        return events;
    }

    public BattleState getState() {
        return state;
    }

    public Map<AkkamonNexus.TrainerID, BattleMessage> initialise() {
        for (AkkamonNexus.TrainerID trainerID : trainerIDToOpponents.keySet()) {
            this.events.trainerIDEventMap.get(trainerID).add(
                    new Introduction(
                    )
            );
        }
        return bundleEventsAndState();
    }

    private Map<AkkamonNexus.TrainerID, BattleMessage> bundleEventsAndState() {
        Map<AkkamonNexus.TrainerID, BattleMessage> messageMap = new HashMap<>();

        for (AkkamonNexus.TrainerID trainerID : trainerIDToOpponents.keySet()) {
            messageMap.put(trainerID, new BattleMessage(
                    trainerID,
                    events,
                    state
            ));
        }
        return messageMap;
    }

    public static List<MonTeam> createDemoTeams() {
        int[] baseSnorlax = {160, 110, 65, 65, 110, 30};
        int[] evs = {252, 4, 0, 0, 252, 0};
        int[] ivs = {31,31,31,31,31,31};
        double[] natureMultiplier = {1, 1, 1, 1, 1.1, 0.9};
        int level = 100;
        MonStats stats = new MonStats(
                baseSnorlax,
                evs,
                ivs,
                natureMultiplier,
                level
        );
        Mon snorlax = new Mon(
                "Snorlax",
                stats,
                AkkamonTypes.NORMAL,
                AkkamonAbilities.IMMUNITY,
                AkkamonStatus.NONE,
                new String[] {
                        "Body Slam",
                        "Reflect",
                        "Rest",
                        "Ice Beam"
                }
        );

        int[] baseMew = {100, 100, 100, 100, 100, 100};
        int[] evsMew = {252, 252, 0, 0, 4, 0};
        int[] ivsMew = {31,31,31,31,31,31};
        double[] natureMultiplierMew = {1, 1.1, 1, 0.9, 1, 1};
        int levelMew = 100;
        MonStats statsMew = new MonStats(
                baseMew,
                evsMew,
                ivsMew,
                natureMultiplierMew,
                levelMew
        );
        Mon mew = new Mon(
                "Mew",
                statsMew,
                AkkamonTypes.PSYCHIC,
                AkkamonAbilities.SYNCHRONIZE,
                AkkamonStatus.NONE,
                new String[] {
                        "Swords Dance",
                        "Earthquake",
                        "Body Slam",
                        "Soft-Boiled"
                }
        );

        List<Mon> team1list = new ArrayList<>();
        team1list.add(mew);
        team1list.add(snorlax);

        int[] baseMew2 = {106, 110, 90, 154, 90, 130};
        int[] evsMew2 = {0, 0, 0, 252, 4, 252};
        int[] ivsMew2 = {31,31,31,31,31,31};
        double[] natureMultiplierMew2 = {1, 0.9, 1, 1.1, 1, 1};
        int levelMew2 = 100;
        MonStats statsMew2 = new MonStats(
                baseMew2,
                evsMew2,
                ivsMew2,
                natureMultiplierMew2,
                levelMew2
        );
        Mon mew2 = new Mon(
                "Mewtwo",
                statsMew2,
                AkkamonTypes.PSYCHIC,
                AkkamonAbilities.PRESSURE,
                AkkamonStatus.NONE,
                new String[] {
                        "Amnesia",
                        "Psychic",
                        "Ice Beam",
                        "Agility"
                }
        );

        int[] baseDragonite = {91, 134, 95, 100, 100, 80};
        int[] evsDragonite = {0, 0, 0, 252, 4, 252};
        int[] ivsDragonite = {31,31,31,31,31,31};
        double[] natureMultiplierDragonite = {1, 0.9, 1, 1.1, 1, 1};
        int levelDragonite = 100;
        MonStats statsDragonite = new MonStats(
                baseDragonite,
                evsDragonite,
                ivsDragonite,
                natureMultiplierDragonite,
                levelDragonite
        );

        Mon dragonite = new Mon(
                "Dragonite",
                statsDragonite,
                AkkamonTypes.DRAGON,
                AkkamonAbilities.INNER_FOCUS,
                AkkamonStatus.NONE,
                new String[] {
                        "Blizzard",
                        "Thunder Wave",
                        "Wrap",
                        "Agility"
                }
        );

        List<Mon> team2list = new ArrayList<>();
        team2list.add(mew2);
        team2list.add(dragonite);

        List<MonTeam> demoTeams = new ArrayList<>();
        demoTeams.add(new MonTeam(team1list));
        demoTeams.add(new MonTeam(team2list));

        return demoTeams;
    }

}
