package by.bavilor.coffee.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by bosak on 4/19/2018.
 */
@Entity
@Table(name = "products")
public class Product implements
        Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column
    private String name;
    @Column
    private double price;

    public Product(){}

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    @Override
    public String toString(){
        return "id: " + getId() + " type: " + getName() + " price: " + getPrice();
    }
}
