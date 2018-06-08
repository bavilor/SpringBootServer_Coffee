package by.bavilor.coffee.service;

import by.bavilor.coffee.controller.CryptoController;
import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.entity.User;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.*;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * Created by bosak on 5/11/2018.
 */
@Service
public class RequestService {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CryptoController cryptoController;
    @Autowired
    private KeyGen keyGen;

    //Get json product list
    public byte[] getProductList(){
        return (new Gson().toJson(productService.getListOfProducts())).getBytes();
    }

    //Return server public key
    public byte[] getServerPublicKey() throws Exception{
        return cryptoController.getServerPublicKey();
    }

    //Register new order
    public void registerOrder(String orderBytes, int userID) throws Exception{
        List<Order> order = Arrays.asList(new Gson().fromJson(orderBytes, Order[].class));
        userService.registerOrder(order, userID);
    }

    //Return orders list bytes
    public byte[] getOrderListBytes() throws Exception{
        List<User> users = userService.getAllUsers();
        List<Order> list;
        Map<String, List<Order>> map = new HashMap<String, List<Order>>();
        Gson g = new Gson();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for(User u : users){
            list = new ArrayList<Order>();
            for(Order o : u.getOrders()){
                list.add(new Order(o.getName(), o.getPrice(), o.getAmount()));
            }
            map.put(u.getPublicRSAKey(), list);
        }
        baos.write(g.toJson(map).getBytes());
        byte[] orders = baos.toByteArray();
        baos.close();
        return orders;
    }

    //Delete users
    public void deleteUsers(byte[] userPublicKeysBytes){
        String userPublicKeysString = getStringFromBytes(userPublicKeysBytes);
        String[] privateKeys = new Gson().fromJson(userPublicKeysString, String[].class);

        userService.deleteUsers(privateKeys);
    }

    //Update order
    public void updateOrder(String orderBytes, int userID){
        User u = userService.getUserByID(userID);
        if(u != null){
            userService.updateOrder(u, orderBytes);
        }else{
            System.out.println("User doesn't exist");
        }

    }

    //Convert byte to string
    private String getStringFromBytes(byte[] data){
        String s = "";
        for (byte b : data){
            s += (char) b;
        }
        return s;
    }

    //Use to separate response on key bytes and data bytes
    private byte[] getByteArray(int start, int end, byte[] data){
        int numOfIter = end - start;
        byte[] bytes = new byte[numOfIter];

        for(int i = 0; i < numOfIter; i++){
            bytes[i] = data[i + start];
        }
        return bytes;
    }
}
