package akkamon.domain.model.akkamon.moves;

import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.akkamon.Stat;
import akkamon.domain.model.akkamon.moves.implementations.*;
import akkamon.domain.model.akkamon.types.implementations.*;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class MovesFactory {
    public Map<MoveSlot, Mon.Move> fromNames(String[] names) {
        Map<MoveSlot, Mon.Move> moves = new HashMap<>();
        for (var i = 0 ; i < names.length; i++) {
            String name = names[i];
            MoveSlot slot = indexToMoveSlot(i);
            moves.put(slot, fromName(name));
        }
        return moves;
    }

    private Mon.Move fromName(String name) {
        Mon.Move move;
        switch (name) {
            case "Body Slam":
                move = new BodySlam(
                        name,
                        new NormalType(),
                        MoveCategory.PHYSICAL,
                        new Stat(15),
                        85,
                        1
                );
                break;
            case "Reflect":
                move = new Reflect(
                        name,
                        new PsychicType(),
                        MoveCategory.STATUS,
                        new Stat(20),
                        -1,
                        -1
                );
                break;
            case "Rest":
                move = new Rest(
                        name,
                        new PsychicType(),
                        MoveCategory.STATUS,
                        new Stat(10),
                        -1,
                        -1
                );
                break;
            case "Ice Beam":
                move = new IceBeam(
                        name,
                        new IceType(),
                        MoveCategory.SPECIAL,
                        new Stat(10),
                        90,
                        1
                );
                break;
            case "Swords Dance":
                move = new SwordsDance(
                        name,
                        new NormalType(),
                        MoveCategory.STATUS,
                        new Stat(20),
                        90,
                        1
                );
                break;
            case "Earthquake":
                move = new Earthquake(
                        name,
                        new GroundType(),
                        MoveCategory.PHYSICAL,
                        new Stat(10),
                        100,
                        1
                );
                break;
            case "Soft-Boiled":
                move = new SoftBoiled(
                        name,
                        new NormalType(),
                        MoveCategory.STATUS,
                        new Stat(10),
                        -1,
                        -1
                );
                break;
            case "Amnesia":
                move = new Amnesia(
                        name,
                        new PsychicType(),
                        MoveCategory.STATUS,
                        new Stat(20),
                        -1,
                        -1
                );
                break;
            case "Psychic":
                move = new Psychic(
                        name,
                        new PsychicType(),
                        MoveCategory.SPECIAL,
                        new Stat(10),
                        90,
                        1
                );
                break;
            case "Agility":
                move = new Agility(
                        name,
                        new PsychicType(),
                        MoveCategory.STATUS,
                        new Stat(30),
                        -1,
                        -1
                );
                break;
            case "Blizzard":
                move = new Blizzard(
                        name,
                        new IceType(),
                        MoveCategory.SPECIAL,
                        new Stat(5),
                        110,
                        0.7
                );
                break;
            case "Thunder Wave":
                move = new ThunderWave(
                        name,
                        new ElectricType(),
                        MoveCategory.STATUS,
                        new Stat(20),
                        -1,
                        0.9
                );
                break;
            case "Wrap":
                move = new Wrap(
                        name,
                        new NormalType(),
                        MoveCategory.PHYSICAL,
                        new Stat(20),
                        15,
                        0.9
                );
                break;
            default:
                System.out.println("move not found: " + name);
                return null;
        }
        return move;
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

    public Mon.Move fromJSON(JsonObject json) {
        String name = String.valueOf(json.get("name"));
        name = name.replaceAll("\"", "");
        System.out.println("Setting this move from json!");
        System.out.println(name);
        Mon.Move move = this.fromName(name);
        JsonObject pp = json.get("PP").getAsJsonObject();
        move.PP.effective = Integer.parseInt(String.valueOf(pp.get("effective")));
        System.out.println(move);
        return move;
    }
}
