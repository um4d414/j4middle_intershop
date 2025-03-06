package ru.umd.intershop.web.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class BackRedirectInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        ModelAndView modelAndView
    ) {
        if (modelAndView != null && "redirectBack".equals(modelAndView.getViewName())) {
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isBlank()) {
                modelAndView.setViewName("redirect:" + referer);
            } else {
                modelAndView.setViewName("redirect:/main/items");
            }
        }
    }
}