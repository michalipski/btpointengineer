package org.lipski;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.lipski.database.DatabaseUpdater;
import org.lipski.server.BluetoothServer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class App
{

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main( String[] args ) throws Exception {
        App app = new App();
        BluetoothServer btServer = new BluetoothServer();
        DatabaseUpdater.update();
        btServer.startServer();
    }

    private void sendPost() throws Exception {

        String url = "http://localhost:8080/TouristApp/remotelogin";

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("login", "michal"));
        urlParameters.add(new BasicNameValuePair("pass", "tests"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());

    }
}
