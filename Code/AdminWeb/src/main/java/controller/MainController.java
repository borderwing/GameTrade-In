package controller;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import model.OfferEntity;
import model.TradeGameEntity;
import model.UserEntity;
import model.WishEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import repository.OfferRepository;
import repository.TradeGameRepository;
import repository.UserRepository;
import repository.WishRepository;

import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by homepppp on 2017/6/27.
 */
@Controller
public class MainController {

    @Autowired
    UserRepository userRepo;
    @Autowired
    OfferRepository offerRepo;
    @Autowired
    WishRepository wishRepo;
    @Autowired
    TradeGameRepository tradeGameRepo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(ModelMap modelMap) {
        System.out.println("get all users...");
        List<UserEntity> userList= userRepo.findAll();
        modelMap.addAttribute("userList",userList);
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping(value="/index/back",method=RequestMethod.GET)
    public String back(){
        System.out.println("back-------------------------------");
        return "redirect:/home";
    }
    @RequestMapping(value="/home",method=RequestMethod.GET)
    public String getIndex(ModelMap modelMap){
        List<UserEntity> userList= userRepo.findAll();
        modelMap.addAttribute("userList",userList);
        return "index";
    }

    @RequestMapping(value = "/index/offerGames/{userid}",method = RequestMethod.GET)
    public String getOfferGames(@PathVariable("userid") int userId,ModelMap modelMap){
        UserEntity user=userRepo.findOne(userId);
        String userName=user.getUsername();
        List<OfferEntity> offerList=offerRepo.findByUser(user);
        modelMap.addAttribute("offerList",offerList);
        modelMap.addAttribute("userId",userId);
        modelMap.addAttribute("username",userName);
        return "/Offer";
    }

    @RequestMapping(value="/index/{userId}/offerGames/delete/{gameId}",method = RequestMethod.GET)
    public String deleteOffer(@PathVariable("userId")int userId,@PathVariable("gameId")Long gameId, ModelMap modelMap){
        offerRepo.deleteByOfferPK(gameId,userId);
        UserEntity user=userRepo.findOne(userId);
        String userName=user.getUsername();
        List<OfferEntity> offerList=offerRepo.findByUser(user);
        modelMap.addAttribute("offerList",offerList);
        modelMap.addAttribute("userId",userId);
        modelMap.addAttribute("username",userName);
        return "/Offer";
    }

    @RequestMapping(value="/index/wishGames/{userid}",method = RequestMethod.GET)
    public String getWishGames(@PathVariable("userid")int userId,ModelMap modelMap){
        UserEntity user=userRepo.findOne(userId);
        List<WishEntity> wishList=wishRepo.findByUserId(userId);
        modelMap.addAttribute("wishList",wishList);
        modelMap.addAttribute("userId",userId);
        String userName=user.getUsername();
        modelMap.addAttribute("username",userName);
        return "/Wish";
    }

    @RequestMapping(value="/index/{userId}/wishGames/delete/{gameId}",method = RequestMethod.GET)
    public String deleteWish(@PathVariable("userId")int userId,@PathVariable("gameId")Long gameId,ModelMap modelMap){
        wishRepo.deleteByWishPK(gameId,userId);
        UserEntity user=userRepo.findOne(userId);
        String userName=user.getUsername();
        List<WishEntity> wishList=wishRepo.findByUserId(userId);
        modelMap.addAttribute("wishList",wishList);
        modelMap.addAttribute("userId",userId);
        modelMap.addAttribute("username",userName);
        return "/Wish";
    }

    @RequestMapping(value="/index/orders/{userid}",method = RequestMethod.GET)
    public String getOrderGames(@PathVariable("userid")int userId,ModelMap modelMap){
        UserEntity user=userRepo.findOne(userId);
        List<TradeGameEntity> TradeAsReceiver1=tradeGameRepo.findAll();
        List<TradeGameEntity> TradeAsReceiver =new ArrayList<>();
        List<TradeGameEntity> TradeAsSender=new ArrayList<>();
        System.out.println(TradeAsReceiver1.size());
        Iterator<TradeGameEntity> iterGame=TradeAsReceiver1.iterator();
        while(iterGame.hasNext()){
            TradeGameEntity tradeGame=iterGame.next();
            if(tradeGame.getReceiver().getUserId()==userId){
                TradeAsReceiver.add(tradeGame);
            }
        }
        for(int i =0;i<TradeAsReceiver1.size();i++){
            if(TradeAsReceiver1.get(i).getSender().getUserId()==userId){
                TradeAsSender.add(TradeAsReceiver1.get(i));
            }
        }
        modelMap.addAttribute("TradeAsReceiver",TradeAsReceiver);
        modelMap.addAttribute("TradeAsSender",TradeAsSender);
        modelMap.addAttribute("userId",userId);
        String userName=user.getUsername();
        modelMap.addAttribute("username",userName);
        return "/Order";
    }


    @RequestMapping(value="/index/orders/delete/{userid}/{tradeid}",method = RequestMethod.GET)
    public String deleteTrade(@PathVariable("userid")int userId,@PathVariable("tradeid")int tradeid,ModelMap modelMap,RedirectAttributes model){
        tradeGameRepo.deleteTrade(tradeid);
        /*UserEntity user=userRepo.findOne(userId);
        List<TradeGameEntity> TradeAsReceiver1=tradeGameRepo.findAll();
        List<TradeGameEntity> TradeAsReceiver =new ArrayList<>();
        List<TradeGameEntity> TradeAsSender=new ArrayList<>();
        System.out.println(TradeAsReceiver1.size());
        Iterator<TradeGameEntity> iterGame=TradeAsReceiver1.iterator();
        while(iterGame.hasNext()){
            TradeGameEntity tradeGame=iterGame.next();
            if(tradeGame.getReceiver().getUserId()==userId){
                TradeAsReceiver.add(tradeGame);
            }
        }
        for(int i =0;i<TradeAsReceiver1.size();i++){
            if(TradeAsReceiver1.get(i).getSender().getUserId()==userId){
                TradeAsSender.add(TradeAsReceiver1.get(i));
            }
        }
        modelMap.addAttribute("TradeAsReceiver",TradeAsReceiver);
        modelMap.addAttribute("TradeAsSender",TradeAsSender);
        modelMap.addAttribute("userId",userId);
        String userName=user.getUsername();
        modelMap.addAttribute("username",userName);*/
        model.addFlashAttribute("userid",userId);
        return "redirect:/index/orders/{userid}";
    }
}
