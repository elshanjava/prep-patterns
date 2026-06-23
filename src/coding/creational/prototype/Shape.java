package coding.creational.prototype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shape implements Prototype<Shape> {

    private final String color;
    private final int radius;
    private final List<String> tags;

    Shape(String color, int radius, List<String> tags) {
        this.color = color;
        this.radius = radius;
        this.tags = new ArrayList<>(tags);
    }

    @Override
    public Shape copy() {
        return new Shape(color, radius, tags);
    }

    public String getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }
}
