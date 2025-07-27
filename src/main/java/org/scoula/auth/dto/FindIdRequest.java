package org.scoula.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FindIdRequest {
    private String name;
    private String phoneNumber;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

}
