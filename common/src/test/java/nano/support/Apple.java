package nano.support;

import java.util.Objects;

public class Apple {

    private String color;
    private String tastes;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTastes() {
        return tastes;
    }

    public void setTastes(String tastes) {
        this.tastes = tastes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Apple apple = (Apple) o;
        return Objects.equals(color, apple.color) && Objects.equals(tastes, apple.tastes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, tastes);
    }
}
