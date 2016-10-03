package controller;

import ejb.PostEJB;
import ejb.UserEJB;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@RequestScoped
public class PostController implements Serializable {

    @EJB
    private UserEJB user;

    @EJB
    private PostEJB post;
}
