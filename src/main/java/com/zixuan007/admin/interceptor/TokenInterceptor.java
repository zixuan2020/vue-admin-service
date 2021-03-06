package com.zixuan007.admin.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zixuan007.admin.common.anno.PassToken;
import com.zixuan007.admin.common.utils.TokenUtil;
import com.zixuan007.admin.constant.Result;
import com.zixuan007.admin.constant.ResultStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author apple
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    private static final String TOKEN_NAME = "small-admin-token";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        boolean isHandlerMethod = handler instanceof HandlerMethod;
        if (!isHandlerMethod) {
            return true;
        }

        //不需要登录注解
        boolean passToken = ((HandlerMethod) handler).getMethodAnnotation(PassToken.class) != null;
        if (passToken) return true;

        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("iview-admin-token");
        if (token != null) {
            boolean result = TokenUtil.parseToken(token) != null;
            if (result) {
                return true;
            }
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValueAsString(Result.failure(ResultStatus.LOGIN_ERROR));
            response.getWriter().append(objectMapper.writeValueAsString(Result.failure(ResultStatus.LOGIN_ERROR)));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
            return false;
        }

        return false;

    }


}
