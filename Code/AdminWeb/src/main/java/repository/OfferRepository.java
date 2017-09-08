package repository;

import model.GameEntity;
import model.OfferEntity;
import model.OfferEntityPK;
import model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public interface OfferRepository extends JpaRepository<OfferEntity, OfferEntityPK> {

    @Query("select p from OfferEntity p where p.offerEntityPK.user=:user and p.status=1")
    List<OfferEntity> findByUser(@Param("user") UserEntity user);

    @Modifying
    @Transactional
    @Query("update OfferEntity p set p.status=0 where p.offerEntityPK.game.gameId=:gameId and p.offerEntityPK.user.userId=:userId and p.status=1")
    int deleteByOfferPK(@Param("gameId")Long gameId, @Param("userId")int userId);
}
