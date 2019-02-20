package com.guido.maven.java_ns_app;

import com.netsuite.suitetalk.client.v2017_2.WsClient;
import org.apache.axis.AxisFault;

import java.io.IOException;
import java.net.MalformedURLException;

import static com.guido.maven.java_ns_app.Messages.ERROR_OCCURRED;
import static com.guido.maven.java_ns_app.Messages.INVALID_WS_URL;
import static com.guido.maven.java_ns_app.Messages.WRONG_PROPERTIES_FILE;
import static com.guido.maven.java_ns_app.utils.PrintUtils.printError;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	WsClient client = null;
        try {
            client = WsClientFactory.getWsClient(new Properties(), null);
        } catch (MalformedURLException e) {
            printError(INVALID_WS_URL, e.getMessage());
            System.exit(2);
        } catch (AxisFault e) {
            printError(ERROR_OCCURRED, e.getFaultString());
            System.exit(3);
        } catch (IOException e) {
            printError(WRONG_PROPERTIES_FILE, e.getMessage());
            System.exit(1);
        }
        
        TransactionSearchBasic tranSearch = new TransactionSearchBasic();
        new SampleOperations(client).run();
    }
}
