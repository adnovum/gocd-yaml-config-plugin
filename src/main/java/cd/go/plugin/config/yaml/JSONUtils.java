package cd.go.plugin.config.yaml;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class JSONUtils {
    static <T> T fromJSON(String json) {
        return new GsonBuilder().create().fromJson(json, new TypeToken<T>() {}.getType());
    }

    static String toJSON(Object object) {
        return new GsonBuilder().create().toJson(object);
    }

    public static void addOptionalValue(Map<String, Object> dest, Map<String, Object> src, String jsonField, String yamlFieldName) {
        if (src.containsKey(jsonField)) {
            dest.put(yamlFieldName, src.get(jsonField));
        }
    }

    public static void addOptionalList(Map<String, Object> dest, Map<String, Object> src, String jsonField, String yamlFieldName) {
        List<Object> value = (List<Object>) src.get(jsonField);
        if (value != null && !value.isEmpty()) {
            dest.put(yamlFieldName, value);
        }
    }

    public static void addOptionalInt(Map<String, Object> dest, Map<String, Object> src, String jsonField, String yamlFieldName) {
        Object n = src.get(jsonField);
        if (n == null)
            return;
        if (n instanceof Double) {
            dest.put(yamlFieldName, ((Double) n).intValue());
        } else {
            dest.put(yamlFieldName, n);
        }
    }

    public static void addRequiredValue(Map<String, Object> dest, Map<String, Object> src, String jsonField, String yamlFieldName) {
        Object value = src.get(jsonField);
        if (value == null)
            throw new YamlConfigException("field " + yamlFieldName + ": is required");

        dest.put(yamlFieldName, value);
    }

    public static Stream<JsonElement> stream(JsonArray array) {
        return StreamSupport.stream(array.spliterator(), false);
    }

    public static String getKeyAsString(JsonElement element, String key) {
        JsonElement value = element.getAsJsonObject().get(key);
        return value != null ? value.getAsString() : null;
    }
}