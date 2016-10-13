package com.winkelmeyer.salesforce.vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@WebServlet(asyncSupported=true,urlPatterns={"/*","/VAADIN/*"})
@VaadinServletConfiguration(ui=VaadinSalesforceRestUI.class, productionMode=false)
public class VaadinSalesforceRestServlet extends VaadinServlet { }
