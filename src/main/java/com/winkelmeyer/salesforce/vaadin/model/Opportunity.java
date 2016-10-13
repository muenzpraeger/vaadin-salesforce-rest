package com.winkelmeyer.salesforce.vaadin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Opportunity {

    @JsonProperty(value="Id")
    String id;
    @JsonProperty(value="Name")
    String name;
    @JsonProperty(value="Amount")
    Double amount;
    @JsonProperty(value="Account")
    Account account;

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

	@Override
	public String toString() {
		return "Opportunity [id=" + id + ", name=" + name + ", amount=" + amount + ", account=" + account + "]";
	}

}
