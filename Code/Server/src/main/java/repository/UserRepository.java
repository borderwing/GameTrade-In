package repository;

import model.UserEntity;
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

    @Query("SELECT p FROM UserEntity p JOIN FETCH p.addresses WHERE p.userId = (:id)")
    UserEntity findByUserIdAndFetchAddresses(@Param("id") int userId);

    @Query("SELECT p FROM UserEntity p where p.role=0")
    List<UserEntity> findNormalUsers();

    @Query("select max(p.userId) from UserEntity p")
    int getMaxId();
}
