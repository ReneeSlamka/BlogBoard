package com.blogboard.server.web;

public class CreateAccountResponse extends Response {

    private String createAccountMessage;
    private String createAccountFailureURL = "http://localhost:3000/home";
    private String getCreateAccountSuccessURL = "http://localhost:3000/account-created";

    public void setCreateAccountFailureMessage(String duplicateAccountCredential) {
        this.createAccountMessage =
            "Sorry, it seems there is already an account with that " + duplicateAccountCredential
            + ". Please try making another account with a different " + duplicateAccountCredential + ".";
    }

    public void setCreateAccountSuccessMessage() {
        this.createAccountMessage =
            "Congrats, your account has successfully been created! You can now login and start blogging.";
    }


    public String getLoginFailureMessage() {
        return  createAccountMessage;
    }

    public CreateAccountResponse() {
        super();
        this.createAccountMessage = "";
    }

    public void setToSuccess() {
        this.setHttpResponseHeader(getCreateAccountSuccessURL);
        this.setCreateAccountSuccessMessage();
    }

    public void setToFailure(String duplicateAccountField) {
        this.setHttpResponseHeader(createAccountFailureURL);
        this.setCreateAccountFailureMessage(duplicateAccountField);
    }
}
