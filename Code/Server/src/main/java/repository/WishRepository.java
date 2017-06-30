package repository;

import model.UserEntity;
import model.WishEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lykav on 2017/6/30.
 */
public interface WishRepository extends JpaRepository<UserEntity, WishEntityPK> {

}
