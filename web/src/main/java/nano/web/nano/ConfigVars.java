package nano.web.nano;

import java.util.Map;

/**
 * 环境配置Key
 */
public class ConfigVars {

    /**
     * nano API
     */
    private String nanoApi;

    /**
     * For verifying the Telegram webhook
     */
    private String nanoApiKey;

    /**
     * 百度翻译APP ID
     */
    private String baiduTranslationAppId;

    /**
     * Secret key
     */
    private String baiduTranslationSecretKey;

    /**
     * Telegram Bots
     */
    private Map<String, Bot> bots;


    public String getNanoApi() {
        return nanoApi;
    }

    public void setNanoApi(String nanoApi) {
        this.nanoApi = nanoApi;
    }

    public String getNanoApiKey() {
        return nanoApiKey;
    }

    public void setNanoApiKey(String nanoApiKey) {
        this.nanoApiKey = nanoApiKey;
    }

    public String getBaiduTranslationAppId() {
        return baiduTranslationAppId;
    }

    public void setBaiduTranslationAppId(String baiduTranslationAppId) {
        this.baiduTranslationAppId = baiduTranslationAppId;
    }

    public String getBaiduTranslationSecretKey() {
        return baiduTranslationSecretKey;
    }

    public void setBaiduTranslationSecretKey(String baiduTranslationSecretKey) {
        this.baiduTranslationSecretKey = baiduTranslationSecretKey;
    }

    public Map<String, Bot> getBots() {
        return bots;
    }

    public void setBots(Map<String, Bot> bots) {
        this.bots = bots;
    }
}
