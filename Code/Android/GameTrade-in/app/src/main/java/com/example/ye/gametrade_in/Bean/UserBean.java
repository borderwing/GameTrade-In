package com.example.ye.gametrade_in.Bean;

/**
 * Created by lykav on 9/12/2017.
 */

public class UserBean {
    private Long userId;
    private String username;
    private String phone;
    private Integer rating;
    private Integer ratingUserNum;
    private Integer points;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRatingUserNum() {
        return ratingUserNum;
    }

    public void setRatingUserNum(Integer ratingUserNum) {
        this.ratingUserNum = ratingUserNum;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserBean userBean = (UserBean) o;

        return userId.equals(userBean.userId);

    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
