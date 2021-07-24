package nano.service.carbon.model;

import java.util.List;

public class CarbonPage {

    private String name;
    private String code;
    private String description;
    private List<CarbonKey> keyList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CarbonKey> getKeyList() {
        return keyList;
    }

    public void setKeyList(List<CarbonKey> keyList) {
        this.keyList = keyList;
    }
}
