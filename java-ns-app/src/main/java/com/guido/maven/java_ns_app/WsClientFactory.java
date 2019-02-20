package com.guido.maven.java_ns_app;

import com.netsuite.suitetalk.client.v2017_2.WsClient;
import com.netsuite.suitetalk.proxy.v2017_2.platform.core.GetDataCenterUrlsResult;
import org.apache.axis.AxisFault;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import static com.guido.maven.java_ns_app.Messages.GETTING_URL;
import static com.guido.maven.java_ns_app.utils.PrintUtils.printInfoWithEmptyLine;

/**
 * <p>Factory for creating fully initialized web services client.</p>
 * <p>© 2017 NetSuite Inc. All rights reserved.</p>
 */
public final class WsClientFactory {

    private WsClientFactory() {
    }

    public static WsClient getWsClient(Properties properties, URL endpointUrl) throws MalformedURLException, RemoteException {
        WsClient client = new WsClient(endpointUrl == null ? properties.getWebServicesUrl() : endpointUrl);
        if (properties.isTbaRequired()) {
            client.setTokenPassport(properties.getTokenPassport());
        } else {
            client.setRequestLevelCredentials(properties.getPassport());
            client.setApplicationId(properties.getApplicationId());
        }
        // If TCP Monitor is not used, get endpoint URL from getDataCenterUrls() operation
        if (endpointUrl == null && !properties.isTcpMonitor()) {
            String account = properties.isTbaRequired() ? properties.getTokenPassport().getAccount()
                    : properties.getPassport().getAccount();
            printInfoWithEmptyLine(GETTING_URL);
            GetDataCenterUrlsResult urlsResult = client.callGetDataCenterUrls(account);
            if (!urlsResult.getStatus().isIsSuccess()) {
                throw new AxisFault(urlsResult.getStatus().getStatusDetail()[0].getMessage());
            }
            return getWsClient(properties, new URL(urlsResult.getDataCenterUrls().getWebservicesDomain()));
        }
        return client;
    }
}
