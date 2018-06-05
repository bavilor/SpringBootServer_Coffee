package by.bavilor.coffee.service;

import by.bavilor.coffee.controller.CryptoController;
import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.entity.User;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public byte[] getServerPublicKey(){
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
        Gson g = new Gson();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for(User u : users){
            list = new ArrayList<Order>();
            for(Order o : u.getOrders()){
                list.add(new Order(o.getName(), o.getPrice(), o.getAmount()));
            }
            baos.write(g.toJson(list).getBytes());
        }
        byte[] orders = baos.toByteArray();
        baos.close();
        return orders;
    }







/*    public byte[] getByteOrderList(InputStream inputStream) throws Exception{
        byte[] b64Data = getByteDataFromRequest(inputStream);

    }*/

    /*

    //Set user public key
    public void addUser(String jsonPublicKey, String sessionID) throws Exception{
        try{
            JWK jwk = new Gson().fromJson(jsonPublicKey, JWK.class);
            System.out.println("Web client");
            userService.addUser(jwk.restorePublicKey(), sessionID, "web");
        }catch (Exception e){
            System.out.println("Desktop client");
            userService.addUser(cryptoController.restoreUserPublicKey(jsonPublicKey), sessionID, "desktop");
        }
    }

    //Return encrypted session id
    public String getEncrSessionID(String sessionID) throws Exception{
        return new Gson().toJson(cryptoController.encryptSessionID(sessionID));
    }

    //Return user find by session
    public User getUserBySession(String sessionID){
        return userService.getUserBySession(sessionID);
    }

    //Return user public key by session
    public PublicKey getPublicKeyByUserSession(String session) throws Exception{
        return userService.getPublicKeyBySession(session);
    }

    //Return price list & secret key in one array
    public byte[] getPriceList(String b64UPK) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PublicKey publicKey = cryptoController.restoreUserPublicKey(b64UPK);
        SecretKey secretKey = keyGen.generateSecretKey();

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[16];
        randomSecureRandom.nextBytes(iv);

        byte[] byteKey = cryptoController.encryptSecretKey(publicKey, secretKey);
        byte[] byteIV = cryptoController.encryptIV(publicKey, iv);
        byte[] data = cryptoController.ecnryptList(productService.getJsonProductList(), secretKey, iv);

        outputStream.write(byteKey);
        outputStream.write(byteIV);
        outputStream.write(data);

        return outputStream.toByteArray();
    }



    //Send order
    public byte[] sendOrder(String session) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PublicKey publicKey = userService.getPublicKeyBySession(session);
        SecretKey secretKey = keyGen.generateSecretKey();

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[16];
        randomSecureRandom.nextBytes(iv);

        byte[] byteKey = cryptoController.encryptSecretKey(publicKey, secretKey);
        byte[] byteIV = cryptoController.encryptIV(publicKey, iv);
        byte[] data = cryptoController.ecnryptList(userService.orderToJson(session), secretKey, iv);

        outputStream.write(byteKey);
        outputStream.write(byteIV);
        outputStream.write(data);

        return outputStream.toByteArray();
    }

    //Update order
    public void updateOrder(String session, byte[] byte64Data, HttpServletRequest request) throws Exception{
        byte[] byteData = Base64.decode(byte64Data);
        byte[] sign = getByteArray(byteData.length - 256, byteData.length, byteData);
        PublicKey publicKey;
        String client;
        if(userService.checkClientApp(session, "desktop")){
            System.out.println("Desktop");
            publicKey = userService.getPublicKeyBySession(session);
            client = "desktop";
        }else{
            System.out.println("Web");
            String key = request.getHeader("rsaPssKey");
            publicKey = cryptoController.restorePssKey(Base64.decode(key));
            client = "web";
        }

        if(cryptoController.checkSign(sign, session, publicKey, client)){
            byte[] byteSecretKey = getByteArray(0, 256, byteData);
            byte[] byteIV = cryptoController.decryptIV(getByteArray(256, 512, byteData));
            byte[] byteUpdateOrder = getByteArray(512, byteData.length - 256, byteData);

            SecretKey secretKey = cryptoController.restoreSecretKey(byteSecretKey);
            userService.updateOrder(cryptoController.decryptOrder(byteUpdateOrder, secretKey, byteIV), session);
            System.out.println("OK");
        }else{
            System.out.println("Sign error");
        }

    }

*/
}
