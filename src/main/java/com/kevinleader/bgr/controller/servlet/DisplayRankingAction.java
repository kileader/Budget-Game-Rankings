package com.kevinleader.bgr.controller.servlet;

import com.kevinleader.bgr.analyzer.Ranker;
import com.kevinleader.bgr.entity.database.RankingConfiguration;
import com.kevinleader.bgr.entity.igdb.Game;
import com.kevinleader.bgr.entity.ranker.RankedGame;
import com.kevinleader.bgr.persistence.GenericDao;
import com.kevinleader.bgr.persistence.IgdbDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Sets the list of ranked games by the chosen ranking configuration, and reloads displayRanking.
 *
 * @author Kevin Leader
 */
@WebServlet(
        name = "DisplayRankingAction",
        urlPatterns = {"/displayRankingAction"}
)
public class DisplayRankingAction extends HttpServlet {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private RankingConfiguration chosenRankConfig;
    private GenericDao rankConfigDao = new GenericDao(RankingConfiguration.class);
    private IgdbDao igdbDao;
    private Ranker ranker;

    @Override
    public void init() {
        logger.debug("run DisplayRankingAction.init()");
        igdbDao = new IgdbDao();
        ranker = new Ranker();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.debug("run DisplayRankingAction.doGet()");

        HttpSession session = req.getSession();
        List<RankingConfiguration> rankConfigs = (List<RankingConfiguration>) session.getAttribute("rankConfigs");
        req.setAttribute("rankConfigs", rankConfigs);

        int rankConfigId = Integer.valueOf(req.getParameter("rankConfigId"));
        chosenRankConfig = (RankingConfiguration) rankConfigDao.getById(rankConfigId);
        req.setAttribute("chosenRankConfig", chosenRankConfig);

        String whereCondition = igdbDao.createWhereCondition(chosenRankConfig);
        Game[] games = igdbDao.loadGamesToRank(whereCondition);
        
        List<RankedGame> rankedGames = null;
        try {
            rankedGames = ranker.getRankedGameList(games);
        } catch (Exception e) {
            logger.error("exception: ", e);
        }

        req.setAttribute("rankedGames", rankedGames);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/displayRanking.jsp");
        dispatcher.forward(req, resp);
    }

}
