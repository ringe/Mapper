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

public class Register extends AsyncTask<String, Void, String> {
    private String regURL = "http://go-ringe.rhcloud.com/register";
	
	@Override
	protected String doInBackground(String... urls) {
		String result = null;
		
	    HttpClient httpclient = new DefaultHttpClient();

	    // Prepare a request object
	    HttpGet httpget = new HttpGet(regURL); 

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
	            
	            result = RebusFetch.convertStreamToString(instream);

	            instream.close();
	        }
	        


	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

        return result;
	}

}
