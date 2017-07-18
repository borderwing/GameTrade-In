package com.bankrupted.tradein.utility;

import com.bankrupted.tradein.model.json.igdb.IgdbGame;
import com.bankrupted.tradein.model.json.igdb.IgdbGenre;
import com.bankrupted.tradein.model.json.igdb.IgdbPlatform;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.org.apache.regexp.internal.RE;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lykav on 7/14/2017.
 */
@Component
public class IgdbUtility {

    @Autowired
    RestTemplate restTemplate;

    private static final String baseUrl = "https://igdbcom-internet-game-database-v1.p.mashape.com";
    private static final String key = "cYrhiTHsSpmshlGHwdCFEIHa04kxp17rTZGjsnbfeOtmtZfCh4";

    private static final ObjectMapper mapper = new ObjectMapper();


    private List<String> gameFullFields = Arrays.asList(
            "name","release_dates","cover","summary","screenshots"
            //,"genres", "themes", "keywords"
    );

    private List<String> genreFields = Arrays.asList(
            "name", "url"
            //, "games"
    );

    private List<String> platformFields = Arrays.asList(
            "name", "logo"
            //, "games"
    );

    private List<String> tileFields = Arrays.asList(
            "name","release_dates","cover", "popularity"
    );

    private static HttpHeaders headers;
    private static HttpEntity<?> httpEntity;

    public IgdbUtility(){
        headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Mashape-Key", key);
        httpEntity = new HttpEntity<>(headers);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public IgdbGame getIgdbGame(long igdbId){

        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(gameFullFields, ',')));

        UriComponentsBuilder builder = constructBuilder("/games", igdbId, queryParams);
        ResponseEntity<List<IgdbGame>> response = retrieveIgdbResponse(IgdbGame.class, builder);

        return getOneFromResponse(response);

    }

    public List<IgdbGame> getIgdbGames(List<Long> igdbIdList){

        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(gameFullFields, ',')));

        UriComponentsBuilder builder = constructBuilder("/games", igdbIdList, queryParams);
        ResponseEntity<List<IgdbGame>> response = retrieveIgdbResponse(IgdbGame.class, builder);

        return getListFromResponse(response);
    }

    public IgdbGenre getIgdbGenre(long igdbGenreId){
        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(genreFields, ',')));

        UriComponentsBuilder builder = constructBuilder("/genres", queryParams);
        ResponseEntity<List<IgdbGenre>> response = retrieveIgdbResponse(IgdbGenre.class, builder);

        return getOneFromResponse(response);
    }

    public List<IgdbGenre> getIgdbGenres(List<Integer> genreIdList){
        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(genreFields, ',')));

        List<Long> genreLongIdList = new ArrayList<>(genreIdList.size());
        for(Integer genreId : genreIdList){
            genreLongIdList.add(genreId.longValue());
        }

        UriComponentsBuilder builder = constructBuilder("/genres", genreLongIdList, queryParams);
        ResponseEntity<List<IgdbGenre>> response = retrieveIgdbResponse(IgdbGenre.class, builder);

        return getListFromResponse(response);
    }

    public List<IgdbPlatform> getIgdbPlatforms(List<Integer> platformIdList){
        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(genreFields, ',')));

        List<Long> platformLongIdList = new ArrayList<>(platformIdList.size());
        for(Integer platformId : platformIdList){
            platformLongIdList.add(platformId.longValue());
        }

        UriComponentsBuilder builder = constructBuilder("/platforms", platformLongIdList, queryParams);
        ResponseEntity<List<IgdbPlatform>> response = retrieveIgdbResponse(IgdbPlatform.class, builder);

        return getListFromResponse(response);
    }

    public List<IgdbGame> getTrendingIgdbGames(int limit, int offset){

        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(tileFields, ',')));
        queryParams.add(new Pair<>("order", "popularity:desc"));
        queryParams.add(new Pair<>("limit", limit));
        queryParams.add(new Pair<>("offset", offset));

        UriComponentsBuilder builder = constructBuilder("/games", queryParams);
        ResponseEntity<List<IgdbGame>> response = retrieveIgdbResponse(IgdbGame.class, builder);

        return getListFromResponse(response);

    }




    private UriComponentsBuilder constructBuilder(String entry, List<Pair<String, Object>> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + entry + "/");
        for(Pair<String, Object> paramPair : queryParams){
            builder.queryParam(paramPair.getKey(), paramPair.getValue());
        }

        System.out.println(builder.build().encode().toString());

        return builder;
    }

    private UriComponentsBuilder constructBuilder(String entry, Long subEntryId, List<Pair<String, Object>> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + entry + "/" + subEntryId);
        for(Pair<String, Object> paramPair : queryParams){
            builder.queryParam(paramPair.getKey(), paramPair.getValue());
        }

        System.out.println(builder.build().encode().toString());

        return builder;
    }

    private UriComponentsBuilder constructBuilder(String entry, List<Long> subEntryIdList, List<Pair<String, Object>> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + entry + "/" + StringUtils.join(subEntryIdList, ','));
        for(Pair<String, Object> paramPair : queryParams){
            builder.queryParam(paramPair.getKey(), paramPair.getValue());
        }

        System.out.println(builder.build().encode().toString());

        return builder;
    }

    private <T> ResponseEntity<List<T>> retrieveIgdbResponse (Class<T> type, UriComponentsBuilder builder){

        ResponseEntity<String> stringResponse = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<String>() {
                }
        );

        if(stringResponse.getStatusCode() != HttpStatus.OK){
            // something went wrong
            System.out.println(stringResponse.getStatusCode().toString());
            System.out.println(stringResponse.getBody());
            return new ResponseEntity<>(stringResponse.getHeaders(), stringResponse.getStatusCode());
        }

        List<T> objects;
        try {
            // use jackson to convert the string response to an actual json object
            TypeFactory factory = TypeFactory.defaultInstance();
            objects = mapper.readValue(stringResponse.getBody(),
                                               factory.constructCollectionType(ArrayList.class, type));
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }

        // convert to another response entity
        ResponseEntity<List<T>> response =
                new ResponseEntity<>(objects, stringResponse.getHeaders(), stringResponse.getStatusCode());

        return response;
    }

    private <T> T getOneFromResponse(ResponseEntity<List<T>> response){
        if(response.getStatusCode() != HttpStatus.OK){
            // something went wrong, return null instead
            return null;
        }

        List<T> objects = response.getBody();
//        TypeFactory factory = TypeFactory.defaultInstance();
//        List<T> objects = mapper.convertValue(response.getBody(), factory.constructCollectionType(ArrayList.class, type));

        if(objects.size() == 0) {
            return null;
        }  else  {
            return objects.get(0);
        }
    }

    private <T> List<T> getListFromResponse(ResponseEntity<List<T>> response){
        if(response.getStatusCode() != HttpStatus.OK){
            // something went wrong, return null instead
            return null;
        }
        return response.getBody();
    }

}
