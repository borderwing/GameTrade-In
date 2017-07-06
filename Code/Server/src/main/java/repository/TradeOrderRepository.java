package repository;

import model.TradeOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by homepppp on 2017/7/5.
 */
public interface TradeOrderRepository extends JpaRepository<TradeOrderEntity,Integer>{

    @Query("select max(p.tradeOrderId) from TradeOrderEntity p")
    int getMaxId();

}
