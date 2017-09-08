package repository;

import model.TradeGameEntity;
import model.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TradeGameRepository extends JpaRepository<TradeGameEntity,Integer> {
}
