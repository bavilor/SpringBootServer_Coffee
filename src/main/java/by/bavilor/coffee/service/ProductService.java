package by.bavilor.coffee.service;

import by.bavilor.coffee.crypto.Encrypt;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.entity.Product;
import by.bavilor.coffee.repository.ProductListRepository;
import com.google.gson.Gson;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * Created by bosak on 4/20/2018.
 */
@Service
public class ProductService {

    @Autowired
    private ProductListRepository productListRepository;

    //Add new product to database
    public boolean addProduct(String name, double price){
        try{
            productListRepository.save(new Product(name, price));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //Delete product from database
    public boolean deleteProduct (int id){
        try{
            productListRepository.deleteById(id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //Print all products to server console
    public boolean printProducts(){
        List<Product> products = getListOfProducts();
        if(products == null) {
            return false;
        }else{
            for(Product p : products){
                System.out.println(p.toString());
            }
            return true;
        }
    }

    //Return a List of products converted to json
    public String getJsonProductList(){
        return new Gson().toJson(getListOfProducts());
    }

    //Get list of products
    private List<Product> getListOfProducts(){
        return productListRepository.findAll();
    }
}
