package nano.service.carbon.model;

import java.util.List;

public class CarbonKey {

    private String key;
    private String pageCode;
    private String original;
    private List<CarbonText> translation;
    private String description;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPageCode() {
        return pageCode;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public List<CarbonText> getTranslation() {
        return translation;
    }

    public void setTranslation(List<CarbonText> translation) {
        this.translation = translation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
