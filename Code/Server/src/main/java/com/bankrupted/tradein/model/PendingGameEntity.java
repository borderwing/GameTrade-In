package com.bankrupted.tradein.model;

import javax.persistence.*;

/**
 * Created by homepppp on 2017/6/28.
 */
@Entity
@Table(name = "pendinggames", catalog = "")
public class PendingGameEntity {

    private int pendingGamesId;
    private String title;
    private String platform;
    private String language;
    private String genre;
    private Integer status;

    private UserEntity proposer;
    private UserEntity reviewer;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "PendingGamesID", nullable = false)
    public int getPendingGamesId() {
        return pendingGamesId;
    }

    public void setPendingGamesId(int pendingGamesId) {
        this.pendingGamesId = pendingGamesId;
    }

    @Basic
    @Column(name = "title", nullable = true, length = 1024)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "platform", nullable = true, length = 1024)
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Basic
    @Column(name = "language", nullable = true, length = 1024)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Basic
    @Column(name = "genre", nullable = true, length = 1024)
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Basic
    @Column(name = "status", nullable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PendingGameEntity that = (PendingGameEntity) o;

        if (pendingGamesId != that.pendingGamesId) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (platform != null ? !platform.equals(that.platform) : that.platform != null) return false;
        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        if (genre != null ? !genre.equals(that.genre) : that.genre != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pendingGamesId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "proposerID", referencedColumnName = "userID")
    public UserEntity getProposer() {
        return proposer;
    }

    public void setProposer(UserEntity proposer) {
        this.proposer = proposer;
    }

    @ManyToOne
    @JoinColumn(name = "reviewerID", referencedColumnName = "userID")
    public UserEntity getReviewer() {
        return reviewer;
    }

    public void setReviewer(UserEntity reviewer) {
        this.reviewer = reviewer;
    }
}
