package by.bavilor.coffee.controller;

import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.service.ProductService;
import by.bavilor.coffee.service.RequestService;
import by.bavilor.coffee.service.UserService;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.hibernate.boot.jaxb.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Created by bosak on 4/19/2018.
 */
@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    //Response server public key
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public @ResponseBody byte[] getPublicKey(){
        return requestService.getServerPublicKey();
    }

    //Request public key & Response user session
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public @ResponseBody String setUserPublicKey(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonPublicKey) throws Exception{
        String session = request.getSession().getId();
        requestService.addUser(jsonPublicKey, session);
        try{
            return requestService.getEncrSessionID(session);
        }catch(Exception e){
            System.out.println("Can't encrypt the session. RequestController/setUserPublicKey: ");
            response.setStatus(403);
            e.printStackTrace();
            return null;
        }
    }

    //Response price list
    @RequestMapping(value = "/getProducts", method = RequestMethod.GET)
    public @ResponseBody byte[]  getPriceList(HttpServletRequest request) throws Exception{
        System.out.println("Dispatch products.. OK.");
        return Base64.encode(requestService.getPriceList((String) request.getAttribute("decryptedSession")));
    }

    //Order from client
    @RequestMapping(value = "/setOrder", method = RequestMethod.POST)
    public void setOrder(@RequestBody byte[] byte64Data, HttpServletRequest request) throws Exception{
        System.out.println("Registration of a new order..");
        requestService.registerOrder((String) request.getAttribute("decryptedSession"), byte64Data);
    }

    //Order to client
    @RequestMapping(value = "/getOrder", method = RequestMethod.GET)
    public @ResponseBody byte[] getOrder(HttpServletRequest request) throws Exception{
        System.out.println("Send order");
        return Base64.encode(requestService.sendOrder((String) request.getAttribute("decryptedSession")));
    }

    //Update order
    @RequestMapping(value = "/updateOrder", method = RequestMethod.POST)
    public void updateOrder(@RequestBody byte[] byte64Data, HttpServletRequest request) throws Exception{
        System.out.println("Try to update order");
        requestService.updateOrder((String) request.getAttribute("decryptedSession"), byte64Data);
    }
}
