package repository;

import model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lykav on 2017/6/29.
 */
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {


}
