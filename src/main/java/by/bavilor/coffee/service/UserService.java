package by.bavilor.coffee.service;

import by.bavilor.coffee.crypto.Decrypt;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.entity.User;
import by.bavilor.coffee.repository.OrderRepository;
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
import java.util.Arrays;
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
    @Autowired
    private OrderRepository orderRepository;

    public UserService(){}

    //Creating user if it doesn't exist
    public int createUser(PublicKey publicKey){
        User user = findUser(publicKey);
        if(user == null){
            User u = new User(publicKeyToString(publicKey));
            userRepository.save(u);
            return u.getId();
        }else{
            return user.getId();
        }
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

    //Return user
    public User getUserByID(int userID){
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

    //Find the user
    public User findUser(PublicKey publicKey){
        List<User> users = getAllUsers();
        String publicUserKeyString = publicKeyToString(publicKey);
        for(User u : users){
            if(u.getPublicRSAKey().equals(publicUserKeyString)){
                return u;
            }
        }
        return null;
    }

    //Delete users
    public void deleteUsers(String[] usersArray){
        List<User> usersList = userRepository.findAll();

        for(User u : usersList){
            for(String us : usersArray){
                if(u.getPublicRSAKey().equals(us)){
                    List<Order> orders = u.getOrders();
                    for(Order o : orders){
                        orderService.deleteOrder(o);
                    }
                    userRepository.deleteById(u.getId());
                }
            }
        }
    }

    //Update user
    public void updateOrder(User user, String orderBytes){
        Order[] ordersArray = new Gson().fromJson(orderBytes, Order[].class);
        List<Order> orders = Arrays.asList(ordersArray);
        List<Order> ordersFromDB = user.getOrders();

        for(Order o : orders){
            System.out.println(o.getName() + " " + o.getAmount());
        }

        int size = ordersFromDB.size();
        user.setOrders(null);
        userRepository.save(user);

        for(int i = 0; i < size; i++){
            orderRepository.deleteById(ordersFromDB.get(i).getId());
        }

        for(Order o : orders){
            if(o.getAmount() > 0){
                o.setUser(user);
                orderService.addOrder(o);
            }
        }

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

}
