package com.blogboard.server.web;


public class CreateBoardResponse extends Response {

    public CreateBoardResponse() { super(); }

    private void setSuccessMessage() {
        this.setResponseMessage("Board creation successful");
    }

    private void setFailureMessage(String causeOfFailure) {
        this.setResponseMessage("Sorry, it seems there is already a board with that username. Try again.");
    }

    @Override
    public void setToSuccess() {
        this.setSuccessMessage();
    }

    @Override
    public void setToFailure(String incorrectLoginParameter) {
        this.setFailureMessage(incorrectLoginParameter);
    }



}
