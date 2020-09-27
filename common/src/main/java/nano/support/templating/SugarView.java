package nano.support.templating;

import nano.support.Sugar;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static nano.support.Sugar.getFirst;

/**
 * @author cbdyzj
 * @see Sugar#render
 * @since 2020.9.22
 */
public class SugarView extends AbstractTemplateView {

    private String charset;

    @Override
    public boolean checkResource(@NotNull Locale locale) {
        return this.getResource().exists();
    }

    @Override
    protected void renderMergedTemplateModel(@NotNull Map<String, Object> model,
                                             @NotNull HttpServletRequest request,
                                             @NotNull HttpServletResponse response) throws Exception {
        var template = this.getResourceAsString(this.getResource());
        var rendered = Sugar.render(template, model);
        response.setCharacterEncoding(this.charset);
        var contentType = getProducibleContentType(request);
        if (contentType != null) {
            response.setContentType(contentType);
        }
        var writer = response.getWriter();
        try (writer) {
            writer.write(rendered);
            writer.flush();
        }
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    private Resource getResource() {
        return requireNonNull(this.getApplicationContext()).getResource(requireNonNull(this.getUrl()));
    }

    private String getResourceAsString(Resource resource) throws IOException {
        var inputStream = resource.getInputStream();
        try (inputStream) {
            return new String(inputStream.readAllBytes(), this.charset);
        }
    }

    /**
     * @see HandlerMapping
     * @see RequestMapping#produces
     */
    private static String getProducibleContentType(HttpServletRequest request) {
        var producibleMimeTypes = request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (producibleMimeTypes instanceof Set) {
            var mimeTypes = (Set<?>) producibleMimeTypes;
            if (!CollectionUtils.isEmpty(mimeTypes)) {
                return getFirst(mimeTypes).toString();
            }
        }
        return null;
    }
}
