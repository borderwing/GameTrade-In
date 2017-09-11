package repository;

import model.TradeGameEntity;
import model.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TradeGameRepository extends JpaRepository<TradeGameEntity,Integer> {
    @Modifying
    @Transactional
    @Query(value = "update tradegames set status=-1 where trade_gameid=?1",nativeQuery = true)
    int deleteTrade(int id);
}
