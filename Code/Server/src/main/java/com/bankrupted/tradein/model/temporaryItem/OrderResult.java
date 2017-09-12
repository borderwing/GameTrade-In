package com.bankrupted.tradein.model.temporaryItem;

public class OrderResult {
    private String wishGameTitle;
    private String offerGameTitle;
    private int wishPoints;
    private int offerPoints;
    private int status;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
