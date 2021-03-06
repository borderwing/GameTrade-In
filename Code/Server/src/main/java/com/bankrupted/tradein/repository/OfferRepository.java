package com.bankrupted.tradein.repository;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.OfferEntity;
import com.bankrupted.tradein.model.OfferEntityPK;
import com.bankrupted.tradein.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by homepppp on 2017/7/4.
 */
@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, OfferEntityPK> {

    @Query("select p from OfferEntity p where p.offerEntityPK.user=:user and p.offerEntityPK.game=:game order by p.offerEntityPK.createTime desc")
    List<OfferEntity> findByUserAndGame(@Param("user")UserEntity user, @Param("game")GameEntity game);

    @Query("select p from OfferEntity p where abs(p.points-:points)<:scale and p.status=1 and p.offerEntityPK.game.gameId=:gameId order by p.offerEntityPK.user.userId")
    List<OfferEntity> getOfferGame(@Param("points")int points,@Param("gameId")long gameId,@Param("scale")int scale);

    @Query("select wish.wishEntityPK.game.gameId from WishEntity wish, OfferEntity offer where wish.wishEntityPK.user.userId=:WishUserid" +
            " and offer.offerEntityPK.user.userId=:OfferUserid and wish.wishEntityPK.game.gameId=offer.offerEntityPK.game.gameId" +
            " and offer.status=1 and wish.status=1 and offer.points=:points and wish.points=:points")
    List<Long> getSameGame(@Param("WishUserid")int wishUserid,@Param("OfferUserid")int offerUserid,@Param("points")int points);

    @Query("select p from OfferEntity p where p.offerEntityPK.user.userId<>:userId")
    List<OfferEntity> findAllExceptById(@Param("userId")int userId);

    @Query("select p from OfferEntity p where p.offerEntityPK.user.userId=:userId and p.status=1 order by p.offerEntityPK.createTime desc")
    List<OfferEntity> findById(@Param("userId")int userId);

    @Modifying
    @Transactional
    @Query("update OfferEntity p set p.status=0 where p.offerEntityPK.game=:game and p.offerEntityPK.user=:user and p.offerEntityPK.createTime=:createTime")
    int deleteOfferGame(@Param("game")GameEntity game, @Param("user")UserEntity user, @Param("createTime")Timestamp createTime);

    @Modifying
    @Transactional
    @Query("update OfferEntity p set p.points=:points where p.offerEntityPK.createTime=:createTime and p.offerEntityPK.game=:game and p.offerEntityPK.user=:user")
    int modifyOfferGame(@Param("game")GameEntity game,@Param("user")UserEntity user,@Param("createTime")Timestamp createTime,@Param("points")int points);

    @Modifying
    @Transactional
    @Query("update OfferEntity p set p.status=0 where p.offerEntityPK.game.gameId=:gameid and p.offerEntityPK.user.userId=:userid and p.status=1")
    int deleteOffer(@Param("userid")int userid,@Param("gameid")long gameid);
}
