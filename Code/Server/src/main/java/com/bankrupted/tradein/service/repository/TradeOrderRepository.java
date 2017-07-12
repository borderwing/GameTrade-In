package com.bankrupted.tradein.service.repository;

import com.bankrupted.tradein.service.model.TradeOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by homepppp on 2017/7/5.
 */
@Repository
public interface TradeOrderRepository extends JpaRepository<TradeOrderEntity,Integer>{

    @Query("select max(p.tradeOrderId) from TradeOrderEntity p")
    int getMaxId();

    @Modifying
    @Transactional
    @Query("update TradeOrderEntity p set p.status=-1 where p.tradeOrderId=:orderid")
    int cancelOrder(@Param("orderid")int orderid);

    @Modifying
    @Transactional
    @Query("update TradeOrderEntity p set p.status=p.status-1 where p.tradeOrderId=:orderid")
    int confirmOneGame(@Param("orderid")int orderid);
}
