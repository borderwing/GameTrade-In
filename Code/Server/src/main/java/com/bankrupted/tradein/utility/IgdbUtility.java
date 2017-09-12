package com.bankrupted.tradein.utility;

import com.bankrupted.tradein.model.json.igdb.IgdbGame;
import com.bankrupted.tradein.model.json.igdb.meta.IgdbGenre;
import com.bankrupted.tradein.model.json.igdb.IgdbPlatform;
import com.bankrupted.tradein.model.json.igdb.meta.IgdbKeyword;
import com.bankrupted.tradein.model.json.igdb.meta.IgdbTheme;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by lykav on 7/14/2017.
 */
@Component
public class IgdbUtility {

    private final Logger logger = LoggerFactory.getLogger(IgdbUtility.class);

    @Autowired
    RestTemplate restTemplate;

    private static final String baseUrl = "https://igdbcom-internet-game-database-v1.p.mashape.com";
    private static final String key = "cYrhiTHsSpmshlGHwdCFEIHa04kxp17rTZGjsnbfeOtmtZfCh4";

    private static final ObjectMapper mapper = new ObjectMapper();


    private List<String> gameFullFields = Arrays.asList(
            "name","release_dates","cover","summary","screenshots"
            ,"genres", "themes", "keywords"
            ,"popularity", "url"
    );

    private List<String> metaFields = Arrays.asList(
            "name", "url"
            //, "games"
    );

    private List<String> platformFields = Arrays.asList(
            "name", "logo"
            //, "games"
    );

    private List<String> tileFields = Arrays.asList(
            "name","release_dates","cover", "popularity", "summary"
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

//        if(!response.getStatusCode().is2xxSuccessful()){
//            // something wrong happened
//            return new ResponseEntity<IgdbGame>(response.getHeaders(), response.getStatusCode());
//        } else {
//            return new ResponseEntity<IgdbGame>(getOneFromResponse(response), response.getHeaders(), response.getStatusCode());
//        }
        return getOneFromResponse(response);

    }

    public List<IgdbGame> getTrendingIgdbGames(int limit, int offset){

        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(tileFields, ',')));
        queryParams.add(new Pair<>("order", "popularity:desc"));
        queryParams.add(new Pair<>("limit", limit));
        queryParams.add(new Pair<>("offset", offset));
        queryParams.add(new Pair<>("filter[release_dates][exists]", null));

        queryParams.add(new Pair<>("filter[category][not_eq]", 2));

        UriComponentsBuilder builder = constructBuilder("/games", queryParams);
        ResponseEntity<List<IgdbGame>> response = retrieveIgdbResponse(IgdbGame.class, builder);

//        if(!response.getStatusCode().is2xxSuccessful()){
//            // something wrong happened
//            return new ResponseEntity<>(response.getHeaders(), response.getStatusCode());
//        } else {
//            return new ResponseEntity<>(getListFromResponse(response), response.getHeaders(), response.getStatusCode());
//        }

        return getListFromResponse(response);
    }

    public List<IgdbGame> getSearchedIgdbGames(String keyword, int limit, int offset){
        if(StringUtils.isEmptyOrWhitespace(keyword)){
            return new ArrayList<>();
        }
        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(tileFields, ',')));

        queryParams.add(new Pair<>("search", keyword));
        queryParams.add(new Pair<>("limit", limit));
        queryParams.add(new Pair<>("offset", offset));

       // queryParams.add(new Pair<>("order", "popularity:desc"));
        queryParams.add(new Pair<>("filter[category][not_eq]", 2));
        queryParams.add(new Pair<>("filter[release_dates][exists]", null));

        UriComponentsBuilder builder = constructBuilder("/games", queryParams);
        ResponseEntity<List<IgdbGame>> response = retrieveIgdbResponse(IgdbGame.class, builder);

        return getListFromResponse(response);
    }

    public List<IgdbGame> getIgdbGames(Collection<Long> igdbIdList){
        if(igdbIdList.size() == 0) return new ArrayList<>();

        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(gameFullFields, ',')));

        UriComponentsBuilder builder = constructBuilder("/games", igdbIdList, queryParams);
        ResponseEntity<List<IgdbGame>> response = retrieveIgdbResponse(IgdbGame.class, builder);

        return getListFromResponse(response);
    }

    public IgdbGenre getIgdbGenre(long igdbGenreId){
        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(metaFields, ',')));

        UriComponentsBuilder builder = constructBuilder("/genres", queryParams);
        ResponseEntity<List<IgdbGenre>> response = retrieveIgdbResponse(IgdbGenre.class, builder);

        return getOneFromResponse(response);
    }

    public List<IgdbGenre> getIgdbGenres(Collection<Long> genreIdList){
        if(genreIdList.size() == 0)  return new ArrayList<>();

        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(metaFields, ',')));

        UriComponentsBuilder builder = constructBuilder("/genres", genreIdList, queryParams);
        ResponseEntity<List<IgdbGenre>> response = retrieveIgdbResponse(IgdbGenre.class, builder);

        return getListFromResponse(response);
    }

    public List<IgdbTheme> getIgdbThemes(Collection<Long> themeIdList){
        if(themeIdList.size() == 0)  return new ArrayList<>();


        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(metaFields, ',')));


        UriComponentsBuilder builder = constructBuilder("/themes", themeIdList, queryParams);
        ResponseEntity<List<IgdbTheme>> response = retrieveIgdbResponse(IgdbTheme.class, builder);

        return getListFromResponse(response);
    }

    public List<IgdbKeyword> getIgdbKeywords(Collection<Long> keywordIdList){
        if(keywordIdList.size() == 0)  return new ArrayList<>();

        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(metaFields, ',')));

        UriComponentsBuilder builder = constructBuilder("/keywords", keywordIdList, queryParams);
        ResponseEntity<List<IgdbKeyword>> response = retrieveIgdbResponse(IgdbKeyword.class, builder);

        return getListFromResponse(response);
    }

    public List<IgdbPlatform> getIgdbPlatforms(Collection<Integer> platformIdList){
        if(platformIdList.size() == 0)  return new ArrayList<>();

        List<Pair<String, Object>> queryParams = new ArrayList<>();
        queryParams.add(new Pair<>("fields", StringUtils.join(metaFields, ',')));

        List<Long> platformLongIdList = new ArrayList<>(platformIdList.size());
        for(Integer platformId : platformIdList){
            platformLongIdList.add(platformId.longValue());
        }

        UriComponentsBuilder builder = constructBuilder("/platforms", platformLongIdList, queryParams);
        ResponseEntity<List<IgdbPlatform>> response = retrieveIgdbResponse(IgdbPlatform.class, builder);

        return getListFromResponse(response);
    }

    @Async
    public CompletableFuture<List<IgdbPlatform>> getFutureIgdbPlatforms(Collection<Integer> platformIdList){
        return CompletableFuture.completedFuture(getIgdbPlatforms(platformIdList));
    }

    @Async
    public CompletableFuture<List<IgdbGenre>> getFutureIgdbGenres(Collection<Long> genreIdList){
        return CompletableFuture.completedFuture(getIgdbGenres(genreIdList));
    }

    @Async
    public CompletableFuture<List<IgdbTheme>> getFutureIgdbThemes(Collection<Long> themeIdList){
        return CompletableFuture.completedFuture(getIgdbThemes(themeIdList));
    }

    @Async
    public CompletableFuture<List<IgdbKeyword>> getFutureIgdbKeywords(Collection<Long> keywordIdList){
        return CompletableFuture.completedFuture(getIgdbKeywords(keywordIdList));
    }


    private UriComponentsBuilder constructBuilder(String entry, List<Pair<String, Object>> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + entry + "/");
        for(Pair<String, Object> paramPair : queryParams){
            builder.queryParam(paramPair.getKey(), paramPair.getValue());
        }

        logger.info(builder.build().encode().toString());

        return builder;
    }

    private UriComponentsBuilder constructBuilder(String entry, Long subEntryId, List<Pair<String, Object>> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + entry + "/" + subEntryId);
        for(Pair<String, Object> paramPair : queryParams){
            builder.queryParam(paramPair.getKey(), paramPair.getValue());
        }

        logger.info(builder.build().encode().toString());

        return builder;
    }

    private UriComponentsBuilder constructBuilder(String entry, Collection<Long> subEntryIds, List<Pair<String, Object>> queryParams){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + entry + "/" + StringUtils.join(subEntryIds, ','));
        for(Pair<String, Object> paramPair : queryParams){
            builder.queryParam(paramPair.getKey(), paramPair.getValue());
        }

        logger.info(builder.build().encode().toString());

        return builder;
    }

    private <T> ResponseEntity<List<T>> retrieveIgdbResponse (Class<T> type, UriComponentsBuilder builder){

        ResponseEntity<String> stringResponse;

        try {
            stringResponse = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<String>() {
                    }
            );

        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

        if(stringResponse.getStatusCode() == null || stringResponse.getHeaders() == null){
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

        if(stringResponse.getStatusCode() != HttpStatus.OK){
            // something went wrong
            logger.debug(stringResponse.getStatusCode().toString());
            logger.debug(stringResponse.getBody());
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
            return new ResponseEntity<List<T>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // convert to another response entity
        ResponseEntity<List<T>> response =
                new ResponseEntity<>(objects, stringResponse.getHeaders(), stringResponse.getStatusCode());

        return response;
    }

    private <T> T getOneFromResponse(ResponseEntity<List<T>> response){
        if(response.getStatusCode() != HttpStatus.OK || response.getBody() == null){
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
        if(response.getStatusCode() != HttpStatus.OK || response.getBody() == null){
            // something went wrong, return null instead
            return null;
        }
        return response.getBody();
    }

}
