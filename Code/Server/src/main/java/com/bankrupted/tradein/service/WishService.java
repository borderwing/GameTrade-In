package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.OfferEntity;
import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.model.WishEntity;
import com.bankrupted.tradein.model.json.wish.ModifyWishJson;
import com.bankrupted.tradein.model.json.wish.CreateWishJson;
import com.bankrupted.tradein.repository.WishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.sql.Timestamp;

/**
 * Created by homepppp on 2017/7/18.
 */
@Service
public class WishService {

    @Autowired
    WishRepository wishRepo;

    public Collection<WishEntity> getAvailableWish(UserEntity user){
        Collection<WishEntity> wishList = user.getWishes();

        //get the available game
        Iterator<WishEntity> iter=wishList.iterator();
        while(iter.hasNext()){
            WishEntity wish=iter.next();
            if(wish.getStatus()==0){
                iter.remove();
            }
        }
        return wishList;
    }

    public List<WishEntity> findByUserAndGame(UserEntity user,GameEntity game){
        return wishRepo.findByUserAndGame(user,game);
    }

    public WishEntity getOneWishByUserAndGame(UserEntity user,GameEntity game){
        List<WishEntity> wishList=wishRepo.findByUserAndGame(user,game);
        boolean isAvailable=false;
        Iterator<WishEntity> iter=wishList.iterator();
        WishEntity wish=new WishEntity();
        while(iter.hasNext()){
            wish =iter.next();
            if(wish.getStatus()==1){
                isAvailable=true;
                break;
            }
        }
        if(isAvailable)
            return wish;
        else
            return null;
    }

    public WishEntity saveWishInAdd(CreateWishJson wishItem, UserEntity user, GameEntity game, Timestamp time){
        WishEntity wish=new WishEntity();
        wish.setPoints(wishItem.getPoints());
        wish.setStatus(1);
        wish.getWishEntityPK().setUser(user);
        wish.getWishEntityPK().setGame(game);
        wish.getWishEntityPK().setCreateTime(time);
        wishRepo.saveAndFlush(wish);
        return wish;
    }

    public boolean removeWishItem(UserEntity user,GameEntity game){
        boolean isAvailable = false;
        List<WishEntity> wishlist = wishRepo.findByUserAndGame(user, game);
        Iterator<WishEntity> iter = wishlist.iterator();
        while (iter.hasNext()) {
            WishEntity wish = iter.next();
            if (wish.getStatus() == 1) {
                Timestamp createTime=wish.getWishEntityPK().getCreateTime();
                wishRepo.deleteWishGame(user,game,createTime);
                isAvailable = true;
            }
        }
        return isAvailable;
    }

    public boolean modifyWishItem(UserEntity user, GameEntity game, ModifyWishJson modifyItem){
        boolean isAvailable=false;
        List<WishEntity> wishlist=wishRepo.findByUserAndGame(user,game);
        Iterator<WishEntity> iter=wishlist.iterator();
        while(iter.hasNext()){
            WishEntity wish=iter.next();
            if(wish.getStatus()==1){
                Timestamp time=wish.getWishEntityPK().getCreateTime();
                wishRepo.modifyWishGame(user,game,time,modifyItem.getPoints());
                isAvailable=true;
            }
        }
        return isAvailable;
    }

    public int getWishPointsByIdAndGame(int userid,long gameid){
        return wishRepo.getWishPoints(userid,gameid);
    }

    public List<WishEntity> findByUserId(int userid){
        return wishRepo.findByUserId(userid);
    }

    public List<Long> getSameGame(int wishUserid,int offerUserid,int points,int scale){
        return wishRepo.getSameGame(wishUserid,offerUserid,points,scale);
    }

    public List<WishEntity> getWishGame(int wantPoint,long gameid){
        return wishRepo.getWishGame(wantPoint,gameid);
    }

    public Map<Long,Integer> getGamePointsById(int userid){
        List<WishEntity> UserOffer=findByUserId(userid);
        Map<Long,Integer> UserOfferPoints=new HashMap<>();
        for(int i =0;i<UserOffer.size();i++){
            UserOfferPoints.put(UserOffer.get(i).getWishEntityPK().getGame().getGameId(),UserOffer.get(i).getPoints());
        }
        return UserOfferPoints;
    }

    public List<Object[]> getPotentialChanges(int scale){
        return wishRepo.getPotientialChanges(scale);
    }
}
