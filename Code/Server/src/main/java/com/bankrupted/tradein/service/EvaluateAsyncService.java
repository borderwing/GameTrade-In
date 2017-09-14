package com.bankrupted.tradein.service;

import com.bankrupted.tradein.model.GameEntity;
import com.bankrupted.tradein.model.json.game.GameDetailJson;
import com.bankrupted.tradein.model.json.game.GameReleaseJson;
import com.bankrupted.tradein.repository.GameRepository;
import com.bankrupted.tradein.script.pythonGetEvaluatePoint;
import com.bankrupted.tradein.utility.IgdbUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by lykav on 9/12/2017.
 */
@Service
public class EvaluateAsyncService {
    private final Logger logger = LoggerFactory.getLogger(IgdbUtility.class);

    @Autowired
    GameRepository gameRepository;



    @Async
    void deferredSetPoints(String title, String platform, Long gameId){

        logger.info("Evaluating: " + title + "@" + platform);

        pythonGetEvaluatePoint evaluate = new pythonGetEvaluatePoint();
        String point = evaluate.getPoints(title, platform);


        float floatPoint=Float.parseFloat(point)*100;
        int finalPoint = (int)floatPoint;

        gameRepository.updateGameEvaluatePoint(finalPoint, gameId);
    }
}
