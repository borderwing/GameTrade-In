package com.example.ye.gametrade_in.Bean;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by lykav on 9/13/2017.
 */

public class TradeConfirmBean {
    private int tradeOrderId;
    private Timestamp createTime;
    private Integer status;
    private List<TradeGameBean> tradeGames;

    public int getTradeOrderId() {
        return tradeOrderId;
    }

    public void setTradeOrderId(int tradeOrderId) {
        this.tradeOrderId = tradeOrderId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<TradeGameBean> getTradeGames() {
        return tradeGames;
    }

    public void setTradeGames(List<TradeGameBean> tradeGames) {
        this.tradeGames = tradeGames;
    }
}
