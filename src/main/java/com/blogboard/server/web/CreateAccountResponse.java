package com.blogboard.server.web;

public class CreateAccountResponse extends Response {

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
        this.setCreateAccountSuccessMessage();
    }

    @Override
    public void setToFailure(String duplicateAccountField) {
        this.setCreateAccountFailureMessage(duplicateAccountField);
    }
}
