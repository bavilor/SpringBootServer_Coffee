package by.bavilor.coffee.filter;

import by.bavilor.coffee.crypto.Encrypt;
import by.bavilor.coffee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bosak on 5/7/2018.
 */
@Component
@Order(1)
public class WithoutSession2 implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        //check URL and method
        if((Boolean) servletRequest.getAttribute("skip")){
            servletRequest.setAttribute("skip", false);
            filterChain.doFilter(servletRequest,servletResponse);
        }else{
            //send redirect if url isn't /registration??
            if((httpServletRequest.getRequestURL().toString().equals("http://localhost:8080/registration"))){
                servletRequest.setAttribute("skip", true);
                filterChain.doFilter(servletRequest,servletResponse);
            }else {
                ((HttpServletResponse) servletResponse).setStatus(403);
            }
        }

    }

    @Override
    public void destroy() {

    }
}
