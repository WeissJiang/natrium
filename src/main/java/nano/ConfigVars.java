package nano;

import lombok.Data;

/**
 * 环境配置Key
 */
@Data
public class ConfigVars {

    /**
     * nano
     */
    private String nanoApi;
    private String nanoApiToken;

    /**
     * Telegram API token
     */
    private String telegramApiToken;

    /**
     * 百度翻译APP ID、secret key
     */
    private String baiduTranslationAppId;
    private String baiduTranslationSecretKey;

}
