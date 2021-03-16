package nano.web.carbon.model;

public class CarbonAppOverview {

    private String id;
    private String name;
    private String description;
    private String url;
    private Integer pageCount;
    private Integer keyCount;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getKeyCount() {
        return keyCount;
    }

    public void setKeyCount(Integer keyCount) {
        this.keyCount = keyCount;
    }
}
