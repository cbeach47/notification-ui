package uk.ac.ed.notify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.notify.entity.UiRole;
import uk.ac.ed.notify.entity.UiUser;
import uk.ac.ed.notify.entity.UiUserRole;
import uk.ac.ed.notify.repository.UiRoleRepository;
import uk.ac.ed.notify.repository.UiUserRepository;
import uk.ac.ed.notify.repository.UiUserRoleRepository;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by rgood on 29/10/2015.
 */
@RestController
public class UiUserController {
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    @Value("${hub.logoutRedirectLocation}")
    private String logoutRedirectLocation;

    @Autowired
    private UiUserRepository uiUserRepository;

    @Autowired
    private UiRoleRepository uiRoleRepository;

    @Autowired
    private UiUserRoleRepository uiUserRoleRepository;

    @RequestMapping(value="/ui-users",method = RequestMethod.GET)
    public List<UiUser> getUsers()
    {
        return (List<UiUser>)uiUserRepository.findAll();
    }

    @RequestMapping(value="/ui-user/{uun}", method = RequestMethod.GET)
    public UiUser getUser(@PathVariable("uun") String uun)
    {
        return uiUserRepository.findOne(uun);
    }

    @RequestMapping(value="/ui-user/{uun}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable("uun") String uun)
    {

        for (UiUserRole uiUserRole : uiUserRoleRepository.findByUun(uun)) {
            uiUserRoleRepository.delete(uiUserRole);
        }

        uiUserRepository.delete(uun);
    }

    @RequestMapping(value="/ui-user/{uun}", method = RequestMethod.PUT)
    public void updateUser(@PathVariable("uun") String uun,@RequestBody UiUser uiUser)
    {
        uiUserRepository.save(uiUser);
    }

    @RequestMapping(value="/ui-roles", method = RequestMethod.GET)
    public List<UiRole> getRoles()
    {
        return (List<UiRole>)uiRoleRepository.findAll();
    }


    /*
      User retrieval based on single sign on
     */
    @RequestMapping(value="/user-role", method = RequestMethod.GET)
    public UiUser getUserRole(HttpServletRequest request)
    {
       String uun = request.getRemoteUser();
       return uiUserRepository.findOne(uun); 
    }

    /*
      Invalidate session
     */
    @RequestMapping(value="/invalidate-session", method = RequestMethod.GET)
    public void invalidateSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().setAttribute("test", "test1");
        logger.info("invalidate");
        request.getSession().invalidate();
        logger.info("remote user - " + request.getRemoteUser());
        logger.info("session - " + request.getSession().getAttribute("test") );
        response.sendRedirect(logoutRedirectLocation);
    }

}
