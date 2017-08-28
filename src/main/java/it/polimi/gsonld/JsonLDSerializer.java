package it.polimi.gsonld;

import com.google.gson.*;
import it.polimi.gsonld.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;


import static it.polimi.gsonld.JsonldUtils.*;

/**
 * Created by riccardo on 24/08/2017.
 */
public class JsonLDSerializer<T> implements JsonSerializer<T> {

    private HashMap<String, String> prefixes;
    private HashMap<String, String> inverse_prefix;
    private HashMap<String, String> aliases;
    private HashMap<String, String> inverse_aliases;


    private boolean expand_prefixes = false, autogen_prefixes = false;

    public JsonLDSerializer() {
        this.prefixes = new HashMap<>();
        this.inverse_prefix = new HashMap<>();
        this.aliases = new HashMap<>();
        this.inverse_aliases = new HashMap<>();

    }

    public static JsonLDSerializer<?> get(Class<?> c) {
        return new JsonLDSerializer<>();
    }

    public JsonElement serialize(T t, java.lang.reflect.Type typeOfSrc, JsonSerializationContext jsonc) {
        JsonObject o = new JsonObject();
        JsonObject c = new JsonObject();
        o.add(JSONLD_CONTEXT, c);

        return getJsonElement(t, t.getClass(), o, c);

    }

    private JsonElement getJsonElement(Object t, Class<?> aClass, JsonObject o, JsonObject context) {

        HashMap<String, List<Field>> properties = new HashMap<>();

        String type = new String();
        String id = new String();

        if (aClass.isAnnotationPresent(JsonLD.class)) {
            expand_prefixes = aClass.getAnnotation(JsonLD.class).expand_prefixes();
            autogen_prefixes = aClass.getAnnotation(JsonLD.class).autogen_prefixes();
        }

        extractPrefixes(aClass, context);
        extractAliases(aClass, context);


        if (aClass.isAnnotationPresent(Type.class)) {
            type = aClass.getAnnotation(Type.class).value();

            checkPrefix(type);

            if (expand_prefixes) {
                type = expand(type);
            }

        } else {
            type = aClass.getCanonicalName();
        }


        o.addProperty(JSONLD_TYPE, type);

        Field[] fields = aClass.getFields();

        List<Method> getters = Arrays.stream(aClass.getMethods()).filter(this::isGetter).collect(Collectors.toList());

        Arrays.stream(fields).filter(f -> f.isAnnotationPresent(Property.class)).forEach(f -> {
            Property p = f.getAnnotation(Property.class);
            if (properties.containsKey(p.value())) {
                properties.get(p.value()).add(f);
            } else {
                List<Field> l = new ArrayList<Field>();
                l.add(f);
                properties.put(p.value(), l);
            }
        });


        boolean foundId = false;

        for (Field f : fields) {

            if (!foundId && f.isAnnotationPresent(Id.class)) {
                id = f.getAnnotation(Id.class).value();
                foundId = true;
            }

            o.addProperty(JSONLD_ID, id);

            String field_uri = "";

            if ((f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(Property.class)) && f.isAnnotationPresent(Type.class)) {
                JsonObject p = new JsonObject();
                field_uri = (f.isAnnotationPresent(Id.class) ? f.getAnnotation(Id.class).value() : f.getAnnotation(Property.class).value()) + "/";
                p.addProperty(JSONLD_ID, field_uri);
                String value = f.getAnnotation(Type.class).value();

                checkPrefix(value);

                if (expand_prefixes)
                    value = expand(value);

                p.addProperty(JSONLD_TYPE, value);
                context.add(f.getName(), p);


            } else if (f.isAnnotationPresent(Property.class)) {
                field_uri = f.getAnnotation(Property.class).value();

                checkPrefix(field_uri);

                if (expand_prefixes)
                    field_uri = expand(field_uri);

                if (properties.containsKey(field_uri) && properties.get(field_uri).size() < 2) {
                    context.addProperty(f.getName(), field_uri);
                }

            } else if (!f.isAnnotationPresent(Property.class) && aClass.isAnnotationPresent(BaseVocab.class)) {
                context.addProperty(f.getName(), aClass.getAnnotation(BaseVocab.class).value() + f.getName());
            } else if (!f.isAnnotationPresent(Property.class) && f.isAnnotationPresent(Type.class) && f.isAnnotationPresent(NameSpace.class)) {
                JsonObject p = new JsonObject();
                p.addProperty(JSONLD_ID, field_uri = f.getAnnotation(NameSpace.class).value() + f.getName());
                p.addProperty(JSONLD_TYPE, f.getAnnotation(Type.class).value());
                context.add(f.getName(), p);
                getFieldValue(t, o, f, context);
            } else if (!f.isAnnotationPresent(Property.class) && f.isAnnotationPresent(NameSpace.class)) {
                context.addProperty(f.getName(), field_uri = f.getAnnotation(NameSpace.class).value() + f.getName());
            }

        }

        properties.forEach((uri, list) -> {

            if (list.size() < 2) {
                getFieldValue(t, o, list.get(0), context);
            } else {
                getFieldsValue(t, o, list, uri, context);
            }

        });


        return o;
    }

    private void extractPrefixes(Class<?> tclass, JsonObject context) {
        boolean has_prefix = tclass.isAnnotationPresent(Prefix.class);

        if (has_prefix) {
            Prefix p = tclass.getAnnotation(Prefix.class);
            String prefix = p.prefix();
            String uri = p.uri();
            this.prefixes.put(prefix, uri);
            this.inverse_prefix.put(uri, prefix);
            this.inverse_prefix.put(uri, prefix);
            context.addProperty(prefix, uri);

        }
        boolean has_prefixes = tclass.isAnnotationPresent(Prefixes.class);
        if (has_prefixes) {
            Prefix[] prefixes = tclass.getAnnotationsByType(Prefix.class);
            Arrays.stream(prefixes).forEach(p -> {
                String prefix = p.prefix();
                String uri = p.uri();
                this.prefixes.put(prefix, uri);
                this.inverse_prefix.put(uri, prefix);
                context.addProperty(prefix, uri);
            });
        }

        if ((has_prefix || has_prefixes) && !tclass.getSuperclass().equals(Object.class)) {
            extractPrefixes(tclass.getSuperclass(), context);
        }
    }


    private void extractAliases(Class<?> tclass, JsonObject context) {
        boolean has_alias = tclass.isAnnotationPresent(Alias.class);
        boolean has_aliases = tclass.isAnnotationPresent(Aliases.class);

        if (has_alias) {
            Alias p = tclass.getAnnotation(Alias.class);
            addAlias(context, p);
        }

        if (has_aliases) {
            Alias[] prefixes = tclass.getAnnotationsByType(Alias.class);
            Arrays.stream(prefixes).forEach(p -> {
                addAlias(context, p);
            });

        }

        if ((has_alias || has_aliases) && !tclass.getSuperclass().equals(Object.class)) {
            extractAliases(tclass.getSuperclass(), context);
        }
    }

    private void addAlias(JsonObject context, Alias p) {
        String alias = p.alias();
        String val = p.value();
        this.prefixes.put(alias, val);
        this.inverse_prefix.put(val, alias);

        if ("[Unassigned]".equals(p.type())) {
            context.addProperty(alias, val);
        } else {
            JsonObject ao = new JsonObject();
            ao.addProperty(JSONLD_ID, val);
            ao.addProperty(JSONLD_TYPE, p.type());
            context.add(alias, ao);
        }
    }

    private String expand(String type) {
        String[] prefix_val = type.split(":");
        return prefixes.get(prefix_val[0]) + prefix_val[1];
    }

    private void checkPrefix(String value) {
        Matcher m = uri_pattern.matcher(value);

        if (!m.matches()) {
            String[] prefix_uri = value.split(":");
            boolean prex = prefixes.containsKey(prefix_uri[0]) && inverse_prefix.containsKey(prefixes.get(prefix_uri[0]));
            boolean alias = aliases.containsKey(prefix_uri[0]) && inverse_aliases.containsKey(aliases.get(prefix_uri[0]));
            if (!prex && !alias) {
                throw new RuntimeException(value);
            }
        }
    }

    private void getFieldValue(Object t, JsonObject o, Field f, JsonObject context) {
        try {
            if (f.getType().isArray()) {
                Class<?> componentType = f.getType().getComponentType();

                Object[] values = (Object[]) f.get(t);

                JsonArray list = new JsonArray();

                Arrays.stream(values).forEach(v -> {

                    list.add(getJsonElement(v, componentType, new JsonObject(), context));

                });

                String prop = f.isAnnotationPresent(Property.class) ? f.getAnnotation(Property.class).value() : f.getName();
                o.add(prop, list);

            } else if (String.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (String) f.get(t));
            else if (Character.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (Character) f.get(t));

            else if (Number.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (Number) f.get(t));
            else if (int.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (Integer) f.get(t));
            else if (float.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (Float) f.get(t));
            else if (long.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (Long) f.get(t));
            else if (double.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (Double) f.get(t));

            else if (Boolean.class.isAssignableFrom(f.getType()) || boolean.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (Boolean) f.get(t));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void getFieldsValue(Object t, JsonObject o, List<Field> fl, String field_uri, JsonObject context) {
        Field f = fl.get(0);

        try {
            if (String.class.isAssignableFrom(f.getType())) {
                o.add(field_uri, getStringValueList(t, fl));
            } else if (Character.class.isAssignableFrom(f.getType()))
                o.add(field_uri, getCharValueList(t, fl));

            else if (Number.class.isAssignableFrom(f.getType()))
                o.add(field_uri, getNumValueList(t, fl));
            else if (int.class.isAssignableFrom(f.getType()))
                o.add(field_uri, getIntValueList(t, fl));
            else if (float.class.isAssignableFrom(f.getType()))
                o.add(field_uri, getFloatValueList(t, fl));
            else if (long.class.isAssignableFrom(f.getType()))
                o.add(field_uri, getLongValueList(t, fl));
            else if (double.class.isAssignableFrom(f.getType()))
                o.add(field_uri, getDoubleValueList(t, fl));

            else if (Boolean.class.isAssignableFrom(f.getType()) || boolean.class.isAssignableFrom(f.getType()))
                o.addProperty(f.getName(), (Boolean) f.get(t));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private JsonArray getStringValueList(Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((String) f.get(t));
        }
        return list;
    }

    private JsonArray getBooleanValueList(Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((Boolean) f.get(t));
        }
        return list;
    }

    private JsonArray getIntValueList(Object t, List<Field> fields) throws IllegalAccessException {

        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((int) f.get(t));
        }
        return list;
    }

    private JsonArray getLongValueList(Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((long) f.get(t));
        }
        return list;
    }

    private JsonArray getFloatValueList(Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((float) f.get(t));
        }
        return list;
    }

    private JsonArray getDoubleValueList(Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((double) f.get(t));
        }
        return list;
    }

    private JsonArray getNumValueList(Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((Number) f.get(t));
        }
        return list;
    }

    private JsonArray getCharValueList(Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((Character) f.get(t));
        }
        return list;
    }

    private boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers()) &&
                method.getParameterTypes().length == 0) {
            if (method.getName().matches("^get[A-Z].*") &&
                    !method.getReturnType().equals(void.class))
                return true;
            if (method.getName().matches("^is[A-Z].*") &&
                    method.getReturnType().equals(boolean.class))
                return true;
        }
        return false;
    }
}
