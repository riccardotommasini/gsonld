package it.polimi.gsonld;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.gsonld.annotations.JsonLD;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by riccardo on 24/08/2017.
 */
public class JsonLDSerializerFactory {

    private static Map<Class<?>, JsonLDSerializer<?>> registered;

    static {
        registered = new HashMap<Class<?>, JsonLDSerializer<?>>();
    }

    public static void init() {
        Reflections reflections = new Reflections("it.polimi.jsonld");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(JsonLD.class);
        annotated.forEach(c -> register(c));
    }

    public static void register(Class<?> c) {
        registered.put(c, JsonLDSerializer.get(c));

    }
    public static void register(String package_name) {
        Reflections reflections = new Reflections(package_name);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(JsonLD.class);
        annotated.forEach(c -> register(c));
    }


    public static Gson getGson() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        registered.entrySet().forEach(e -> gsonBuilder.registerTypeHierarchyAdapter(e.getKey(), e.getValue()));
        return gsonBuilder.create();

    }

}
