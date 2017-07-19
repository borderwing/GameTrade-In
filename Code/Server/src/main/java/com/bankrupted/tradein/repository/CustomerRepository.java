package com.bankrupted.tradein.repository;

import com.bankrupted.tradein.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by lykav on 2017/6/29.
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
    @Query("SELECT p FROM CustomerEntity p JOIN FETCH p.addresses WHERE p.userId = (:id)")
    CustomerEntity findByUserIdAndFetchAddresses(@Param("id") int userId);

    @Query("select p.rating from CustomerEntity p where p.userId=:userid")
    Integer getRatingById(@Param("userid")int userid);

    @Query("select p.ratingUserNum from CustomerEntity p where p.userId=:userid")
    Integer getRatingUserNumById(@Param("userid")int userid);
}
