package com.moodle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melita Saldanha on 12-03-2018.
 */

public class Staff {

    String username;
    String password;
    String full_name;
    String email;
    List<String> subjects = new ArrayList<String>();

    //Default constructor for firebase
    Staff() {

    }

    //Getters and Setters
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getFull_name() {
        return full_name;
    }
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSubjects() {
        return subjects;
    }
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
}
