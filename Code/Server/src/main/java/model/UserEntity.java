package model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name = "users", catalog = "")
public class UserEntity {
    private int userId;
    private String username;
    private String password;
    private Integer role;

    private Collection<AddressEntity> addresses;

    private Collection<WishEntity> wishes;
    private Collection<OfferEntity> offers;

    private Collection<PendingGameEntity> proposedGames;
    private Collection<PendingGameEntity> reviewedGames;

    private Collection<TradeGameEntity> sendingGames;
    private Collection<TradeGameEntity> receivingGames;

    @Id
    @Column(name = "userID", nullable = false)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "username", nullable = true, length = 31)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password", nullable = true, length = 31)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "role", nullable = true)
    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (userId != that.userId) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "user")
    public Collection<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<AddressEntity> _addresses) {
        addresses = _addresses;
    }


    @OneToMany(mappedBy = "wishEntityPK.user")
    public Collection<WishEntity> getWishes() {
        return wishes;
    }

    public void setWishes(Collection<WishEntity> wishes) {
        this.wishes = wishes;
    }

    @OneToMany(mappedBy = "offerEntityPK.user")
    public Collection<OfferEntity> getOffers() {
        return offers;
    }

    public void setOffers(Collection<OfferEntity> offers) {
        this.offers = offers;
    }


    @OneToMany(mappedBy = "proposer")
    public Collection<PendingGameEntity> getProposedGames() {
        return proposedGames;
    }

    public void setProposedGames(Collection<PendingGameEntity> proposedGames) {
        this.proposedGames = proposedGames;
    }

    @OneToMany(mappedBy = "reviewer")
    public Collection<PendingGameEntity> getReviewedGames() {
        return reviewedGames;
    }

    public void setReviewedGames(Collection<PendingGameEntity> reviewedGames) {
        this.reviewedGames = reviewedGames;
    }

    @OneToMany(mappedBy = "sender")
    public Collection<TradeGameEntity> getSendingGames() {
        return sendingGames;
    }

    public void setSendingGames(Collection<TradeGameEntity> sendingGames) {
        this.sendingGames = sendingGames;
    }

    @OneToMany(mappedBy = "receiver")
    public Collection<TradeGameEntity> getTradeInfoReceiveByUserId() {
        return receivingGames;
    }

    public void setTradeInfoReceiveByUserId(Collection<TradeGameEntity> receivingGames) {
        this.receivingGames = receivingGames;
    }
}