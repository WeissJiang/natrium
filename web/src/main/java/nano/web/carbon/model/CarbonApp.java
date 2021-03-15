package nano.web.carbon.model;

import java.util.List;

public class CarbonApp {

    private String id;
    private String name;
    private String fallbackLocale;
    private List<String> localeList;
    private List<CarbonPage> pageList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFallbackLocale() {
        return fallbackLocale;
    }

    public void setFallbackLocale(String fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
    }

    public List<String> getLocaleList() {
        return localeList;
    }

    public void setLocaleList(List<String> localeList) {
        this.localeList = localeList;
    }

    public List<CarbonPage> getPageList() {
        return pageList;
    }

    public void setPageList(List<CarbonPage> pageList) {
        this.pageList = pageList;
    }
}
