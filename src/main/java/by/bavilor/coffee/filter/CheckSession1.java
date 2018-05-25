package by.bavilor.coffee.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;


/**
 * Created by bosak on 5/3/2018.
 */
@Component
@Order(0)
public class CheckSession1 implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response =  ((HttpServletResponse) servletResponse);
        HttpServletRequest request =  ((HttpServletRequest) servletRequest);

        ((HttpServletResponse) servletResponse).setHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) servletResponse).setHeader("Access-Control-Allow-Headers", "*");

        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(200);
        }else{
            System.out.println("Try to connecting. " + request.getMethod() + " to " + request.getRequestURL());
            if(request.getHeader("session") == null || request.getHeader("session").equals("")){
                servletRequest.setAttribute("skip", false);
                ((HttpServletResponse) servletResponse).setHeader("session", ((HttpServletRequest) servletRequest).getSession().getId());
                filterChain.doFilter(servletRequest,servletResponse);
            }else{
                servletRequest.setAttribute("skip", true);
                filterChain.doFilter(servletRequest,servletResponse);
            }
        }
    }

    @Override
    public void destroy() {

    }

}
