package no.westerdals.pg5100.frontend.controller;

import no.westerdals.pg5100.backend.ejb.UserEJB;
import no.westerdals.pg5100.backend.entity.User;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginController implements Serializable {

    @EJB
    private UserEJB user;

    private User registeredUser;

    private String formUsername;
    private String formPassword;
    private String formPasswordConfirm;

    private String formFirstname;
    private String formMiddlename;
    private String formLastname;
    private String formCountry;


    public LoginController() {}


    public String logIn() {
        if (!formPassword.equals(formPasswordConfirm)) {
            return "login.jsf";
        }

        boolean valid = user.login(formUsername, formPassword);

        if (valid) {
            setRegisteredUser(user.getUser(formUsername));
            return "home.jsf";
        }
        return "login.jsf";
    }

    public String logOut() {
        registeredUser = null;
        return "home.jsf";
    }

    public String registerNew() {
        if (!passwordsMatch()) {
            return "newUser.jsf";
        }

        boolean registered = user.createUser(formUsername, formPassword, formFirstname, formMiddlename, formLastname, formCountry);
        if (registered) {
            setRegisteredUser(user.getUser(formUsername));
            return "home.jsf";
        }

        return "newUser.jsf";
    }

    public boolean isLoggedIn() {
        return getRegisteredUser() != null;
    }

    public boolean passwordsMatch() {
        return formPassword.equals(formPasswordConfirm);
    }

    public String getFormUsername() { return formUsername; }

    public void setFormUsername(String formUsername) { this.formUsername = formUsername; }

    public String getFormPassword() { return formPassword; }

    public void setFormPassword(String formPassword) { this.formPassword = formPassword; }

    public String getFormPasswordConfirm() { return formPasswordConfirm; }

    public void setFormPasswordConfirm(String formPasswordConfirm) { this.formPasswordConfirm = formPasswordConfirm; }

    public User getRegisteredUser() { return registeredUser; }

    public void setRegisteredUser(User registeredUser) { this.registeredUser = registeredUser; }

    public String getFormFirstname() { return formFirstname; }

    public void setFormFirstname(String formFirstname) { this.formFirstname = formFirstname; }

    public String getFormMiddlename() { return formMiddlename; }

    public void setFormMiddlename(String formMiddlename) { this.formMiddlename = formMiddlename; }

    public String getFormLastname() { return formLastname; }

    public void setFormLastname(String formLastname) { this.formLastname = formLastname; }

    public String getFormCountry() { return formCountry; }

    public void setFormCountry(String formCountry) { this.formCountry = formCountry; }
}
