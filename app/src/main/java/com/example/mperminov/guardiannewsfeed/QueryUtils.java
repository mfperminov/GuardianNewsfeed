package com.example.mperminov.guardiannewsfeed;

import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving news data from Guardian.
 */
public final class QueryUtils {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Story} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<Story> extractStoryData(String jsonResponse) {
        // Create an empty ArrayList that we can start adding stories to
        ArrayList<Story> stories = new ArrayList<>();
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        try {
           //parse root object
            JSONObject storiesJson = new JSONObject(jsonResponse);
            //Extract "subroot" object response
            JSONObject response = storiesJson.getJSONObject("response");
            //check for ok status code
            String status = response.getString("status");
            //Extract “results” JSONArray if status "ok"
            if (status.equals("ok")) {
                JSONArray results = response.getJSONArray("results");
                //Loop through each feature in the array
                for (int i = 0; i < results.length(); i++) {
                    //Get result JSONObject at position i
                    JSONObject result = (JSONObject) results.get(i);
                    //extract title of the Article, url for Intent
                    //and name of the section
                    String title = result.getString("webTitle");
                    String section = result.getString("sectionName");
                    String url = result.getString("webUrl");
                    //extract date and time. I wanna burn with fire my computer
                    //when dealing with java.time so I decided use string concatenation for now
                    String date = result.getString("webPublicationDate");
                    //first substring - date, second - time in UTC,
                    // newline for nice formatting in layout
                    String dateFormatted = date.substring(0,10) + "\n"
                            + date.substring(11,16);
                    //looking for author name in tags
                    JSONArray tags = result.getJSONArray("tags");
                    if (tags != null && tags.length() > 0) {
                        JSONObject tag = tags.getJSONObject(0);
                        String id = tag.getString("webTitle");
                        stories.add(new Story(title,section,url,dateFormatted,id));
                    } else {
                        //Create Story java object from title, section name
                        //url, time and add story to list of stories
                        stories.add(new Story(title,section,url,dateFormatted));
                    }
                }
            } else {
                Log.e("QueryUtils - JSON error",response.getString("message"));

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the stories JSON results", e);
        }
        // Return the list of stories
        return stories;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpConnect(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query Guardian API dataset and return a list of {@link Story} objects.
     */
    public static ArrayList<Story> fetchStoriesData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpConnect(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response, create a list of {@link Story}ies
        // and return it;
        return extractStoryData(jsonResponse);
    }

}