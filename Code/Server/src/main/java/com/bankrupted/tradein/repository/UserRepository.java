package com.bankrupted.tradein.repository;

import com.bankrupted.tradein.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lykav on 2017/6/29.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUsername(String username);

    @Query("SELECT p FROM UserEntity p JOIN FETCH p.wishes WHERE p.userId = (:id)")
    UserEntity findByUserIdAndFetchWishlist(@Param("id") int userId);

    @Query("select p from UserEntity p join fetch p.offers where p.userId=:id")
    UserEntity findByUserIdAndFetchOfferlist(@Param("id")int userId);

    @Query("SELECT p FROM UserEntity p JOIN FETCH p.addresses WHERE p.userId = (:id)")
    UserEntity findByUserIdAndFetchAddresses(@Param("id") int userId);

    @Query("SELECT user FROM UserEntity user " +
            "JOIN user.roles role " +
            "WHERE role.name = '" + RoleRepository.customerRoleName + "'")
    List<UserEntity> findNormalUsers();

    @Query("select max(p.userId) from UserEntity p")
    int getMaxId();

    @Query("select p.userId from UserEntity p")
    List<Integer> getAllUserId();
}
