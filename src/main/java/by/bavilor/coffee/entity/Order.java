package by.bavilor.coffee.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by bosak on 4/24/2018.
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private double price;
    private int amount;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Order(){}

    public Order(String name, double price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString(){
        return "Id: " + getId() + ", type: " + getName() + ", price: " + getPrice() + ", amount: " + getAmount();
    }
}
