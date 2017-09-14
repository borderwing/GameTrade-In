package com.bankrupted.tradein.repository;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.model.WishEntity;
import com.bankrupted.tradein.model.WishEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;


/**
 * Created by lykav on 2017/6/30.
 */
@Repository
public interface WishRepository extends JpaRepository<WishEntity, WishEntityPK> {
    WishEntity findByWishEntityPK(WishEntityPK wishEntityPK);

    @Query("select p from WishEntity p where p.wishEntityPK.user.userId=:userId and p.status=1 order by p.wishEntityPK.createTime desc")
    List<WishEntity> findByUserId(@Param("userId")int userId);

    @Query("select p from WishEntity p where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game")
    List<WishEntity> findByUserAndGame(@Param("user")UserEntity user, @Param("game")GameEntity game);


    @Query("select p from WishEntity p where p.wishEntityPK.user.userId=:userid and p.wishEntityPK.game.gameId=:gameid")
    List<WishEntity> findByUserIDAndGameID(@Param("userid")int userid,@Param("gameid")long gameid);

    @Query("select p from WishEntity p where p.points=:points and p.status=1 and p.wishEntityPK.game.gameId=:gameid order by p.wishEntityPK.user.userId")
    List<WishEntity> getWishGame(@Param("points")int points,@Param("gameid")long gameid);

//abs(offer.points-:points) <:scale and abs(wish.points-:points)<:scale
    @Query("select wish.wishEntityPK.game.gameId from WishEntity wish, OfferEntity offer where wish.wishEntityPK.user.userId=:WishUserid" +
            " and offer.offerEntityPK.user.userId=:OfferUserid and wish.wishEntityPK.game.gameId=offer.offerEntityPK.game.gameId" +
            " and offer.status=1 and wish.status=1 and abs(offer.points-:points) <:scale and abs(wish.points-:points)<:scale")
    List<Long> getSameGame(@Param("WishUserid")int wishUserid,@Param("OfferUserid")int offerUserid,@Param("points")int points,@Param("scale")int scale);


    @Query(value = "select offerA.userID as userAId,offerA.gameID as UserASendGameId,offerB.userID as UserBId,offerB.gameID as UserBSendGameId\n" +
            "from offers as offerA,offers as offerB,wishes as wishA,wishes as wishB\n" +
            "where offerA.userID=wishA.userID and wishB.userID=offerB.userID and wishA.gameID=offerB.gameID\n" +
            "\tand wishB.gameID=offerA.gameID and abs(wishB.points-offerA.points)<=?1 and abs(wishA.points-offerB.points)<=?1\n" +
            "\tand wishA.status=1 and wishB.status=1 and offerA.status=1 and offerB.status=1 and offerA.userID>offerB.userID order by abs(wishB.points-offerA.points) desc" +
            "limit 0,10",nativeQuery = true)
    List<Object[]> getPotientialChanges(int scale);

    @Query("select p.points from WishEntity p where p.wishEntityPK.user.userId=:userid and p.wishEntityPK.game.gameId=:gameid and p.status=1")
    Integer getWishPoints(@Param("userid")int userid,@Param("gameid")long gameid);

    @Modifying
    @Transactional
    @Query("update WishEntity p set p.status=0 where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game and p.wishEntityPK.createTime=:createTime")
    int deleteWishGame(@Param("user")UserEntity user,@Param("game")GameEntity game,@Param("createTime")Timestamp createTime);

    @Modifying
    @Transactional
    @Query("update WishEntity p set p.points=:points where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game and p.wishEntityPK.createTime=:createTime")
    int modifyWishGame(@Param("user")UserEntity user,@Param("game")GameEntity game,@Param("createTime")Timestamp createTime,@Param("points")int points);

    @Modifying
    @Transactional
    @Query("update WishEntity p set p.status=0 where p.wishEntityPK.user.userId=:userid and p.wishEntityPK.game.gameId=:gameid and p.status=1")
    int deleteWish(@Param("userid")int userid,@Param("gameid")long gameid);
}
