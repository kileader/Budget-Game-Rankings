package com.kevinleader.bgr.controller.servlet;

import com.kevinleader.bgr.entity.database.RankingConfiguration;
import com.kevinleader.bgr.entity.database.Role;
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
import java.io.IOException;

/**
 * Gets properties for a new user from the sign up form, and inserts the new user. Then forwards to index.jsp.
 *
 * @author Kevin Leader
 */
@WebServlet(
        name = "SignUp",
        urlPatterns = {"/signup"}
)
public class SignUpAction extends HttpServlet {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private GenericDao userDao;
    private GenericDao rankingConfigurationDao;
    private User receivedUser;
    private User newUser;
    private Role newRole;
    private RankingConfiguration newRankingConfiguration;

    @Override
    public void init() {
        logger.debug("run SignUpAction.init()");
        userDao = new GenericDao(User.class);
        rankingConfigurationDao = new GenericDao(RankingConfiguration.class);
        receivedUser = new User();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        logger.debug("run SignUpAction.doGet()");

        receivedUser.setUserName(req.getParameter("userName"));
        receivedUser.setEmail(req.getParameter("email"));
        receivedUser.setPassword(req.getParameter("password"));

        newUser = new User(receivedUser.getUserName(), receivedUser.getEmail(), receivedUser.getPassword());
        logger.debug("Add user: {}", newUser);

        newRole = new Role(newUser, "user", newUser.getUserName());
        newUser.addRole(newRole);

        newRankingConfiguration = new RankingConfiguration(newUser, "Any Game for Past 5 Years", "Any", "Any", 157784630);

        int id = userDao.insert(newUser);
        int id2 = rankingConfigurationDao.insert(newRankingConfiguration);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
        dispatcher.forward(req, resp);
    }

}
