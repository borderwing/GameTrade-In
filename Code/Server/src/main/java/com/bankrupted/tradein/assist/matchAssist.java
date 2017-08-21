package com.bankrupted.tradein.assist;

import com.bankrupted.tradein.model.OfferEntity;
import org.python.antlr.op.Del;
import org.python.antlr.op.In;

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


    //get the user who owns the game you want and match the points
    public List<Integer> getUsersOwnsWantingGames(List<OfferEntity> OfferWantedGameList,List<Long> YouWantGameList,List<Integer> OfferRange){
        Map<Integer,Integer> AvailablePersonMap=getAvailablePerson(OfferWantedGameList,YouWantGameList);

        List<Integer> targetUserId=new ArrayList<>();
        Iterator<Map.Entry<Integer,Integer>> iter=AvailablePersonMap.entrySet().iterator();

        while(iter.hasNext()){
            Map.Entry<Integer,Integer> offerPoints=iter.next();
            int points=offerPoints.getValue();
            if(points>OfferRange.get(0) && points<OfferRange.get(1)){
                targetUserId.add(offerPoints.getKey());
            }
        }
        return targetUserId;
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

    public boolean checkDuplicated(long num1,long num2,long num3){
        if(num1==num2 || num1==num3 || num2==num3){
            return false;
        }
        else
            return true;
    }

    public boolean checkOrderDuplicated(List<List<Long>> fatherList,List<Long> son){
        Iterator<List<Long>> iter=fatherList.iterator();
        Long first=son.get(0);
        Long second=son.get(1);
        Long third=son.get(2);
        while(iter.hasNext()){
            List<Long> temp=iter.next();
            if(temp.contains(first) && temp.contains(second) && temp.contains(third)){
                return false;
            }
        }
        return true;
    }

    //get Potential changes
    public List<String> getOfferGames(Map<Long,Integer> userOfferPoints,Map<Long,Integer>TargetWishPoints,int minpoint,int maxpoint){
        //change the map to ensure all the games are available in target's map
        Iterator<Map.Entry<Long,Integer>> Offeriter=userOfferPoints.entrySet().iterator();
        while(Offeriter.hasNext()){
            Map.Entry<Long,Integer> OfferItem=Offeriter.next();
            if(!TargetWishPoints.containsKey(OfferItem.getKey())){
                Offeriter.remove();
            }
        }
        Iterator<Map.Entry<Long,Integer>> WishIter=TargetWishPoints.entrySet().iterator();
        while(WishIter.hasNext()){
            Map.Entry<Long,Integer> WishItem=WishIter.next();
            if(!userOfferPoints.containsKey(WishItem.getKey())){
                WishIter.remove();
            }
        }

        //get a evaluate number for a certain point
        //all the setting points in wish list and offer list should divided by the point range
        int divide=maxpoint-minpoint;
        int target=maxpoint/divide;


        Iterator<Map.Entry<Long,Integer>> iter= TargetWishPoints.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<Long,Integer> item=iter.next();
            item.setValue(item.getValue()/divide);
        }
        List<List<Long>> resultLong=new ArrayList<>();
        List<String> result=new ArrayList<>();

        //get one game in return
        Iterator<Map.Entry<Long,Integer>> OneGameIterator=TargetWishPoints.entrySet().iterator();
        while(OneGameIterator.hasNext()){
            Map.Entry<Long,Integer> WishItem=OneGameIterator.next();
            if((WishItem.getValue()==target)||(WishItem.getValue()==(target-1))){
                List<Long> temp=new ArrayList<>();
                temp.add(WishItem.getKey());
                resultLong.add(temp);
                result.add(Long.toString(WishItem.getKey()));
            }
        }

        //get two game in return
        HashMap<Integer,Long> map=new HashMap<Integer,Long>();
        Iterator<Map.Entry<Long,Integer>> TwoIter= TargetWishPoints.entrySet().iterator();
        while(TwoIter.hasNext()){
            Map.Entry<Long,Integer> item=TwoIter.next();
            if(map.containsKey(target-item.getValue())){
                List<Long> temp=new ArrayList<>();
                temp.add(map.get(target-item.getValue()));
                temp.add(item.getKey());
                resultLong.add(temp);
                result.add(Long.toString(map.get(target-item.getValue()))+","+Long.toString(item.getKey()));
            }
            else if(map.containsKey(target-item.getValue()-1)){
                List<Long> temp=new ArrayList<>();
                temp.add(map.get(target-item.getValue()-1));
                temp.add(item.getKey());
                resultLong.add(temp);
                result.add(Long.toString(map.get(target-item.getValue()-1))+","+Long.toString(item.getKey()));
            }
            else{
                map.put(item.getValue(),item.getKey());
            }
        }

        //get three game in return
        System.out.println(TargetWishPoints);
        Iterator<Map.Entry<Long,Integer>> ThreeIter=TargetWishPoints.entrySet().iterator();
        while(ThreeIter.hasNext()){
            Map.Entry<Long,Integer> item=ThreeIter.next();
            int nowTarget=target;
            nowTarget=nowTarget-item.getValue();
            HashMap<Integer,Long> ThreeMap=new HashMap<Integer,Long>();
            Iterator<Map.Entry<Long,Integer>> ThreeIter2=TargetWishPoints.entrySet().iterator();
            while(ThreeIter2.hasNext()){
                Map.Entry<Long,Integer> item2=ThreeIter2.next();
                if(ThreeMap.containsKey(nowTarget-item2.getValue())){
                    if(checkDuplicated(item.getKey(),ThreeMap.get(nowTarget-item2.getValue()),item2.getKey())){
                        List<Long> temp=new ArrayList<>();
                        temp.add(item.getKey());
                        temp.add(ThreeMap.get(nowTarget-item2.getValue()));
                        temp.add(item2.getKey());
                        if(checkOrderDuplicated(resultLong,temp)){
                            resultLong.add(temp);
                            result.add(Long.toString(item.getKey())+","+Long.toString(ThreeMap.get(nowTarget-item2.getValue()))+","+Long.toString(item2.getKey()));
                        }
                    }
                }
                else{
                    ThreeMap.put(item2.getValue(),item2.getKey());
                }
            }
        }
        System.out.println(resultLong);
        return result;
    }


}
