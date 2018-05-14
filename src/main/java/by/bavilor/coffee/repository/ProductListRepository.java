package by.bavilor.coffee.repository;

import by.bavilor.coffee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by bosak on 4/19/2018.
 */
@Repository
public interface ProductListRepository extends JpaRepository<Product, Integer> {

}
