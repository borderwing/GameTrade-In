package repository;

import model.PendingGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Created by homepppp on 2017/6/30.
 */

@Repository
public interface PendingGameRepository extends JpaRepository<PendingGameEntity,Integer>{
    List<PendingGameEntity> findByTitle(String title);
}
