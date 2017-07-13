package com.example.ye.gametrade_in;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ye on 2017/7/7.
 */

/* [
    {
        "senderId": 2,
        "getGameId": 4,
        "offerGameId": 5
    },
    {
        "senderId": 2,
        "getGameId": 4,
        "offerGameId": 6
    },
  ]
*/

public class MatchBean implements Serializable{

    public String senderId;
    public String getGameId;
    public String offerGameId;


    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getGetGameId() {
        return getGameId;
    }

    public void setGetGameId(String getGameId) {
        this.getGameId = getGameId;
    }

    public String getOfferGameId() {
        return offerGameId;
    }

    public void setOfferGameId(String offerGameId) {
        this.offerGameId = offerGameId;
    }



    @Override
    public String toString(){
        return "senderId: " + senderId + " getGameId: " + getGameId + " offerGameId: " + offerGameId;
    }
}
