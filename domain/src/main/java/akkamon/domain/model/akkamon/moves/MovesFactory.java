package akkamon.domain.model.akkamon.moves;

import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.akkamon.Stat;
import akkamon.domain.model.akkamon.moves.implementations.*;
import akkamon.domain.model.akkamon.types.implementations.*;

import java.util.HashMap;
import java.util.Map;

public class MovesFactory {
    public Map<MoveSlot, Mon.Move> fromNames(String[] names) {
        Map<MoveSlot, Mon.Move> moves = new HashMap<>();
        for (var i = 0 ; i < names.length; i++) {
            String name = names[i];
            MoveSlot slot = indexToMoveSlot(i);
            switch (name) {
                case "Body Slam":
                    Mon.Move bodySlam = new BodySlam(
                            name,
                            new NormalType(),
                            MoveCategory.PHYSICAL,
                            new Stat(15),
                            85,
                            1
                    );
                    moves.put(slot, bodySlam);
                    break;
                case "Reflect":
                    Mon.Move reflect = new Reflect(
                            name,
                            new PsychicType(),
                            MoveCategory.STATUS,
                            new Stat(20),
                            -1,
                            -1
                    );
                    moves.put(slot, reflect);
                    break;
                case "Rest":
                    Mon.Move rest = new Rest(
                            name,
                            new PsychicType(),
                            MoveCategory.STATUS,
                            new Stat(10),
                            -1,
                            -1
                    );
                    moves.put(slot, rest);
                    break;
                case "Ice Beam":
                    Mon.Move iceBeam = new IceBeam(
                            name,
                            new IceType(),
                            MoveCategory.SPECIAL,
                            new Stat(10),
                            90,
                            1
                    );
                    moves.put(slot, iceBeam);
                    break;
                case "Swords Dance":
                    Mon.Move swordsDance = new SwordsDance(
                            name,
                            new NormalType(),
                            MoveCategory.STATUS,
                            new Stat(20),
                            90,
                            1
                    );
                    moves.put(slot, swordsDance);
                    break;
                case "Earthquake":
                    Mon.Move earthquake = new Earthquake(
                            name,
                            new GroundType(),
                            MoveCategory.PHYSICAL,
                            new Stat(10),
                            100,
                            1
                    );
                    moves.put(slot, earthquake);
                    break;
                case "Soft-Boiled":
                    Mon.Move softBoiled = new SoftBoiled(
                            name,
                            new NormalType(),
                            MoveCategory.STATUS,
                            new Stat(10),
                            -1,
                            -1
                    );
                    moves.put(slot, softBoiled);
                    break;
                case "Amnesia":
                    Mon.Move amnesia = new Amnesia(
                            name,
                            new PsychicType(),
                            MoveCategory.STATUS,
                            new Stat(20),
                            -1,
                            -1
                    );
                    moves.put(slot, amnesia);
                    break;
                case "Psychic":
                    Mon.Move psychic = new Psychic(
                            name,
                            new PsychicType(),
                            MoveCategory.SPECIAL,
                            new Stat(10),
                            90,
                            1
                    );
                    moves.put(slot, psychic);
                    break;
                case "Agility":
                    Mon.Move agility = new Agility(
                            name,
                            new PsychicType(),
                            MoveCategory.STATUS,
                            new Stat(30),
                            -1,
                            -1
                    );
                    moves.put(slot, agility);
                    break;
                case "Blizzard":
                    Mon.Move blizzard = new Blizzard(
                            name,
                            new IceType(),
                            MoveCategory.SPECIAL,
                            new Stat(5),
                            110,
                            0.7
                    );
                    moves.put(slot, blizzard);
                    break;
                case "Thunder Wave":
                    Mon.Move thunderWave = new ThunderWave(
                            name,
                            new ElectricType(),
                            MoveCategory.STATUS,
                            new Stat(20),
                            -1,
                            0.9
                    );
                    moves.put(slot, thunderWave);
                    break;
                case "Wrap":
                    Mon.Move wrap = new Wrap(
                            name,
                            new NormalType(),
                            MoveCategory.PHYSICAL,
                            new Stat(20),
                            15,
                            0.9
                    );
                    moves.put(slot, wrap);
                    break;
                default:
                    System.out.println("move not found: " + name);
                    return null;
            }
        }
        return moves;
    }

    private MoveSlot indexToMoveSlot(int i) {
        switch (i) {
            case 0:
                return MoveSlot.FIRST;
            case 1:
                return MoveSlot.SECOND;
            case 2:
                return MoveSlot.THIRD;
            case 3:
                return MoveSlot.FOURTH;
            default:
                return null;
        }
    }
}
