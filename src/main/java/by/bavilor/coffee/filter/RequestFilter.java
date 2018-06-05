package by.bavilor.coffee.filter;

import by.bavilor.coffee.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.PublicKey;


/**
 * Created by bosak on 5/3/2018.
 */

@Component
@Order(0)
public class RequestFilter implements Filter {

    @Autowired
    private FilterService filterService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response =  ((HttpServletResponse) servletResponse);
        HttpServletRequest request =  ((HttpServletRequest) servletRequest);

        String method = request.getMethod();
        String jsonUserPublicKey = request.getHeader("key");

        if(method.equals("GET")){
            if(jsonUserPublicKey != null){
                try{
                    PublicKey userPublicKey = filterService.decodeUPK(jsonUserPublicKey);
                    RequestWrapper requestWrapper = filterService.getRequestWrapper(request, null);
                    ResponseWrapper responseWrapper = filterService.getResponseWrapper(response);
                    filterService.createUser(userPublicKey);

                    filterChain.doFilter(requestWrapper, responseWrapper);

                    byte[] encrData = filterService.formResponse(userPublicKey, responseWrapper.getCopy());
                    response.getOutputStream().write(encrData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else if(method.equals("POST")){
            try{
                byte[] encrDataBytes = filterService.readEncrData(request);
                byte[] decrDataBytes = filterService.decryptData(encrDataBytes);
                RequestWrapper requestWrapper = filterService.getRequestWrapper(request, decrDataBytes);

                PublicKey userPublicKey = filterService.decodeUPK(jsonUserPublicKey);
                int userID = filterService.createUser(userPublicKey);
                requestWrapper.setAttribute("userID", userID);

                filterChain.doFilter(requestWrapper, servletResponse);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (method.equals("OPTIONS")){
            filterChain.doFilter(servletRequest, servletResponse);
        }else{
            ((HttpServletResponse) servletResponse).sendError(403);
        }
    }

    @Override
    public void destroy() {

    }
}
