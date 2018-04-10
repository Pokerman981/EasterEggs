package me.pokerman99.EasterEggs.data;

import com.google.common.reflect.TypeToken;
import java.util.List;;
import javax.annotation.Generated;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.ListValue;

@Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2018-04-08T19:36:57.755Z")
public class Keys {

    public Keys() {}

    public final static Key<ListValue<String>> EGGDATA;
    static {
        TypeToken<List<String>> listStringToken = new TypeToken<List<String>>(){};
        TypeToken<ListValue<String>> listValueStringToken = new TypeToken<ListValue<String>>(){};
        EGGDATA = KeyFactory.makeListKey(listStringToken, listValueStringToken, DataQuery.of("Eggdata"), "eastereggs:eggdata", "Eggdata");
    }
}
