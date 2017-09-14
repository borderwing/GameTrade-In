package com.example.ye.gametrade_in.Bean;

/**
 * Created by ye on 2017/7/7.
 */

public class WishBean {

    private Integer points;
    private Integer status;

    private GameBean game;
    private Pair pair;

    public GameBean getGame() {
        return game;
    }

    public void setGame(GameBean game) {
        this.game = game;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Pair getPair() {
        return pair;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public static class Pair{
        private Integer gameId;
        private Integer userId;
        private Integer CreateTime;

        public Integer getGameId() {
            return gameId;
        }

        public void setGameId(Integer gameId) {
            this.gameId = gameId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(Integer createTime) {
            CreateTime = createTime;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "gameId=" + gameId +
                    ", userId=" + userId +
                    ", CreateTime=" + CreateTime +
                    '}';
        }

    }


    @Override
    public String toString(){
        return "points"+points+"status"+status;
    }
}
