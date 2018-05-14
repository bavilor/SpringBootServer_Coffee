package by.bavilor.coffee.service;

import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bosak on 5/10/2018.
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void deleteOrder(Order order){
        orderRepository.delete(order);
    }
}
