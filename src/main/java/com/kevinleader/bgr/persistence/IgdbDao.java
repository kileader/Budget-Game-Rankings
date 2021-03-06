package com.kevinleader.bgr.persistence;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevinleader.bgr.entity.database.RankingConfiguration;
import com.kevinleader.bgr.entity.igdb.Game;
import com.kevinleader.bgr.entity.igdb.Website;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for use with the Igdb.com web service
 *
 * @author Kevin Leader
 */
public class IgdbDao {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private String fields = "id,aggregated_rating,aggregated_rating_count,cover,first_release_date," +
            "genres,name,platforms,rating,rating_count,storyline,summary,total_rating," +
            "total_rating_count,url,websites.*";

    /**
     * Search igdb.com from game name.
     *
     * @param gameName the game name
     * @return the game array
     * @throws JsonProcessingException the json processing exception
     */
    public Game[] searchFromGameName(String gameName) throws JsonProcessingException {
        logger.debug("run searchFromName({})", gameName);

        String params = "?search=" + gameName.replaceAll(" ", "_") + "&fields=" + fields;
        String url = "https://api.igdb.com/v4/games" + params;

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        builder.header("Client-Id", "bdm989e3d9fgsslcn2l9d9hktacpq3");
        builder.header("Authorization", "Bearer tzy4jcjfhn7dtdw0kb9p9cfgpii0ys");
        builder.header("Accept", "application/json");
        String response = builder.get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        Game[] games = mapper.readValue(response, Game[].class);
        client.close();
        return games;
    }

    /**
     * Load games to rank by using igdb.com api.
     *
     * @param whereConditions the where conditions
     * @return the game array
     * @throws JsonProcessingException the json processing exception
     */
    public Game[] loadGamesToRank(String whereConditions) throws JsonProcessingException {
        logger.debug("run loadGamesToRank({})", whereConditions);

        String url = "https://api.igdb.com/v4/games";
        String body = "fields " + fields + "; limit 500; " + whereConditions + " & rating_count > 4 " +
                "& aggregated_rating_count > 1; sort total_rating desc;";

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        builder.header("Client-Id", "bdm989e3d9fgsslcn2l9d9hktacpq3");
        builder.header("Authorization", "Bearer 7pslgqjjgluo8ci4vslr0gsm4uvavq");
        builder.header("Accept", "application/json");
        String response = builder.post(Entity.entity(body, MediaType.APPLICATION_JSON), String.class);
        ObjectMapper mapper = new ObjectMapper();
        Game[] games = mapper.readValue(response, Game[].class);
        client.close();
        return games;
    }

    /**
     * Gets release date epoch in seconds.
     *
     * @param releaseSpan the release span
     * @return the release date epoch
     */
    public int getReleaseDateEpoch(int releaseSpan) {
        logger.debug("run getReleaseDateEpoch({})", releaseSpan);
        long currentTimestamp = System.currentTimeMillis();
        int currentTimeSec = (int) (currentTimestamp / 1000);
        return currentTimeSec - releaseSpan;
    }

    /**
     * Gets video game names from a list of igdb games.
     *
     * @param games the games
     * @return the names
     */
    public List<String> getNames(Game[] games) {
        logger.debug("run getNames(games)");
        List<String> names = new ArrayList<>();
        for (Game game : games) {
            names.add(game.getName());
        }
        return names;
    }

    /**
     * Creates a where condition string using a ranking configuration to be used with igdb.com api.
     *
     * @param rankConfig the chosen ranking configuration
     * @return the string
     */
    public String createWhereCondition(RankingConfiguration rankConfig) {
        logger.debug("run createWhereConditions({})", rankConfig);
        String platforms = "";
        String genres = "";
        int releaseSpan = rankConfig.getReleaseSpan();

        if (!rankConfig.getPlatforms().equals("Any")) {
            platforms = "platforms = (" + rankConfig.getPlatforms() + ") & ";
        }
        if (!rankConfig.getGenres().equals("Any")) {
            genres = "genres = (" + rankConfig.getGenres() + ") & ";
        }

        int releaseEpoch = getReleaseDateEpoch(releaseSpan);
        String releaseDate = "first_release_date > " + releaseEpoch;

        String whereCondition = "where " + platforms + genres + releaseDate;
        return whereCondition;
    }

    /**
     * Creates a where condition string using igdb game IDs.
     *
     * @param igdbIds the igdb ids
     * @return the string
     */
    public String createWhereConditionFromIds(List<Integer> igdbIds) {
        logger.debug("run createWhereConditionsFromIds({})", igdbIds);

        String whereCondition = "where id = (";
        boolean firstTime = true;
        for (int id : igdbIds) {
            if (!firstTime) {
                whereCondition += ",";
            } else {
                firstTime = false;
            }
            whereCondition += id;
        }
        whereCondition += ");";

        return whereCondition;
    }

//    public Website[] getWebsitesFromGameId(int gameId) throws JsonProcessingException {
//        logger.debug("run getWebsitesFromGameId({})", gameId);
//        String url = "https://api.igdb.com/v4/websites/";
//        String body = "fields *; where game = " + gameId + ";";
//
//        Client client = ClientBuilder.newClient();
//        WebTarget target = client.target(url);
//        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
//        builder.header("Client-Id", "bdm989e3d9fgsslcn2l9d9hktacpq3");
//        builder.header("Authorization", "Bearer 7pslgqjjgluo8ci4vslr0gsm4uvavq");
//        builder.header("Accept", "application/json");
//        String response = builder.post(Entity.entity(body, MediaType.APPLICATION_JSON), String.class);
//        ObjectMapper mapper = new ObjectMapper();
//        Website[] websites = mapper.readValue(response, Website[].class);
//        client.close();
//        return websites;
//    }

}
