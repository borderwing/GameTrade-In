package repository;

import model.TradeGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by homepppp on 2017/7/5.
 */
public interface TradeGameRepository extends JpaRepository<TradeGameEntity,Integer> {

    @Query("select max(p.tradeGameId) from TradeGameEntity p")
    int getMaxId();

    @Query("select p from TradeGameEntity p")
    List<TradeGameEntity> findAllTradeGame();

}
