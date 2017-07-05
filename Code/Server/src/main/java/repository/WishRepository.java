package repository;

import model.GameEntity;
import model.UserEntity;
import model.OfferEntity;
import model.WishEntity;
import model.WishEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;


/**
 * Created by lykav on 2017/6/30.
 */
public interface WishRepository extends JpaRepository<WishEntity, WishEntityPK> {
    WishEntity findByWishEntityPK(WishEntityPK wishEntityPK);

    @Query("select p from WishEntity p where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game")
    List<WishEntity> findByUserAndGame(@Param("user")UserEntity user, @Param("game")GameEntity game);


    @Query("select p from WishEntity p where p.wishEntityPK.user.userId=:userid and p.wishEntityPK.game.gameId=:gameid")
    List<WishEntity> findByUserIDAndGameID(@Param("userid")int userid,@Param("gameid")int gameid);

    @Query("select wish.wishEntityPK.game.gameId from WishEntity wish join fetch OfferEntity offer where wish.wishEntityPK.user.userId=:WishUserid and offer.offerEntityPK.user.userId=:OfferUserid and wish.wishEntityPK.game.gameId=offer.offerEntityPK.game.gameId")
    List<Integer> getSameGame(@Param("WishUserid")int wishUserid,@Param("OfferUserid")int offerUserid);

    @Modifying
    @Transactional
    @Query("update WishEntity p set p.status=0 where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game and p.wishEntityPK.createTime=:createTime")
    int deleteWishGame(@Param("user")UserEntity user,@Param("game")GameEntity game,@Param("createTime")Timestamp createTime);

    @Modifying
    @Transactional
    @Query("update WishEntity p set p.points=:points where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game and p.wishEntityPK.createTime=:createTime")
    int modifyWishGame(@Param("user")UserEntity user,@Param("game")GameEntity game,@Param("createTime")Timestamp createTime,@Param("points")int points);
}
