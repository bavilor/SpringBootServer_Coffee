package by.bavilor.coffee.repository;

import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by bosak on 5/10/2018.
 */
public interface OrderRepository extends JpaRepository<Order, Integer>{

}
