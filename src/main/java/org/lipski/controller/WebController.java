package org.lipski.controller;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.lipski.server.ServerProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebController {

    private final String USER_AGENT = "Mozilla/5.0";

    public WebController() {}

    public boolean remoteLogin(String args) throws IOException {
        String url = "http://localhost:8080/TouristApp/remotelogin";

        String[] postParameters = args.split(";",2);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("login", postParameters[0]));
        urlParameters.add(new BasicNameValuePair("pass", postParameters[1]));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer stringResult = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            stringResult.append(line);
        }

        return new Boolean(String.valueOf(stringResult));
    }

    public boolean commentPlace(String args) throws IOException {
        String url = "http://localhost:8080/TouristApp/remotecomment";

        String[] postParameters = args.split(";",3);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("username", postParameters[0]));
        urlParameters.add(new BasicNameValuePair("placeId", postParameters[1]));
        urlParameters.add(new BasicNameValuePair("comment", postParameters[2]));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer stringResult = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            stringResult.append(line);
        }

        return new Boolean(String.valueOf(stringResult));
    }

    public Boolean gradePlace(String args) throws IOException {
        String url = "http://localhost:8080/TouristApp/remotegrade";

        String[] postParameters = args.split(";",3);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("username", postParameters[0]));
        urlParameters.add(new BasicNameValuePair("placeId", postParameters[1]));
        urlParameters.add(new BasicNameValuePair("grade", postParameters[2]));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer stringResult = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            stringResult.append(line);
        }

        return new Boolean(String.valueOf(stringResult));
    }

    public String getObjectListToUpdate(String objectName) throws IOException {
        String url = "http://localhost:8080/TouristApp/btserver/getjson" + objectName + "s/" + ServerProperties.getServerId();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public String getUserListToUpdate(Integer id) throws IOException {
        String url = "http://localhost:8080/TouristApp/btserver/getjsonusers/" + id.toString();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
