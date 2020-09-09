package nano.web.service.scripting;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ScriptResourceTransformer implements ResourceTransformer {

    private static final Charset utf8 = StandardCharsets.UTF_8;

    @lombok.NonNull
    private final ScriptService scriptService;

    @NonNull
    @Override
    public Resource transform(@NonNull HttpServletRequest request, @NonNull Resource resource,
                              @NonNull ResourceTransformerChain transformerChain) throws IOException {
        @Cleanup var inputStream = resource.getInputStream();
        var origin = StreamUtils.copyToString(inputStream, utf8);
        var filename = resource.getFilename();
        var suffix = StringUtils.getFilenameExtension(filename);
        String transpiled;
        // script
        if (Scripting.SCRIPT_SUFFIX.contains(suffix)) {
            transpiled = this.scriptService.transpileScriptModule(origin);
        }
        // style
        else if (Scripting.STYLE_SUFFIX.contains(suffix)) {
            transpiled = this.scriptService.transpileStyleModule(origin);
        }
        // others
        else {
            throw new IllegalStateException("Illegal suffix");
        }
        return new TransformedResource(resource, transpiled.getBytes(utf8));
    }
}
