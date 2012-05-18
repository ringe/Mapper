package no.rin.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class RebusFetch extends AsyncTask<String, Void, ArrayList<Contest>> {
	private String url = "http://go-ringe.rhcloud.com/contests.json";
	
	@Override
	protected ArrayList<Contest> doInBackground(String... urls) {
		Contests contests = new Contests();
		ArrayList<Contest> result = null;
		
	    HttpClient httpclient = new DefaultHttpClient();

	    // Prepare a request object
	    HttpGet httpget = new HttpGet(url); 

	    // Execute the request
	    HttpResponse response;
	    
	    try {
	        response = httpclient.execute(httpget);
	        // Examine the response status
	        Log.i("Praeda",response.getStatusLine().toString());

	        // Get hold of the response entity
	        HttpEntity entity = response.getEntity();
	        // If the response does not enclose an entity, there is no need
	        // to worry about connection release
	        if (entity != null) {

	            // A Simple JSON Response Read
	            InputStream instream = entity.getContent();
	            
	            String json = convertStreamToString(instream);
	            
	            JsonFactory jf = new JsonFactory();
	            JsonParser jp = jf.createJsonParser(json);
	            
	            // now you have the string representation of the HTML request
	            
	    	    ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
				contests = mapper.readValue(jp, Contests.class);
	            result = contests.get("contests");
				
	            instream.close();
	        }
	        


	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

        return result;
	}

	    public static String convertStreamToString(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
}
