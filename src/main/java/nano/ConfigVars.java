package nano;

import lombok.Data;

/**
 * 环境配置Key
 */
@Data
public class ConfigVars {
    /**
     * Telegram API token
     */
    private String telegramApiToken;

    /**
     * nano相关配置
     */
    private String nanoApi;
    private String nanoApiToken;

    /**
     * 百度翻译APP ID、secret key
     */
    private String baiduTranslationAppId;
    private String baiduTranslationSecretKey;

    /**
     * Postgres
     */
    private String jdbcUrl;
    private String username;
    private String password;
}
