package com.ambitious.vcbestm.filter;

import com.ambitious.vcbestm.common.CommonResult;
import com.ambitious.vcbestm.exception.ErrCode;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Ambitious
 * @date 2022/6/18 15:46
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    private final AntPathMatcher MATCHER = new AntPathMatcher();
    private final String[] anonymousUrl = new String[] {
        "/api/student/register",
        "/api/student/login/**",
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 放行匿名 url
        String uri = request.getRequestURI();
        for (String au : anonymousUrl) {
            if (MATCHER.match(au, uri)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        // 检查 session 是否有登录态
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (userId != null) {
            LocalUser.set(userId);
            filterChain.doFilter(request, response);
            return;
        }
        Gson gson = new Gson();
        CommonResult<?> res = CommonResult.fail(ErrCode.NEED_LOGIN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(gson.toJson(res));
        response.getWriter().flush();
    }
}
