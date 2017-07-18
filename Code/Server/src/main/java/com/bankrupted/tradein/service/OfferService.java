package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.OfferEntity;
import com.bankrupted.tradein.model.UserEntity;
import com.bankrupted.tradein.model.json.ModifyOfferJsonItem;
import com.bankrupted.tradein.model.json.ModifyWishJsonItem;
import com.bankrupted.tradein.model.json.OfferJsonItem;
import com.bankrupted.tradein.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by homepppp on 2017/7/18.
 */
@Service
public class OfferService {

    @Autowired
    OfferRepository offerRepo;

    public Collection<OfferEntity> getAvailableOffer(UserEntity user){
        Collection<OfferEntity> offerlist=user.getOffers();
        //check the availability
        Iterator<OfferEntity> iter=offerlist.iterator();
        while(iter.hasNext()){
            OfferEntity offerGame=iter.next();
            if(offerGame.getStatus()==0){
                iter.remove();
            }
        }
        return offerlist;
    }

    public OfferEntity getOneOfferByUserAndGame(UserEntity user, GameEntity game){
        List<OfferEntity> offerlist=offerRepo.findByUserAndGame(user,game);

        boolean isAvailable=false;
        Iterator<OfferEntity> iter=offerlist.iterator();
        OfferEntity offer=new OfferEntity();
        while(iter.hasNext()){
            offer =iter.next();
            if(offer.getStatus()==1){
                isAvailable=true;
                break;
            }
        }
        if(isAvailable)
            return offer;
        else
            return null;
    }

    public OfferEntity saveOfferInAdd(OfferJsonItem offerGame,UserEntity user,GameEntity game,Timestamp time){
        OfferEntity offer=new OfferEntity();
        offer.setPoints(offerGame.getPoints());
        offer.setStatus(1);
        offer.getOfferEntityPK().setUser(user);
        offer.getOfferEntityPK().setGame(game);
        offer.getOfferEntityPK().setCreateTime(time);
        offerRepo.saveAndFlush(offer);
        return offer;
    }

    public boolean removeOfferItem(UserEntity user,GameEntity game){
        List<OfferEntity> offerlist=offerRepo.findByUserAndGame(user,game);
        boolean isAvailable=false;
        Iterator<OfferEntity> iter=offerlist.iterator();
        while(iter.hasNext()){
            OfferEntity offer=iter.next();
            if(offer.getStatus()==1){
                Timestamp time=offer.getOfferEntityPK().getCreateTime();
                offerRepo.deleteOfferGame(game,user,time);
                isAvailable=true;
            }
        }
        return isAvailable;
    }

    public boolean modifyOfferItem(UserEntity user, GameEntity game, ModifyOfferJsonItem modifyPoints){
        List<OfferEntity> offerList = offerRepo.findByUserAndGame(user,game);
        boolean isAvailable=false;
        Iterator<OfferEntity> iter=offerList.iterator();
        while(iter.hasNext()){
            OfferEntity offer=iter.next();
            if(offer.getStatus()==1){
                Timestamp time=offer.getOfferEntityPK().getCreateTime();
                offerRepo.modifyOfferGame(game,user,time,modifyPoints.getPoints());
                isAvailable=true;
            }
        }
        return isAvailable;
    }
}
