package com.bankrupted.tradein.model.json.igdb;

import java.util.List;

/**
 * Created by lykav on 7/18/2017.
 */
public class IgdbPlatform {
    private int id;
    private String name;
    private IgdbImage logo;

    private List<Long> games;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IgdbImage getLogo() {
        return logo;
    }

    public void setLogo(IgdbImage logo) {
        this.logo = logo;
    }

    public List<Long> getGames() {
        return games;
    }

    public void setGames(List<Long> games) {
        this.games = games;
    }
}
