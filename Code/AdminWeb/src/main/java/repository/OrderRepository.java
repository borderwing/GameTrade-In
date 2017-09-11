package repository;

import model.TradeOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface OrderRepository extends JpaRepository<TradeOrderEntity,Integer> {
}
