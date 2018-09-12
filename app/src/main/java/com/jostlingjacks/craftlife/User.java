package com.jostlingjacks.craftlife;

public class User {
    private int id;
    private String email;

    public User() {
        super();
    }

    public User(int id, String email){
        super();
        this.id = id;
        this.email = email;
    }

    public User(String email){
        this.email = email;
    }


    public int getId(){
        return id;
    }

    public void setId(){
        this.id = id;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(){
        this.email = email;
    }
}
