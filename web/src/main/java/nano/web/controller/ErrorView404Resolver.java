package nano.web.controller;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * For single page application
 *
 * @author cbdyzj
 * @since 2021.1.30
 */
@Component
public class ErrorView404Resolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (HttpStatus.NOT_FOUND.equals(status)
                && HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            return new ModelAndView("forward:/", HttpStatus.OK);
        }
        return null;
    }
}
