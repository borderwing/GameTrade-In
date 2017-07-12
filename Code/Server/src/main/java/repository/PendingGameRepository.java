package repository;

import model.PendingGameEntity;
import model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Created by homepppp on 2017/6/30.
 */

@Repository
public interface PendingGameRepository extends JpaRepository<PendingGameEntity,Integer>{
    @Query("select p from PendingGameEntity p where p.reviewer=null and p.status=0")
    Page<PendingGameEntity> findNoReviewerPendingGame(Pageable pageable);

    @Modifying
    @Transactional
    @Query("update PendingGameEntity p set p.status=2 where p.pendingGamesId=:id")
    void pendingFailure(@Param("id")int pendingGameId);

    @Modifying
    @Transactional
    @Query("update PendingGameEntity p set p.reviewer=:reviewer where p.pendingGamesId=:id")
    void SetReviewer(@Param("id")int pendingGameId, @Param("reviewer")UserEntity admin);

    @Modifying
    @Transactional
    @Query("update PendingGameEntity p set p.status=1 where p.pendingGamesId=:id")
    void pendingSuccess(@Param("id")int pendingGameId);
}
