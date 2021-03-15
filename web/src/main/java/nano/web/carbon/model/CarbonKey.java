package nano.web.carbon.model;

import java.util.List;

public class CarbonKey {

    private String key;
    private String pageCode;
    private List<CarbonText> original;
    private List<CarbonText> translation;

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

    public List<CarbonText> getOriginal() {
        return original;
    }

    public void setOriginal(List<CarbonText> original) {
        this.original = original;
    }

    public List<CarbonText> getTranslation() {
        return translation;
    }

    public void setTranslation(List<CarbonText> translation) {
        this.translation = translation;
    }
}
