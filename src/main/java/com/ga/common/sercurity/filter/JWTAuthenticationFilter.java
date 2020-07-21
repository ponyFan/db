package com.ga.common.sercurity.filter;

import com.alibaba.fastjson.JSON;
import com.ga.common.sercurity.JwtTokenUtils;
import com.ga.common.sercurity.JwtUser;
import com.ga.common.sercurity.LoginUser;
import com.ga.entity.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingwk on 2019/11/17
 */
@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private ThreadLocal<Integer> rememberMe = new ThreadLocal<>();
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        // 从输入流中获取到登录的信息
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            LoginUser loginUser = new LoginUser();
            BeanUtils.populate(loginUser, parameterMap);
            rememberMe.set(loginUser.getRememberMe());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), new ArrayList<>())
            );
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("attemptAuthentication error :{}",e);
            return null;
        }
    }

    // 成功验证后调用的方法
    // 如果验证成功，就生成token并返回
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {

        JwtUser jwtUser = (JwtUser) authResult.getPrincipal();
        boolean isRemember = rememberMe.get() == 1;

        /*String role = "";
        Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
        for (GrantedAuthority authority : authorities){
            role = authority.getAuthority();
        }*/

        String token = JwtTokenUtils.createToken(jwtUser.getId(), jwtUser.getUsername(), jwtUser.getRole(), jwtUser.getGroup(), isRemember);
        response.setHeader("token", JwtTokenUtils.TOKEN_PREFIX + token);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> maps = new HashMap<>();
        maps.put("data", JwtTokenUtils.TOKEN_PREFIX + token);
        /*maps.put("roles", role.split(","));*/
        response.getWriter().write(JSON.toJSONString(new BaseResponse(200, maps)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSON.toJSON(new BaseResponse<>(400, "登陆失败")).toString());
    }
}
