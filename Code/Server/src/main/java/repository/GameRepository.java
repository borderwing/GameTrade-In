package repository;

import model.GameEntity;
import model.OfferEntity;
import model.TradeGameEntity;
import model.WishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Collection;

/**
 * Created by homepppp on 2017/6/29.
 */
@Repository
public interface GameRepository extends JpaRepository<GameEntity,Integer>{
    List<GameEntity> findByTitle(String title);

    @Modifying
    @Transactional
    @Query("update GameEntity us set us.title=:qtitle,us.platform=:qplatform,us.evaluatePoint=:qevaluatePoint,us.language=:qlanguage,us.genre=:qgenre,us.wishes=:qwishes,us.offers=:qoffers,us.tradeGames=:qtradeGames where us.gameId=:qgameid")
    void updateGame(@Param("qtitle") String title, @Param("qplatform") String platform, @Param("qlanguage") String language, @Param("qgenre") String genre, @Param("qevaluatePoint") Integer evaluatePoint, @Param("qwishes") Collection<WishEntity> wishes, @Param("qoffers") Collection<OfferEntity> offers, @Param("qtradeGames")Collection<TradeGameEntity> tradeGames,@Param("qgameid") Integer gameId);

}
