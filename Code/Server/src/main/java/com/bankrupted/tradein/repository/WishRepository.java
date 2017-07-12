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

    @Query("select p from WishEntity p where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game")
    List<WishEntity> findByUserAndGame(@Param("user")UserEntity user, @Param("game")GameEntity game);


    @Query("select p from WishEntity p where p.wishEntityPK.user.userId=:userid and p.wishEntityPK.game.gameId=:gameid")
    List<WishEntity> findByUserIDAndGameID(@Param("userid")int userid,@Param("gameid")int gameid);

    @Query("select p from WishEntity p where p.points=:points and p.status=1 and p.wishEntityPK.game.gameId=:gameid order by p.wishEntityPK.user.userId")
    List<WishEntity> getWishGame(@Param("points")int points,@Param("gameid")int gameid);

    @Query("select wish.wishEntityPK.game.gameId from WishEntity wish, OfferEntity offer where wish.wishEntityPK.user.userId=:WishUserid" +
            " and offer.offerEntityPK.user.userId=:OfferUserid and wish.wishEntityPK.game.gameId=offer.offerEntityPK.game.gameId" +
            " and offer.status=1 and wish.status=1 and offer.points=:points and wish.points=:points")
    List<Integer> getSameGame(@Param("WishUserid")int wishUserid,@Param("OfferUserid")int offerUserid,@Param("points")int points);

    /*@Query("select offerA.offerEntityPK.user.userId as userAId,offerB.offerEntityPK.user.userId as userBId,offerA.offerEntityPK.game.gameId" +
            " as UserASendGameId,offerB.offerEntityPK.game.gameId as UserBSendGameId from WishEntity wishA,WishEntity wishB,OfferEntity offerA," +
            "OfferEntity offerB where wishA.wishEntityPK.user.userId=offerA.offerEntityPK.user.userId and wishB.wishEntityPK.user.userId=" +
            "offerB.offerEntityPK.user.userId and wishA.wishEntityPK.game.gameId=offerB.offerEntityPK.game.gameId and" +
            " wishB.wishEntityPK.game.gameId=offerA.offerEntityPK.game.gameId and wishB.points=offerA.points and wishA.points=offerB.points" +
            " and wishA.status=1 and wishB.status=1 and offerB.status=1 and offerA.status=1 and offerB.offerEntityPK.user.userId<>offerA.offerEntityPK.user.userId" +
            " and offerA.offerEntityPK.user.userId>offerB.offerEntityPK.user.userId")
    List<PotentialChangesItem> getPotientialChanges();*/

    @Query(value = "select offerA.userID as userAId,offerA.gameID as UserASendGameId,offerB.userID as UserBId,offerB.gameID as UserBSendGameId\n" +
            "from offers as offerA,offers as offerB,wishes as wishA,wishes as wishB\n" +
            "where offerA.userID=wishA.userID and wishB.userID=offerB.userID and wishA.gameID=offerB.gameID\n" +
            "\tand wishB.gameID=offerA.gameID and wishB.points=offerA.points and wishA.points=offerB.points\n" +
            "\tand wishA.status=1 and wishB.status=1 and offerA.status=1 and offerB.status=1 and offerA.userID>offerB.userID",nativeQuery = true)
    List<Object[]> getPotientialChanges();

    @Modifying
    @Transactional
    @Query("update WishEntity p set p.status=0 where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game and p.wishEntityPK.createTime=:createTime")
    int deleteWishGame(@Param("user")UserEntity user,@Param("game")GameEntity game,@Param("createTime")Timestamp createTime);

    @Modifying
    @Transactional
    @Query("update WishEntity p set p.points=:points where p.wishEntityPK.user=:user and p.wishEntityPK.game=:game and p.wishEntityPK.createTime=:createTime")
    int modifyWishGame(@Param("user")UserEntity user,@Param("game")GameEntity game,@Param("createTime")Timestamp createTime,@Param("points")int points);
}