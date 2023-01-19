package com.example.uno.models;

import com.example.uno.helpers.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Game implements Serializable {

    String id;

    ArrayList<String> deck = null, discard = new ArrayList<>();

    ArrayList<String> player1hand = new ArrayList<>();

    ArrayList<String> player2hand = new ArrayList<>();

    User player1;

    Date created_at = new Date();

    User player2;

    String winner = null;

    boolean active = true;

    String playerTurn = "p1";

    public Game() {
    }

    public void switchTurn(){
        if(playerTurn.equals("p1"))
            this.playerTurn = "p2";
        else
            this.playerTurn = "p1";
    }

    public ArrayList<String> getUserHand(User user){
        return isPlayer1(user) ? getPlayer1hand() : getPlayer2hand();
    }

    public boolean canPlay(User user){
        for(String card : getUserHand(user))
            if(Utils.canPlay(getTopCard(), card))   return true;
        return false;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public boolean isMyTurn(User user){
        return user.getId().equals(this.getUserTurn().getId());
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public boolean isWinner(User user){
        return winner.equals("p1") == isPlayer1(user);
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner){
        this.winner = winner;
    }

    public ArrayList<String> getDeckCards(int card_count){
        ArrayList<String> toAdd = new ArrayList<>();
        if(deck.size() <= card_count){
            String temp = removeTopCard();
            deck = discard;
            discard.clear();
            discard = new ArrayList<>(Arrays.asList(temp));
        }
        for(int j = 0; j < card_count; j++){
            toAdd.add(deck.remove(j));
        }
        return toAdd;
    }

    public void addWinnerUser(User user) {
        if(isPlayer1(user)) setWinner("p1");
        else setWinner("p2");
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOpponentUser(User user){
        return isPlayer1(user) ? player2 : player1;
    }

    public void addCardsToUser(User user, ArrayList<String> cards){
        if(isPlayer1(user)) {
            cards.addAll(player1hand);
            player1hand = cards;
        } else {
            cards.addAll(player2hand);
            player2hand = cards;
        }
    }

    public ArrayList<String> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<String> deck) {
        this.deck = deck;
    }

    public ArrayList<String> getPlayer1hand() {
        return player1hand;
    }

    public void setPlayer1hand(ArrayList<String> player1hand) {
        this.player1hand = player1hand;
    }

    public ArrayList<String> getPlayer2hand() {
        return player2hand;
    }

    public void setPlayer2hand(ArrayList<String> player2hand) {
        this.player2hand = player2hand;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<String> getDiscard() {
        return discard;
    }

    public void setDiscard(ArrayList<String> discard) {
        this.discard = discard;
    }

    public Game(User player1, User player2, ArrayList<String> deck, ArrayList<String> player1hand, ArrayList<String> player2hand, ArrayList<String> discard) {
        this.deck = deck;
        this.player1 = player1;
        this.player2 = player2;
        this.player1hand = player1hand;
        this.player2hand = player2hand;
        this.discard = discard;
    }

    public String removeTopCard(){
        return this.discard.remove(this.discard.size() - 1);
    }

    public String getTopCard(){
        return this.discard.get(this.discard.size() - 1);
    }

    public String getDrawCard(){
        return (this.deck.size() >= 1) ? this.deck.get(0) : null;
    }

    public boolean isPlayer1(User user){
        return user.getId().equals(this.player1.getId());
    }

    public String getPlayerTurn() {
        return playerTurn;
    }

    public boolean isPlayer1Turn(){
        return this.playerTurn.equals("p1");
    }

    public User getUserTurn(){
        if(this.playerTurn.equals("p1"))   return player1;
        else return player2;
    }

    public void setPlayerTurn(String playerTurn) {
        this.playerTurn = playerTurn;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", deck=" + deck +
                ", discard=" + discard +
                ", player1hand=" + player1hand +
                ", player2hand=" + player2hand +
                ", player1=" + player1 +
                ", created_at=" + created_at +
                ", player2=" + player2 +
                ", winner='" + winner + '\'' +
                ", active=" + active +
                ", playerTurn='" + playerTurn + '\'' +
                '}';
    }
}
