package com.shaheen.like4like.model;

public class Transaction {


    int points;
    int type;
    String msg;
    String date;
    int balance;
    int plusOrMinus;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getPlusOrMinus() {
        return plusOrMinus;
    }

    public void setPlusOrMinus(int plusOrMinus) {
        this.plusOrMinus = plusOrMinus;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}