package model.json;

/**
 * Created by lykav on 2017/6/30.
 */
public class WishJsonItem {
    private int gameId;
    private int points;
    private int status;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
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
}
