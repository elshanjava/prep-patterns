package coding.structural.flyweight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TreeTypeFactory {
    private final Map<String, TreeType> cache = new ConcurrentHashMap<>();

    public TreeType getTreeType(String name, String color, String texture) {
        String key = name + "-" + color + "-" + texture;
        return cache.computeIfAbsent(key, k -> new TreeType(name, color, texture));
    }

    public int cacheSize() {
        return cache.size();
    }
}
