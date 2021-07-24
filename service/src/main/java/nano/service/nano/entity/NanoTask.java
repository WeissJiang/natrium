package nano.service.nano.entity;

import java.util.Objects;

public class NanoTask {

    private Integer id;
    private String name;
    private String description;
    private String options;

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

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NanoTask nanoTask = (NanoTask) o;
        return Objects.equals(id, nanoTask.id) && Objects.equals(name, nanoTask.name) && Objects.equals(description, nanoTask.description) && Objects.equals(options, nanoTask.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, options);
    }
}
