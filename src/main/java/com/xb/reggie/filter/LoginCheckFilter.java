package com.xb.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.xb.reggie.common.BaseContext;
import com.xb.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 * @author xb
 * @create 2022-12-08 16:11
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();
        log.info("此次请求的uri:{}",requestURI);

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",  //员工登录
                "/employee/logout", //员工退出
                "/user/sendMsg",     //用户发送验证码
                "/user/login",       //用户登录
                "/common/**",       //公共
                "/backend/**",      //后台静态页面
                "/front/**"         //用户静态页面
        };
        //2.判断本次请求是否需要处理
        boolean res = checkUrls(requestURI, urls);

        //3.如果不需要处理则直接放行
        if(res){
            log.info("此次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1.判断登录状态,如果已登录,则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            log.info("用户已登录,用户id:{}",empId);
            //将用户id存储到ThreadLocal
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        //4-2.判断登录状态,如果已登录,则直接放行
        if(request.getSession().getAttribute("user")!=null){
            Long userId = (Long) request.getSession().getAttribute("user");
            log.info("用户已登录,用户id:{}",userId);
            //将用户id存储到ThreadLocal
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录返回未登录结果,通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));


    }

    /**
     * 路径匹配,检查本次请求是否需要放行
     * @param requestURl
     * @param urls
     * @return
     */
    public boolean checkUrls(String requestURl,String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURl);
            if(match){
                return true;
            }
        }
        return false;
    }

}
