package nano.web;

import lombok.Data;

/**
 * 环境配置Key
 */
@Data
public class ConfigVars {

    /**
     * nano
     */
    private String botName;
    private String nanoApi;
    private String nanoToken;

    /**
     * Telegram Bot token
     */
    private String telegramBotToken;

    /**
     * 百度翻译APP ID、secret key
     */
    private String baiduTranslationAppId;
    private String baiduTranslationSecretKey;

}
