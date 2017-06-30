package repository;

import model.CustomerEntity;
import model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by lykav on 2017/6/29.
 */
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
    @Query("SELECT p FROM CustomerEntity p JOIN FETCH p.addresses WHERE p.userId = (:id)")
    CustomerEntity findByUserIdAndFetchAddresses(@Param("id") int userId);

}
