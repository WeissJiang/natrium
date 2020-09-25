package nano.support.templating;

import lombok.Cleanup;
import nano.support.Sugar;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;

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
        @Cleanup var writer = response.getWriter();
        writer.write(rendered);
        writer.flush();
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    private Resource getResource() {
        return requireNonNull(this.getApplicationContext()).getResource(requireNonNull(this.getUrl()));
    }

    private String getResourceAsString(Resource resource) throws IOException {
        @Cleanup var inputStream = resource.getInputStream();
        return new String(inputStream.readAllBytes(), this.charset);
    }
}
