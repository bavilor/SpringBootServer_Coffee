package by.bavilor.coffee.controller;


import by.bavilor.coffee.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



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

    //Delete users
    @RequestMapping(value = "/deleteUsers", method = RequestMethod.POST)
    public void deleteUsers(@RequestBody byte[] userPublicKeysBytes){
        requestService.deleteUsers(userPublicKeysBytes);
    }

    //Update order
    @RequestMapping(value = "/updateOrder", method = RequestMethod.POST)
    public void updateOrder(@RequestBody String orderBytes, HttpServletRequest request) throws Exception{
        System.out.println("Try to update order");
        int userID = (int) request.getAttribute("userID");
        requestService.updateOrder(orderBytes, userID);
    }
}
