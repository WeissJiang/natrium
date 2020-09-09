package nano.web.service.scripting;

import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
public class ScriptResourceTransformer implements ResourceTransformer {

    private static final Charset utf8 = StandardCharsets.UTF_8;

    private final ScriptService scriptService;

    @Autowired
    public ScriptResourceTransformer(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    @NonNull
    @Override
    public Resource transform(@NonNull HttpServletRequest request,
                              @NonNull Resource resource,
                              @NonNull ResourceTransformerChain transformerChain) throws IOException {
        @Cleanup var inputStream = resource.getInputStream();
        var origin = StreamUtils.copyToString(inputStream, utf8);
        var transpiled = this.scriptService.transpileModule(origin);
        return new TransformedResource(resource, transpiled.getBytes(utf8));
    }
}
