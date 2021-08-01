package nano.service.carbon.model;

import java.util.List;

public record CarbonApp (
         String id,
         String name,
         String description,
         String url,
         List<String> localeList,
         String fallbackLocale,
         List<CarbonPage> pageList
){
}
