package by.bavilor.coffee.controller;

import by.bavilor.coffee.component.JWK;
import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.service.ProductService;
import by.bavilor.coffee.service.RequestService;
import by.bavilor.coffee.service.UserService;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.hibernate.boot.jaxb.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by bosak on 4/19/2018.
 */
@CrossOrigin
@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    //Response server public key
    @RequestMapping(value = "/getServerPublicKey", method = RequestMethod.GET)
    public void getPublicKey(HttpServletResponse response) throws Exception{
        System.out.println("Key was send");
        response.getOutputStream().write(requestService.getServerPublicKey());
    }

    //Response price list
    @RequestMapping(value = "/getProducts", method = RequestMethod.GET)
    public void getPriceList(HttpServletResponse response) throws Exception{
        System.out.println("Dispatch products");
        response.getOutputStream().write(requestService.getProductList());
    }

    //Order from client
    @RequestMapping(value = "/setOrder", method = RequestMethod.POST)
    public void setOrder(@RequestBody String orderBytes, HttpServletRequest request) throws Exception{
        System.out.println("Registration of a new order..");
        int userID = (int) request.getAttribute("userID");
        requestService.registerOrder(orderBytes, userID);
    }

    //Order to client
    @RequestMapping(value = "/getOrder", method = RequestMethod.GET)
    public void getOrder(HttpServletResponse response) throws Exception{
        System.out.println("Send order");
        byte[] byteListOfOrders = requestService.getOrderListBytes();
        response.getOutputStream().write(byteListOfOrders);
    }

    /*
    //Update order
    @RequestMapping(value = "/updateOrder", method = RequestMethod.POST)
    public void updateOrder(@RequestBody byte[] byte64Data, HttpServletRequest request) throws Exception{
        System.out.println("Try to update order");
        requestService.updateOrder((String) request.getAttribute("decryptedSession"), byte64Data, request);
    }*/
}
