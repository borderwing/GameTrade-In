package com.example.ye.gametrade_in.TestedBean;

import java.util.Map;

/**
 * Created by ye on 2017/7/7.
 */

/* [
    {
        "points": 122,
        "status": 1,
        "pair": {
            "gameId": 1,
            "userId": 1,
            "createTime": 1499331267000
        }
    }
  ]
*/

public class MyListBean {

    public Integer points;
    public Integer status;
    public Pair pair;

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
        public Integer gameId;
        public Integer userId;
        public Integer CreateTime;
    }


    @Override
    public String toString(){
        return "points"+points+"status"+status;
    }
}
