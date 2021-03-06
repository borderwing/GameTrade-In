package com.bankrupted.tradein.repository;

import com.bankrupted.tradein.model.AddressEntity;
import com.bankrupted.tradein.model.TradeGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by homepppp on 2017/7/5.
 */
@Repository
public interface TradeGameRepository extends JpaRepository<TradeGameEntity,Integer> {

    @Query("select max(p.tradeGameId) from TradeGameEntity p")
    int getMaxId();

    @Query("select p from TradeGameEntity p where p.tradeOrder.tradeOrderId=:orderid")
    List<TradeGameEntity> findByOrderId(@Param("orderid")int orderid);

    @Modifying
    @Transactional
    @Query("update TradeGameEntity p set p.toAddress=:address,p.receiverStatus=0,p.status=p.status-1 where p.tradeGameId=:tradeGameId")
    int ConfirmByReceiver(@Param("tradeGameId")int tradeGameId, @Param("address")AddressEntity address);

    @Modifying
    @Transactional
    @Query("update TradeGameEntity p set p.fromAddress=:address,p.senderStatus=0,p.status=p.status-1 where p.tradeGameId=:tradeGameId")
    int ConfirmBySender(@Param("tradeGameId")int tradeGameId,@Param("address")AddressEntity address);

    @Modifying
    @Transactional
    @Query("update TradeGameEntity p set p.senderStatus=2,p.status=-1 where p.tradeGameId=:tradeGameId")
    int RefuseBySender(@Param("tradeGameId")int tradeGameId);

    @Modifying
    @Transactional
    @Query("update TradeGameEntity p set p.receiverStatus=2,p.status=-1 where p.tradeGameId=:tradeGameId")
    int RefuseByReceiver(@Param("tradeGameId")int tradeGameId);

    @Query("select p from TradeGameEntity p where p.game.gameId=:gameId and p.status>0 and p.receiver.userId=:userId")
    List<TradeGameEntity> getDuplicatedOrder(@Param("gameId")Long gameId,@Param("userId")int userId);
}
