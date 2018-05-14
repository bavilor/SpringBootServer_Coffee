package by.bavilor.coffee.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bosak on 4/28/2018.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column (length = 5000)
    private String publicRSAKey;
    @Column
    private String sessionID;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public User() {}

    public User(String sessionID, String publicRSAKey) {
        this.sessionID = sessionID;
        this.publicRSAKey = publicRSAKey;
    }

    public String getPublicRSAKey() {
        return publicRSAKey;
    }

    public void setPublicRSAKey(String publicRSAKey) {
        this.publicRSAKey = publicRSAKey;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public int getId() {
        return id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> list){
        orders = list;
    }

}
