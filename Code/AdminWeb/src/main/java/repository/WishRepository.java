package repository;

import model.WishEntity;
import model.WishEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WishRepository extends JpaRepository<WishEntity, WishEntityPK>{
    @Query("select p from WishEntity p where p.wishEntityPK.user.userId=:userId and p.status=1")
    List<WishEntity> findByUserId(@Param("userId")int userId);

    @Modifying
    @Transactional
    @Query("update WishEntity p set p.status=0 where p.wishEntityPK.game.gameId=:gameId and p.wishEntityPK.user.userId=:userId and p.status=1")
    int deleteByWishPK(@Param("gameId")Long gameId, @Param("userId")int userId);
}
