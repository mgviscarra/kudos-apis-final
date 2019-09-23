package com.mgvr.stats.api.model;

public class User {

    private String nickName;
    private int nroKudos;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public int getNroKudos() {
        return nroKudos;
    }

    public void setNroKudos(int nroKudos) {
        this.nroKudos = nroKudos;
    }
}
