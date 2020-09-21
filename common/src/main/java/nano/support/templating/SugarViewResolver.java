package nano.support.templating;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import java.nio.charset.Charset;

public class SugarViewResolver extends AbstractTemplateViewResolver {

    private String charset = "utf8";

    public SugarViewResolver() {
        this.setViewClass(requiredViewClass());
    }

    @NotNull
    @Override
    protected Class<?> requiredViewClass() {
        return SugarView.class;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @NotNull
    @Override
    protected AbstractUrlBasedView buildView(@NotNull String viewName) throws Exception {
        var view = (SugarView) super.buildView(viewName);
        view.setCharset(Charset.forName(this.charset));
        return view;
    }
}
