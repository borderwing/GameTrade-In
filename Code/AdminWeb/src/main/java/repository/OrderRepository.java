package repository;

import model.TradeOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<TradeOrderEntity,Integer> {

}
