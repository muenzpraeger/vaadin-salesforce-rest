package com.winkelmeyer.salesforce.vaadin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Account {

    @JsonProperty(value="Id")
    String id;
    @JsonProperty(value="Name")
    String name;
    @JsonProperty(value="BilingCity")
    String city;
    @JsonProperty(value="BillingCountry")
    String country;
    @JsonProperty(value="BillingPostalCode")
    String postalCode;
    @JsonProperty(value="BillingState")
    String state;
    @JsonProperty(value="BillingStreet")
    String street;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id; 
    }
    
    public String getName() { 
        return name;
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + ", city=" + city + ", country=" + country + ", postalCode="
				+ postalCode + ", state=" + state + ", street=" + street + "]";
	}

}