package akkamon.domain.model.battle.requests;

import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.akkamon.Stat;
import akkamon.domain.model.akkamon.moves.MoveCategory;
import akkamon.domain.model.akkamon.moves.MovesFactory;
import com.google.gson.*;

import java.lang.reflect.Type;

public class JsonToMove implements JsonDeserializer<Mon.Move> {

    @Override
    public Mon.Move deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        return new MovesFactory().fromJSON(json.getAsJsonObject());
    }
}
