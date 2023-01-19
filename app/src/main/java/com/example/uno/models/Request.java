package com.example.uno.models;

import java.util.Date;

public class Request {

    String id;

    User requester;
    Date created_at;

    String game_id = null;
    boolean accepted = false;

    public Request(User requester, Date created_at) {
        this.requester = requester;
        this.created_at = created_at;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public Request() {
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
