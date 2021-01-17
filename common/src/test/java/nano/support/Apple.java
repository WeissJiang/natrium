package nano.support;

import java.util.Objects;

public class Apple {

    private Integer id;

    private String name;

    private String color;

    private String tastes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
        return Objects.equals(id, apple.id) && Objects.equals(name, apple.name) && Objects.equals(color, apple.color) && Objects.equals(tastes, apple.tastes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, tastes);
    }
}
