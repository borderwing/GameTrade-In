package com.bankrupted.tradein.assist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by homepppp on 2017/7/13.
 */
public class matchAssist {


    //get game string to game id list
    //from"gameid1,gameid2,gameid3..." to [gameid1,gameid2,gameid3...]
    public List<Integer> getGameIdList(String gameString){
        List<Integer> gameIdList=new ArrayList<>();

        String[] splitString=gameString.split(",");
        for(int i =0;i<splitString.length;i++){
            Integer gameid=new Integer(splitString[i]);
            gameIdList.add(gameid);
        }
        return gameIdList;
    }
}
