package com.blogboard.server.web;

public class CreateAccountResponse extends Response {

    //find better place for these strings
    private String getCreateAccountSuccessURL = "http://localhost:3000/account-created";
    private String createAccountFailureURL = "http://localhost:3000/home";

    public CreateAccountResponse() {
        super();
    }

    private void setCreateAccountSuccessMessage() {
        this.setResponseMessage("Congrats, your account has successfully been " +
                "created! You can now login and start blogging.");
    }

    private void setCreateAccountFailureMessage(String duplicateAccountCredential) {
        this.setResponseMessage(
            "Sorry, it seems there is already an account with that " + duplicateAccountCredential
            + ". Please try making another account with a different " + duplicateAccountCredential + ".");
    }

    @Override
    public void setToSuccess() {
        this.setHttpResponseHeader(getCreateAccountSuccessURL);
        this.setCreateAccountSuccessMessage();
    }

    @Override
    public void setToFailure(String duplicateAccountField) {
        this.setHttpResponseHeader(createAccountFailureURL);
        this.setCreateAccountFailureMessage(duplicateAccountField);
    }
}
