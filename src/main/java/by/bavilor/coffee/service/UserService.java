package by.bavilor.coffee.service;

import by.bavilor.coffee.crypto.Decrypt;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.entity.User;
import by.bavilor.coffee.repository.UserRepository;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bosak on 4/28/2018.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Decrypt decrypt;
    @Autowired
    private OrderService orderService;

    public UserService(){}

    public int createUser(PublicKey publicKey){
        User u = new User(publicKeyToString(publicKey));
        userRepository.save(u);
        return u.getId();
    }

    //Register new order
    public void registerOrder(List<Order> orderList, int userID){
        User user = getUserByID(userID);
        for(Order order : orderList){
            order.setUser(user);
            user.getOrders().add(order);
        }
        userRepository.save(user);
    }

    //Convert a key to string for database
    private String publicKeyToString(Key key){
        String buffer = "";
        byte[] keyBytes = Base64.encode(key.getEncoded());
        for(byte b : keyBytes) {
            buffer += (char) b;
        }
        return buffer;
    }

    //Restore a public key from string
    private PublicKey stringToPublicKey(String stringPublicKey) throws Exception{
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                Base64.decode(
                        stringPublicKey.getBytes()));
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    //Return user
    private User getUserByID(int userID){
        List<User> users = userRepository.findAll();
        for(User u : users){
            if(u.getId() == userID){
                return u;
            }
        }
        return null;
    }

    //Return users
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }











    /*


    //Send order
    public List<Order> getOrder(String session) throws Exception{
        if(user == null) {
            System.out.println("Can't find the user");
            return null;
        }else{
            return user.getOrders();
        }
    }

    //Order to json
    public String orderToJson(String session) throws Exception{
        List<Order> list = new ArrayList<Order>();
        for(Order order : getOrder(session)){
            list.add(new Order(order.getName(), order.getPrice(), order.getAmount()));
        }
        return new Gson().toJson(list);
    }

    //Update order
    public void updateOrder(List<Order> list,String session){
        User user = getUserBySession(session);
        for(int i = 0; i < user.getOrders().size(); i++){
            //set a all value from list
            if(list.get(i).getAmount() == user.getOrders().get(i).getAmount()){}
            else {
                user.getOrders().get(i).setAmount(list.get(i).getAmount());
            }
        }

        //delete records with 0 amount
        List<Order> notNullAmount = new ArrayList<>();
        List<Order> nullAmount = new ArrayList<>();
        for(int j = 0; j < user.getOrders().size(); j++){
            if(user.getOrders().get(j).getAmount() > 0){
                notNullAmount.add(user.getOrders().get(j));
            }else{
                nullAmount.add(user.getOrders().get(j));
            }
        }
        user.setOrders(notNullAmount);
        userRepository.save(user);

        if(nullAmount.size() > 0){
            for(Order o : nullAmount){
                orderService.deleteOrder(o);
            }
        }
    }

    //Check client app
    public boolean checkClientApp(String session, String typeApp) {
        for(User u: getListOfUsers()){
            if(u.getSessionID().equals(session)){
                if(u.getClient().equals(typeApp)){
                    return true;
                }
            }
        }
        return false;
    }*/
}
