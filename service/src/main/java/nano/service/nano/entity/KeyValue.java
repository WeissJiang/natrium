package nano.service.nano.entity;

import java.sql.Timestamp;
import java.util.Objects;

public class KeyValue {

    private Integer id;

    private String key;
    private String value;

    private Timestamp lastUpdatedTime;
    private Timestamp creationTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Timestamp getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Timestamp lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyValue keyValue = (KeyValue) o;
        return Objects.equals(id, keyValue.id) && Objects.equals(key, keyValue.key) && Objects.equals(value, keyValue.value) && Objects.equals(lastUpdatedTime, keyValue.lastUpdatedTime) && Objects.equals(creationTime, keyValue.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, value, lastUpdatedTime, creationTime);
    }
}
