package com.bankrupted.tradein.assist;

import com.bankrupted.tradein.model.OfferEntity;
import org.python.antlr.op.Del;

import java.util.*;

/**
 * Created by homepppp on 2017/7/13.
 */
public class matchAssist {


    //get game string to game id list
    //from"gameid1,gameid2,gameid3..." to [gameid1,gameid2,gameid3...]
    public List<Long> getGameIdList(String gameString){
        List<Long> gameIdList=new ArrayList<>();

        String[] splitString=gameString.split(",");
        for(int i =0;i<splitString.length;i++){
            Long gameid=new Long(splitString[i]);
            gameIdList.add(gameid);
        }
        return gameIdList;
    }

    //get the availPerson
    //return map<userid,points>
    public Map<Integer,Integer> getAvailablePerson(List<OfferEntity> offerList,List<Long> youWantList){
        Map<Integer,Integer> userAppear=new HashMap<>();
        Map<Integer,Integer> userPoints=new HashMap<>();
        Iterator<OfferEntity> iter=offerList.iterator();
        while(iter.hasNext()){
            OfferEntity offer=iter.next();
            if(offer.getStatus()==1){
                if(youWantList.contains(offer.getGame().getGameId())) {
                    int points = offer.getPoints();
                    int userid = offer.getUser().getUserId();
                    if (userAppear.containsKey(userid)) {
                        int times = userAppear.get(userid);
                        int oldPoints=userPoints.get(userid);
                        userAppear.put(userid, times+1);
                        userPoints.put(userid,points+oldPoints);
                    } else {
                        userAppear.put(userid, 1);
                        userPoints.put(userid,points);
                    }
                }
            }
        }
        Iterator<Map.Entry<Integer,Integer>> DeleteUncontainUserIter=userAppear.entrySet().iterator();
        while(DeleteUncontainUserIter.hasNext()){
            Map.Entry<Integer,Integer> entry=DeleteUncontainUserIter.next();
            if(entry.getValue()<3){
                DeleteUncontainUserIter.remove();
                userPoints.remove(entry.getKey());
            }
        }
        return userPoints;
    }

}
