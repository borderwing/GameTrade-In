package model.temporaryItem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by homepppp on 2017/7/5.
 */
public class ShowOrderItem {
    private int TradeOrderId;
    private Timestamp time;
    private int status;

    List<ShowOrderGamesItem> gameDetail=new ArrayList<>();

    public int getTradeOrderId() {
        return TradeOrderId;
    }

    public void setTradeOrderId(int tradeOrderId) {
        TradeOrderId = tradeOrderId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ShowOrderGamesItem> getGameDetail() {
        return gameDetail;
    }

    public void setGameDetail(List<ShowOrderGamesItem> gameDetail) {
        this.gameDetail = gameDetail;
    }
}
