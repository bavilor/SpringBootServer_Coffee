package by.bavilor.coffee.repository;

import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by bosak on 4/28/2018.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
