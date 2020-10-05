package com.moodle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Melita Saldanha on 10-03-2018.
 */

public class Student {

    String username;
    String password;
    String full_name;
    String email;
    String branch;
    String sem;
    ArrayList<HashMap<String, String>> subjects = new ArrayList<HashMap<String, String>>();

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

    public String getBranch() {
        return branch;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSem() {
        return sem;
    }
    public void setSem(String sem) {
        this.sem = sem;
    }

    public ArrayList<HashMap<String, String>> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<HashMap<String, String>> subjects) {
        this.subjects = subjects;
    }
}
