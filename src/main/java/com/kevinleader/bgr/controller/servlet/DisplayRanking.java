package com.kevinleader.bgr.controller.servlet;

import com.kevinleader.bgr.entity.database.RankingConfiguration;
import com.kevinleader.bgr.entity.database.User;
import com.kevinleader.bgr.persistence.GenericDao;
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
 * Serves displayRanking.jsp and sets ranking configurations for the current user.
 *
 * @author Kevin Leader
 */
@WebServlet(
        name = "DisplayRanking",
        urlPatterns = {"/displayRanking"}
)
public class DisplayRanking extends HttpServlet {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private GenericDao userDao;
    private GenericDao rankingConfigurationDao;

    @Override
    public void init() {
        logger.debug("run DisplayRanking.init()");
        userDao = new GenericDao(User.class);
        rankingConfigurationDao = new GenericDao(RankingConfiguration.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        logger.debug("run DisplayRanking.doGet()");

        // Grab user from login
        String username = req.getUserPrincipal().getName();
        List<User> users = userDao.getByPropertyEqual("userName", username);
        int userId = users.get(0).getId();
        User user = (User) userDao.getById(userId);
        req.setAttribute("user", user);

        HttpSession session = req.getSession();
        session.setAttribute("user", user);

        List<RankingConfiguration> rankConfigs = rankingConfigurationDao.getByPropertyEqual("user", user);
        session.setAttribute("rankConfigs", rankConfigs);
        req.setAttribute("rankConfigs", rankConfigs);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/displayRanking.jsp");
        dispatcher.forward(req, resp);
    }

}
