package com.guido.maven.java_ns_app;

import com.netsuite.suitetalk.client.common.authentication.Credentials;
import com.netsuite.suitetalk.client.common.authentication.Passport;
import com.netsuite.suitetalk.client.common.authentication.TokenPassport;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.netsuite.suitetalk.client.common.utils.CommonUtils.composeUrl;
import static com.guido.maven.java_ns_app.Messages.PROMPT_FOR_LOGIN_INFORMATION;
import static com.guido.maven.java_ns_app.io.Console.readLine;
import static com.guido.maven.java_ns_app.io.Console.readPassword;
import static com.guido.maven.java_ns_app.utils.IndentationUtils.getIndentedString;
import static com.guido.maven.java_ns_app.utils.PrintUtils.printWithEmptyLine;
import static com.guido.maven.java_ns_app.utils.StringUtils.getFirstCapitalized;
import static java.lang.Boolean.parseBoolean;

/**
 * <p>This class provides access to all properties in nsclient.properties file.</p>
 * <p>© 2017 NetSuite Inc. All rights reserved.</p>
 */
public class Properties extends java.util.Properties {

    private static final String PROPERTIES_FILE = "nsclient.properties";

    private static final String WS_URL = "wsUrl";

    private static final String PROMPT_FOR_LOGIN = "promptForLogin";

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String ACCOUNT = "account";
    private static final String ROLE = "roleId";

    private static final String APPLICATION_ID = "applicationId";

    private static final String USE_TBA = "useTba";
    private static final String TBA_CONSUMER_KEY = "tbaConsumerKey";
    private static final String TBA_CONSUMER_SECRET = "tbaConsumerSecret";
    private static final String TBA_TOKEN = "tbaTokenId";
    private static final String TBA_TOKEN_SECRET = "tbaTokenSecret";

    private static final String USE_TCP_MONITOR = "useTcpMonitor";

    private Passport passport;

    /**
     * Constructor loading all properties from {@code nsclient.properties} file.
     *
     * @throws IOException If it is something wrong with properties file
     */
    public Properties() throws IOException {
        super();
        load(new FileInputStream(PROPERTIES_FILE));
    }

    public String getEmail() {
    	return getProperty(EMAIL);
    }
    /**
     * @return URL for web services endpoint written in properties file
     * @throws MalformedURLException If URL in properties file has invalid format
     */
    public URL getWebServicesUrl() throws MalformedURLException {
        URL url = new URL(getProperty(WS_URL));
        return composeUrl(url.getProtocol(), url.getHost(), url.getPort());
    }

    /**
     * Returns either credentials entered into properties file or asks a customer to enter credentials
     * if {@code promptForLogin} is {@code true}.
     *
     * @return Object containing user's email and password
     */
    public Credentials getCredentials() {
        String email;
        String password;
        if (isPromptForLogin()) {
            printWithEmptyLine(PROMPT_FOR_LOGIN_INFORMATION);
            email = readLine(getIndentedString(getFirstCapitalized(EMAIL)));
            password = readPassword(getIndentedString(getFirstCapitalized(PASSWORD)));
        } else {
            email = getProperty(EMAIL);
            password = getProperty(PASSWORD);
        }
        return new Credentials(email, password);
    }

    /**
     * Returns a passport according to information in properties file or asks a customer to enter information
     * if {@code promptForLogin} is {@code true}.
     *
     * @return Object containing passport for authentication
     */
    public Passport getPassport() {
        if (passport == null) {
            Credentials credentials = getCredentials();
            String account;
            String role;
            if (isPromptForLogin()) {
                account = readLine(getIndentedString(getFirstCapitalized(ACCOUNT)));
                role = readLine(getIndentedString(getFirstCapitalized(ROLE)), null);
            } else {
                account = getProperty(ACCOUNT);
                role = getProperty(ROLE);
            }
            passport = new Passport(credentials, account, role);
        }
        return passport;
    }

    /**
     * Returns a token passport according to information in properties file.
     *
     * @return Object containing passport for authentication using TBA
     */
    public TokenPassport getTokenPassport() {
        String account = getProperty(ACCOUNT);
        String consumerKey = getProperty(TBA_CONSUMER_KEY);
        String consumerSecret = getProperty(TBA_CONSUMER_SECRET);
        String token = getProperty(TBA_TOKEN);
        String tokenSecret = getProperty(TBA_TOKEN_SECRET);
        return new TokenPassport(account, consumerKey, consumerSecret, token, tokenSecret);
    }

    /**
     * @return Application ID stored in properties file
     */
    public String getApplicationId() {
        return getProperty(APPLICATION_ID);
    }

    /**
     * @return {@code true} if login information should be prompted from a user
     */
    private boolean isPromptForLogin() {
        return parseBoolean(getProperty(PROMPT_FOR_LOGIN));
    }

    /**
     * @return {@code true} if TBA should be used for authentication instead of RLC
     */
    public boolean isTbaRequired() {
        return parseBoolean(getProperty(USE_TBA));
    }

    /**
     * @return {@code true} if SOAP requests are going to be monitored by TCP monitor so the requests should not be
     * routed to the URL got from getDataCenterUrls() operation
     */
    public boolean isTcpMonitor() {
        return parseBoolean(getProperty(USE_TCP_MONITOR));
    }
}
