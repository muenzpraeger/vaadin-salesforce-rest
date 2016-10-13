package com.winkelmeyer.salesforce.vaadin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.vaadin.addon.oauthpopup.OAuthListener;
import org.vaadin.addon.oauthpopup.OAuthPopupOpener;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.button.PrimaryButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.force.api.ApiSession;
import com.force.api.ForceApi;
import com.force.api.Identity;
import com.github.scribejava.apis.SalesforceApi;
import com.github.scribejava.apis.salesforce.SalesforceToken;
import com.github.scribejava.core.model.Token;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;
import com.winkelmeyer.salesforce.vaadin.model.Account;
import com.winkelmeyer.salesforce.vaadin.model.Opportunity;

@SuppressWarnings("serial")
@Theme("VaadinSalesforceRest")
@Push
public class VaadinSalesforceRestUI extends UI {

	private CssLayout actions = new MCssLayout().withStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
	private VerticalSplitPanel layoutMain = new VerticalSplitPanel(actions, new Label(""));
	private ForceApi api;

	private static String OAUTH_CONSUMER_KEY = "OAUTH_CONSUMER_KEY";
	private static String OAUTH_CONSUMER_SECRET = "OAUTH_CONSUMER_SECRET";

	@Override
	protected void init(VaadinRequest request) {
		initLoginUI();
	}

	private void initLoginUI() {
        Button btnLoginForce = new PrimaryButton("Login to Salesforce");
        OAuthPopupOpener opener = new OAuthPopupOpener(SalesforceApi.instance(), System.getenv(OAUTH_CONSUMER_KEY), System.getenv(OAUTH_CONSUMER_SECRET));
        opener.extend(btnLoginForce);

        opener.addOAuthListener(new OAuthListener() {
            @Override
            public void authSuccessful(Token token, boolean isOAuth20) {
                SalesforceToken sfToken = (SalesforceToken)token;
                initForceConfig(sfToken.getAccessToken(), sfToken.getInstanceUrl());
                access(()->populateUiComponents());
            }

            @Override
            public void authDenied(String reason) {
                Notification.show("authDenied: " + reason);
            }
        });
        btnLoginForce.focus();

		setContent(new MVerticalLayout(new MVerticalLayout(
				new RichText().withMarkDown(
						"# Login to proceed \n\n To access the REST services, you need to authorize the app to use the data."),
				btnLoginForce).withWidth("50%")).alignAll(Alignment.MIDDLE_CENTER).withFullHeight());

	}

	private void populateUiComponents() {

        Button btnReadAllAccounts = actionBtn("Read all Accounts test", event -> {

		    List<Account> accounts = api.query("SELECT Id, Name FROM Account", Account.class).getRecords();

		    MTable<Account> accountsTable = new MTable<>();
		    accountsTable.addBeans(accounts).withProperties("id", "name");
		    layoutMain.setSecondComponent(accountsTable.withFullHeight().withFullWidth());

		});


        Button btnReadOpportunitiesWithLimit = actionBtn("Read 10 Opportunities", event -> {

		    List<Opportunity> opportunities = api.query("Select Id, Name from Opportunity LIMIT 10", Opportunity.class).getRecords();

		    MTable<Opportunity> accountsTable = new MTable<>();
		    accountsTable.addBeans(opportunities).withProperties("id", "name");
		    layoutMain.setSecondComponent(accountsTable.withFullHeight().withFullWidth());

		});

        Button btnReadLocationsFromOpportunities = actionBtn("Read Opportunities with Locations",event -> {

		    List<Opportunity> opportunities = api.query("select Id, Name, Amount, Account.Name, Account.BillingCity, Account.BillingCountry, Account.BillingPostalCode, Account.BillingState, Account.BillingStreet from Opportunity WHERE Account.BillingState != ''", Opportunity.class).getRecords();

		    MTable<Opportunity> accountsTable = new MTable<>(Opportunity.class).withFullWidth();
		    accountsTable.addBeans(opportunities)
		    .withProperties("id", "name", "amount", "account.name", "account.city", "account.state");

		    layoutMain.setSecondComponent(new MVerticalLayout(new Button("Show on map", e -> showOnMap(opportunities))).expand(accountsTable));

		});

        Button btnGetIdentity = actionBtn("Get User Data", event -> {
		    Identity identity = api.getIdentity();

		    Notification.show(identity.getOrganizationId());

		});

        actions.addComponents(btnReadAllAccounts, btnReadOpportunitiesWithLimit, btnReadLocationsFromOpportunities, btnGetIdentity);
				layoutMain.setSplitPosition(40, Unit.PIXELS);
				setContent(layoutMain);

    }

	public Button actionBtn(String caption, ClickListener listener) {
		return new MButton(caption, listener).withStyleName(ValoTheme.BUTTON_LINK);
	}

	protected void showOnMap(List<Opportunity> opportunities) {
		ChoroplethMap choroplethMap = new ChoroplethMap();
		Map<String, Long> stateToOppurtunityCount =
		opportunities.stream().filter(o-> o.getAccount().getState() != null).collect(Collectors.groupingBy(( Opportunity o ) -> o.getAccount().getState(),  Collectors.counting()));

		stateToOppurtunityCount.entrySet().stream().forEach(e->choroplethMap.setValue(e.getKey(), e.getValue()));

		layoutMain.setSecondComponent(choroplethMap);
	}

	private void initForceConfig(String accessToken, String instanceUrl) {
		ApiSession session = new ApiSession().setAccessToken(accessToken).setApiEndpoint(instanceUrl);

		api = new ForceApi(session);
	}

}
