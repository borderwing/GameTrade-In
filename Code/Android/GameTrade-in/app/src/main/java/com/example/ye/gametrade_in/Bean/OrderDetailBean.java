package com.example.ye.gametrade_in.Bean;

/**
 * Created by ye on 2017/9/11.
 */

public class OrderDetailBean {
    public String wishGameTitle;
    public String offerGameTitle;
    public int wishPoints;
    public int offerPoints;
    public String status;


    public String getWishGameTitle() {
        return wishGameTitle;
    }

    public void setWishGameTitle(String wishGameTitle) {
        this.wishGameTitle = wishGameTitle;
    }

    public String getOfferGameTitle() {
        return offerGameTitle;
    }

    public void setOfferGameTitle(String offerGameTitle) {
        this.offerGameTitle = offerGameTitle;
    }

    public int getWishPoints() {
        return wishPoints;
    }

    public void setWishPoints(int wishPoints) {
        this.wishPoints = wishPoints;
    }

    public int getOfferPoints() {
        return offerPoints;
    }

    public void setOfferPoints(int offerPoints) {
        this.offerPoints = offerPoints;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
