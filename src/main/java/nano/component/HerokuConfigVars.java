package nano.component;

import org.springframework.stereotype.Component;

@Component
public class HerokuConfigVars {

    public String nanoTgApiToken() {
        var token = System.getenv("NANO_TG_API_TOKEN");
        return token != null ? token : "";
    }

    /**
     * 把API Token倒转一下作为Webhook token用
     */
    public String nanoTgWebhookToken() {
        var apiToken = this.nanoTgApiToken();
        return new StringBuilder(apiToken).reverse().toString();
    }
}
