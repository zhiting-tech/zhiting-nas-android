package com.zhiting.clouddisk.request;

import java.util.List;

public class MoveCopyRequest {

    /**
     * action : move
     * destination : /1/试试/a1
     * sources : ["/1/试试/secret garden"]
     */

    private String action;
    private String destination;
    private String destination_pwd;
    private List<String> sources;

    public MoveCopyRequest(String action, String destination, List<String> sources) {
        this.action = action;
        this.destination = destination;
        this.sources = sources;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public String getDestination_pwd() {
        return destination_pwd;
    }

    public void setDestination_pwd(String destination_pwd) {
        this.destination_pwd = destination_pwd;
    }
}
