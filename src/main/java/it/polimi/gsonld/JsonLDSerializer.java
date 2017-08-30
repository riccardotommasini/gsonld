package it.polimi.gsonld;

import com.google.gson.*;
import it.polimi.gsonld.annotations.*;
import it.polimi.gsonld.annotations.Object;
import it.polimi.gsonld.annotations.Type;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.gsonld.JsonldUtils.*;

/**
 * Created by riccardo on 24/08/2017.
 */
public class JsonLDSerializer<T> implements JsonSerializer<T> {

    private HashMap<String, String> prefixes, aliases;
    private HashMap<String, String> inverse_prefix, inverse_aliases;
    private HashMap<String, Set<String>> properties;
    private HashMap<String, String> reference_names;

    private boolean expand_prefixes = false, autogen_prefixes = false;

    public JsonLDSerializer() {
        this.prefixes = new HashMap<>();
        this.aliases = new HashMap<>();
        this.inverse_prefix = new HashMap<>();
        this.inverse_aliases = new HashMap<>();
        this.properties = new HashMap<>();
        this.reference_names = new HashMap<>();

    }

    public static JsonLDSerializer<?> get(Class<?> c) {
        return new JsonLDSerializer<>();
    }

    public JsonElement serialize(T t, java.lang.reflect.Type typeOfSrc, JsonSerializationContext jsonc) {

        JsonElement res = null;

        if (t.getClass().isAnnotationPresent(Object.class)) {

            Object a = t.getClass().getAnnotation(Object.class);

            if (a.expanded()) {

            } else if (a.flattened()) {

            } else if (a.framed()) {

            } else {
                JsonObject o = new JsonObject();
                JsonElement c = buildContext(t, t.getClass(), new JsonObject());
                o.add(JSONLD_CONTEXT, c);
                res = getJsonElement(t, t.getClass(), o, c);
            }
        } else if (t.getClass().isAnnotationPresent(Property.class)) {

        }

        return res;

    }

    private JsonElement buildContext(java.lang.Object t, Class<?> c, JsonElement context) {

        extractAliases(c, context);

        Arrays.stream(c.getMethods()).filter(this::isGetter).collect(Collectors.toList())
                .forEach(m -> updateContext(c, context, m, m.getName(), getPropertyName(m), m.getReturnType()));

        Arrays.stream(c.getFields()).forEach((Field f) ->
                updateContext(c, context, f, f.getName(), getPropertyName(f), f.getType()));

        if (c.isAnnotationPresent(Base.class) && isURI(c.getAnnotation(Base.class).value())) {
            if (!context.isJsonObject() || ((JsonObject) context).size() == 0) {
                return new JsonPrimitive(c.getAnnotation(Base.class).value());
            } else
                ((JsonObject) context).addProperty(JSONLD_VOCAB, c.getAnnotation(Base.class).value());
        }

        if (c.isAnnotationPresent(Vocab.class) && isURI(c.getAnnotation(Vocab.class).value())) {
            if (!context.isJsonObject() || ((JsonObject) context).size() == 0) {
                return new JsonPrimitive(c.getAnnotation(Vocab.class).value());
            } else
                ((JsonObject) context).addProperty(JSONLD_VOCAB, c.getAnnotation(Vocab.class).value());
        }

        return context;
    }

    private JsonElement getJsonElement(java.lang.Object t, Class<?> aClass, JsonObject o, JsonElement context) {

        Map<String, JsonArray> properties_objects = new HashMap<>();

        String type = new String();
        String id = new String();

        if (!aClass.isAnnotationPresent(Object.class)) {
            buildContext(t, aClass, context);
        }

        if (aClass.isAnnotationPresent(Type.class)) {
            type = aClass.getAnnotation(Type.class).value();

            checkPrefix(type, aClass);

            if (expand_prefixes) {
                type = expand(type);
            }
            o.addProperty(JSONLD_TYPE, type);

        }

        Arrays.stream(aClass.getMethods()).filter(this::isGetter).collect(Collectors.toList()).forEach(
                m -> {
                    String property = reference_names.containsKey(m.getName()) ? reference_names.get(m.getName()) : getPropertyName(m);
                    getMethodValue(t, o, m, property, context);
                });

        Field[] fields = aClass.getFields();

        boolean foundId = false;

        for (int i = 0; i < fields.length; i++) {

            Field f = fields[i];

            if (!foundId && f.isAnnotationPresent(Id.class)) {
                id = f.getAnnotation(Id.class).value();
                foundId = true;
                o.addProperty(JSONLD_ID, id);
            }

            String name = f.getName();
            String prop_name = reference_names.containsKey(name) ? reference_names.get(name) : name;

            if (properties.containsKey(prop_name) && properties.get(prop_name).size() > 1) {
                try {
                    if (properties_objects.containsKey(prop_name)) {
                        JsonArray array = properties_objects.get(prop_name);
                        if (f.getType().isAssignableFrom(String.class)) {
                            array.add((String) f.get(t));
                        } else if (f.getType().isAssignableFrom(Boolean.class)) {
                            array.add((Boolean) f.get(t));
                        } else {
                            array.add(f.get(t).toString());
                        }
                    } else {
                        JsonArray array = new JsonArray();
                        if (f.getType().isAssignableFrom(String.class)) {
                            array.add((String) f.get(t));
                        } else if (f.getType().isAssignableFrom(Boolean.class)) {
                            array.add((Boolean) f.get(t));
                        } else {
                            array.add(f.get(t).toString());
                        }
                        properties_objects.put(prop_name, array);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                getFieldValue(t, o, f, prop_name, context);
            }


            /* else if (f.isAnnotationPresent(Property.class)) {
                field_uri = f.getAnnotation(Property.class).value();

                checkPrefix(field_uri, aClass);

                if (expanded)
                    field_uri = expand(field_uri);

                if (properties.containsKey(field_uri) && properties.get(field_uri).size() < 2) {
                    context.addProperty(f.getName(), field_uri);
                }

            } else if (!f.isAnnotationPresent(Property.class) && aClass.isAnnotationPresent(Base.class)) {
                context.addProperty(f.getName(), aClass.getAnnotation(Base.class).value() + f.getName());
            } else if (!f.isAnnotationPresent(Property.class) && f.isAnnotationPresent(Type.class) && f.isAnnotationPresent(NameSpace.class)) {
                JsonObject p = new JsonObject();
                p.addProperty(JSONLD_ID, field_uri = f.getAnnotation(NameSpace.class).value() + f.getName());
                p.addProperty(JSONLD_TYPE, f.getAnnotation(Type.class).value());
                context.add(f.getName(), p);
                getFieldValue(t, o, f, context);
            } else if (!f.isAnnotationPresent(Property.class) && f.isAnnotationPresent(NameSpace.class)) {
                context.addProperty(f.getName(), field_uri = f.getAnnotation(NameSpace.class).value() + f.getName());
            } else {
                getFieldValue(t, o, f, null);
            }*/
        }

        properties_objects.forEach((k, v) -> {
            o.add(k, v);
        });

        return o;
    }

    private void updateContext(Class<?> aClass, JsonElement context, AccessibleObject m, String name, String property, Class<?> type) {

        if ("getClass".equals(name)) {
            return;
        }

        String reference_name = property;

        if (m.isAnnotationPresent(Property.class)) {

            String field_uri = m.getAnnotation(Property.class).value();

            if (m.isAnnotationPresent(Type.class)) {
                JsonObject p = new JsonObject();

                String property_type = m.getAnnotation(Type.class).value();

                p.addProperty(JSONLD_TYPE, property_type);
                p.addProperty(JSONLD_ID, field_uri);

                checkPrefix(property_type, aClass);

                if (context.isJsonPrimitive()) {

                } else if (context.isJsonObject()) {
                    ((JsonObject) context).add(reference_name, p);
                }

            } else {
                if (context.isJsonPrimitive()) {

                } else if (context.isJsonObject()) {
                    if (!isAliased(field_uri) || isPrefixed(field_uri)) {
                        ((JsonObject) context).addProperty(reference_name, field_uri);
                    }
                }
            }
        }

        if (m.isAnnotationPresent(Prefix.class) && isPrefix(m.getAnnotation(Prefix.class).value())) {
            String prefix = m.getAnnotation(Prefix.class).value();
            reference_name = prefix + ":" + reference_name;
            if (m.isAnnotationPresent(Type.class)) {
                JsonObject p = new JsonObject();

                String property_type = m.getAnnotation(Type.class).value();

                p.addProperty(JSONLD_TYPE, property_type);

                checkPrefix(property_type, aClass);

                if (context.isJsonPrimitive()) {

                } else if (context.isJsonObject()) {
                    ((JsonObject) context).add(reference_name, p);
                }
            }
        }

        updateReferenceMaps(name, reference_name);
    }

    private void updateReferenceMaps(String name, String reference_name) {
        Set<String> s = properties.containsKey(reference_name) ? properties.get(reference_name) : new HashSet<>();
        s.add(name);
        properties.put(reference_name, s);
        reference_names.put(name, reference_name);
    }

    private void extractAliases(Class<?> tclass, JsonElement context) {
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

        if ((has_alias || has_aliases) && !tclass.getSuperclass().equals(java.lang.Object.class)) {
            extractAliases(tclass.getSuperclass(), context);
        }
    }

    private void addAlias(JsonElement context, Alias p) {
        String alias = p.alias();
        String val = p.value();
        this.aliases.put(alias, val);
        this.inverse_aliases.put(val, alias);

        if ("[Unassigned]".equals(p.type())) {
            if (context.isJsonObject())
                ((JsonObject) context).addProperty(alias, val);
        } else {
            JsonObject ao = new JsonObject();
            ao.addProperty(JSONLD_ID, val);
            ao.addProperty(JSONLD_TYPE, p.type());
            ((JsonObject) context).add(alias, ao);
        }
    }

    private String expand(String type) {
        String[] prefix_val = type.split(":");
        return prefixes.get(prefix_val[0]) + prefix_val[1];
    }

    private void checkPrefix(String value, Class<?> aClass) {

        if (JSONLD_ID.equals(value)) {
            return;
        } else if (!isURI(value) && !isPrefixed(value) && !isAliased(value) && !aClass.isAnnotationPresent(Vocab.class)) {
            throw new RuntimeException(value);
        }

    }

    private boolean isURI(String s) {
        return uri_pattern.matcher(s).matches();
    }

    private boolean isAliased(String s) {
        boolean b = aliases.containsKey(s) && inverse_aliases.containsKey(aliases.get(s));
        return b;
    }

    private boolean isPrefixed(String s) {
        String[] prefix_uri = s.split(":");
        boolean b = aliases.containsKey(prefix_uri[0]) && inverse_aliases.containsKey(aliases.get(prefix_uri[0]));
        return s.contains(":") && b;
    }

    private boolean isPrefix(String p) {
        return aliases.containsKey(p);
    }

    private String getFieldValue(java.lang.Object t, JsonObject o, Field f, String property_name, JsonElement context) {
        try {
            Class<?> type = f.getType();
            java.lang.Object val = f.get(t);
            return accessValue(o, context, type, property_name, val);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getMethodValue(java.lang.Object t, JsonObject o, Method f, String method, JsonElement context) {
        try {
            if (f.getName().equals("getClass")) {
                return null;
            }

            java.lang.Object invoke = f.invoke(t);
            Class<?> type = f.getReturnType();

            return accessValue(o, context, type, method, invoke);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getPropertyName(Method f) {
        String method = f.getName().replace("get", "").replace("is", "");
        String substring = method.substring(0, 1);
        method = method.replace(substring, substring.toLowerCase());

        String property = f.isAnnotationPresent(Property.class) ? f.getAnnotation(Property.class).value() : method;

        property = (f.isAnnotationPresent(As.class) && !f.getAnnotation(As.class).value().equals("[Unassigned]")) ?
                f.getAnnotation(As.class).value() : property;

        property = isURI(property) ? method : property;

        if (!isURI(property) && !isPrefixed(property) && f.isAnnotationPresent(Prefix.class)) {
            String prefix = f.getAnnotation(Prefix.class).value();
            if (prefixes.containsKey(prefix)) {
                property = prefix + ":" + property;
            }
        }

        return property;
    }

    private String getPropertyName(Field f) {
        String property = f.isAnnotationPresent(Property.class) ? f.getAnnotation(Property.class).value() : f.getName();

        property = (f.isAnnotationPresent(As.class) && !f.getAnnotation(As.class).value().equals("[Unassigned]")) ?
                f.getAnnotation(As.class).value() : property;

        property = isURI(property) ? f.getName() : property;

        if (!isURI(property) && !isPrefixed(property) && f.isAnnotationPresent(Prefix.class)) {
            String prefix = f.getAnnotation(Prefix.class).value();
            if (prefixes.containsKey(prefix)) {
                property = prefix + ":" + property;
            }
        }

        return property;
    }

    private String accessValue(JsonObject o, JsonElement context, Class<?> type, String property, java.lang.Object val) {

        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();

            java.lang.Object[] values = (java.lang.Object[]) val;

            JsonArray list = new JsonArray();

            Arrays.stream(values).forEach(v -> {

                list.add(getJsonElement(v, componentType, new JsonObject(), context));

            });
            o.add(property, list);

        } else if (String.class.isAssignableFrom(type))
            o.addProperty(property, (String) val);
        else if (Character.class.isAssignableFrom(type))
            o.addProperty(property, (Character) val);
        else if (Number.class.isAssignableFrom(type))
            o.addProperty(property, val.toString());
        else if (int.class.isAssignableFrom(type))
            o.addProperty(property, (Integer) val);
        else if (float.class.isAssignableFrom(type))
            o.addProperty(property, val.toString());
        else if (long.class.isAssignableFrom(type))
            o.addProperty(property, val.toString());
        else if (double.class.isAssignableFrom(type))
            o.addProperty(property, val.toString());
        else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type))
            o.addProperty(property, (Boolean) val);
        else {
            JsonElement jsonElement = getJsonElement(val, val.getClass(), new JsonObject(), context);
            o.add(property, jsonElement);
        }
        return property;

    }


    private void getFieldsValue(java.lang.Object t, JsonObject o, List<Field> fl, String field_uri, JsonElement context) {
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
                o.add(field_uri, getBooleanValueList(t, fl));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private JsonArray getStringValueList(java.lang.Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((String) f.get(t));
        }
        return list;
    }

    private JsonArray getBooleanValueList(java.lang.Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((Boolean) f.get(t));
        }
        return list;
    }

    private JsonArray getIntValueList(java.lang.Object t, List<Field> fields) throws IllegalAccessException {

        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((int) f.get(t));
        }
        return list;
    }

    private JsonArray getLongValueList(java.lang.Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((long) f.get(t));
        }
        return list;
    }

    private JsonArray getFloatValueList(java.lang.Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((float) f.get(t));
        }
        return list;
    }

    private JsonArray getDoubleValueList(java.lang.Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((double) f.get(t));
        }
        return list;
    }

    private JsonArray getNumValueList(java.lang.Object t, List<Field> fields) throws IllegalAccessException {
        JsonArray list = new JsonArray();
        for (Field f : fields) {
            list.add((Number) f.get(t));
        }
        return list;
    }

    private JsonArray getCharValueList(java.lang.Object t, List<Field> fields) throws IllegalAccessException {
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
