package nano.support.templating;

import lombok.Cleanup;
import nano.support.Sugar;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class SugarView extends AbstractTemplateView {

    private Charset charset;

    @Override
    public boolean checkResource(@NotNull Locale locale) {
        var resource = this.getResource();
        return resource.exists();
    }

    @Override
    protected void renderMergedTemplateModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request,
                                             @NotNull HttpServletResponse response) throws Exception {
        var template = this.getString(this.getResource());
        var rendered = Sugar.render(template, model);
        @Cleanup var outputStream = response.getOutputStream();
        StreamUtils.copy(rendered, this.charset, outputStream);
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    private Resource getResource() {
        return requireNonNull(this.getApplicationContext()).getResource(requireNonNull(this.getUrl()));
    }

    private String getString(Resource resource) throws IOException {
        @Cleanup var inputStream = resource.getInputStream();
        return StreamUtils.copyToString(inputStream, this.charset);
    }
}
