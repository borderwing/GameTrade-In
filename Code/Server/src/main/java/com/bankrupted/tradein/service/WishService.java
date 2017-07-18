package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.model.WishEntity;
import com.bankrupted.tradein.model.json.ModifyWishJsonItem;
import com.bankrupted.tradein.model.json.WishJsonItem;
import com.bankrupted.tradein.repository.WishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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

    public WishEntity saveWishInAdd(WishJsonItem wishItem,UserEntity user,GameEntity game,Timestamp time){
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

    public boolean modifyWishItem(UserEntity user, GameEntity game, ModifyWishJsonItem modifyItem){
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
}
