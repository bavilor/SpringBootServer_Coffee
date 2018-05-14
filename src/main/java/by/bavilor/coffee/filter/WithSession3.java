package by.bavilor.coffee.filter;

import by.bavilor.coffee.controller.CryptoController;
import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Signature;

/**
 * Created by bosak on 5/7/2018.
 */
@Component
@Order(2)
public class WithSession3 implements Filter {

    @Autowired
    private UserService userService;
    @Autowired
    private CryptoController cryptoController;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if((Boolean) servletRequest.getAttribute("skip")){
           filterChain.doFilter(servletRequest,servletResponse);
       }else{
            try{
                String session = cryptoController.decryptSessionID(((HttpServletRequest) servletRequest).getHeader("session"));
                if(userService.checkUser(session)){
                    servletRequest.setAttribute("decryptedSession", session);
                    filterChain.doFilter(servletRequest, servletResponse);
                }else {
                    ((HttpServletResponse) servletResponse).setStatus(403);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            //filterChain.doFilter(servletRequest, servletResponse);
       }
    }

    @Override
    public void destroy() {

    }
}
