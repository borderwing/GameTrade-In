package model.temporaryItem;

import com.fasterxml.jackson.annotation.JsonIgnore;

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


    @JsonIgnore
    private int userStatus=0;

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

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
