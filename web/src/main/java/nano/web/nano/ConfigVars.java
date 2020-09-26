package nano.web.nano;

/**
 * 环境配置Key
 */
public class ConfigVars {

    /**
     * nano
     */
    private String botName;
    private String nanoApi;

    /**
     * For verifying the Telegram webhook
     */
    private String nanoApiKey;

    /**
     * Telegram Bot token
     */
    private String telegramBotToken;

    /**
     * 百度翻译APP ID、secret key
     */
    private String baiduTranslationAppId;
    private String baiduTranslationSecretKey;

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

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

    public String getTelegramBotToken() {
        return telegramBotToken;
    }

    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
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
}
