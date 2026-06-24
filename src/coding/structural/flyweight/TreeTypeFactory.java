package coding.structural.flyweight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TreeTypeFactory {
    private final Map<String, TreeType> cache = new ConcurrentHashMap<>();

    public TreeType getTreeType(String name, String color, String texture) {
        String key = name + "-" + color + "-" + texture;
        // есть в кэше — отдаём существующий; нет — создаём и кладём
        return cache.computeIfAbsent(key, k -> new TreeType(name, color, texture));
    }

    // сколько уникальных flyweight-объектов реально создано
    public int cacheSize() {
        return cache.size();
    }
}
