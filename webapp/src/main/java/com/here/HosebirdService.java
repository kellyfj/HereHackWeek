package com.here;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class HosebirdService {

	private static int messagesRead=0;
	
	public static void oauth(String consumerKey, String consumerSecret,
			String token, String secret) throws InterruptedException {
		// Create an appropriately sized blocking queue
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

		// Define our endpoint: By default, delimited=length is set (we need
		// this for our processor)
		// and stall warnings are on.
		StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
		endpoint.stallWarnings(false);

		Authentication auth = new OAuth1(consumerKey, consumerSecret, token,
				secret);
		// Authentication auth = new
		// com.twitter.hbc.httpclient.auth.BasicAuth(username, password);

		// Create a new BasicClient. By default gzip is enabled.
		BasicClient client = new ClientBuilder().name("sampleExampleClient")
				.hosts(Constants.STREAM_HOST).endpoint(endpoint)
				.authentication(auth)
				.processor(new StringDelimitedProcessor(queue)).build();

		// Establish a connection
		client.connect();

		// Do whatever needs to be done with messages
		for (int msgRead = 0; msgRead < Integer.MAX_VALUE; msgRead++) {
			if (client.isDone()) {
				System.out.println("Client connection closed unexpectedly: "
						+ client.getExitEvent().getMessage());
				break;
			}

			String msg = queue.poll(5, TimeUnit.SECONDS);			
			if (msg != null) {
				messagesRead++;
				Tweet t = extractTweet(msg);
				processTweet(t);
				if(messagesRead%500==0) {
					System.out.println("Read "+messagesRead+" messages");
				}
			} else {
				System.out.println("Did not receive a message in 5 seconds");
			}
		}

		client.stop();

		// Print some stats
		System.out.printf("The client read %d messages!\n", client
				.getStatsTracker().getNumMessages());
	}

	@SuppressWarnings(value = { "unchecked" })
	private static Tweet extractTweet(String msg) {
		Tweet t = new Tweet();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> data = mapper.readValue(msg.getBytes(), Map.class);
			if (!data.containsKey("delete")) {
				//System.out.println(msg);

				t.setLanguage((String) data.get("lang"));
				t.setCreatedAt((String) data.get("created_at"));
				t.setText((String) data.get("text"));
				Map<String, Object> user = (Map<String, Object>) data.get("user");
				if (user != null) {
					Object o = user.get("screen_name");
					if (o != null)
						t.setUserID((String) o);
					else
						t.setUserID("N/A");
				} else
					t.setUserID("N/A");

				if(data.get("coordinates") !=null) {
					Map<String,Object> coordinates = (Map<String, Object>) data.get("coordinates");
					try{
						List<Double> longLat = (List<Double>) coordinates.get("coordinates");
						t.setLongitude(longLat.get(0));
						t.setLatitude(longLat.get(1));
					} catch(ClassCastException c) {
						System.err.println("Exception casting coordinates :"+ coordinates.get("coordinates"));
					}
				}
				if(data.get("place")!=null){
					//System.out.println("Place present" + data.get("place"));
					Map<String,Object> placeData = (Map<String, Object>) data.get("place");
					t.setPlaceType((String) placeData.get("place_type"));
					t.setPlaceName((String) placeData.get("name"));
					t.setPlaceCountry((String) placeData.get("country"));
				}
				if(data.get("entities")!=null) {
					Map<String,Object> entityData = (Map<String, Object>) data.get("entities");
					Object hashtags = entityData.get("hashtags");
					if (hashtags != null) {
						List<String> tags = (List<String>) hashtags;
						t.setHashtags(tags);
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return t;
	}

	private static void processTweet(Tweet t) {
		if(t==null)
			throw new IllegalArgumentException("Tweet cannot be null");
		
		if(isUsableFoursquareTweet(t)) {	
			printTweet(t);
			String foundPlaceName = findPlace(t);
			if(foundPlaceName != null) {
				persistPlaceUTF8(foundPlaceName, t);
			}
		}
	}

	private static void persistPlaceUTF8(String foundPlaceName, Tweet t)  {
		OutputStreamWriter writer1=null;
		BufferedWriter fbw1 = null;
		OutputStreamWriter writer2=null;
		BufferedWriter fbw2 = null;
		try {		
			writer1 = new OutputStreamWriter(new FileOutputStream("/tmp/data.csv", true), "UTF-8");
	        fbw1 = new BufferedWriter(writer1);   
		    fbw1.write(foundPlaceName +", " + t.getPlaceCountry() +", " + t.getLatitude() + ", " + t.getLongitude());   
		    fbw1.newLine();
		    fbw1.flush();
		    
			writer2 = new OutputStreamWriter(new FileOutputStream("/tmp/d3data.csv", true), "UTF-8");
	        fbw2 = new BufferedWriter(writer2);   
		    fbw2.write(t.getLatitude() + "," + t.getLongitude());   
		    fbw2.newLine();
		    fbw2.flush();		    
			System.out.println("Place persisted successfully");  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			 if(writer1!=null) {
				 try { 
					 writer1.close();
				 } catch(IOException ignored) {
					 
				 }
			 }
			 if(fbw1!=null) {
				 try {
					 fbw1.close();
				} catch (IOException ignored) {
				}
			 }
			 if(writer2!=null) {
				 try { 
					 writer2.close();
				 } catch(IOException ignored) {
					 
				 }
			 }
			 if(fbw2!=null) {
				 try {
					 fbw2.close();
				} catch (IOException ignored) {
				}
			 }
		}
	}
	
	private static String findPlace(Tweet t) {
		
		placesApiCaller p = new placesApiCaller();
		
		String name = t.getText();
		int start = name.indexOf("I'm at")+6;
		
		int end = name.indexOf("@");
		if(end < start)
			end = name.indexOf("(");
		if(end < start)
			end = name.indexOf("http://");
		if(end < start)
			return null;
		
		String searchName = name.substring(start, end).trim();
		if(searchName.isEmpty())
			return null;
		
		System.out.println("SearchName = ["+searchName+"]");
		
		int count = p.howManyExist(searchName, t.getLongitude(), t.getLatitude());
		
		if(count >= 1) {
			System.out.println("PlacesAPI Search Found ["+count+"] likely hits at these Coordinates. Skipping . . . ");
			return null;
		}
		else
			return searchName;
	}

	private static void printTweet(Tweet t) {
		System.out.println(t.getUserID() + "\t" + t.getLanguage() + "\t" + t.getText());
		System.out.println("Coordinates: " + t.getLatitude() + "," + t.getLongitude());
		if(t.getPlaceName() != null) System.out.println("Place Details:" + t.getPlaceType() + "\t" + t.getPlaceName() + "\t" + t.getPlaceCountry());
		if(t.getTags()!=null && !t.getTags().isEmpty()) System.out.println("Tags : " + t.getTags());
		
		System.out.println("");
	}

	private static boolean isUsableFoursquareTweet(Tweet t) {

		if(t.getText()!=null && t.getLatitude()!=null && t.getText().contains("I'm at"))
			return true;
		else
			return false;
	}
	
	public static void main(String[] args) {
		try {

			String consumerKey = "R2srtfc5kEIxuQfiyG9pEA";
			String consumerSecret = "0id5kQT8ZSkQuNRBDhIlKOrE3sKvPDGAfmgIVVZPI";
			String accessToken = "312942836-izeK4pfEQGOt48ySWyKYRQJBdgz716cFFcDqh1eg";
			String accessTokenSecret = "d99NY9IjkygnrPtAqafn77aDyqp0PAhztSaXhqyJE40fp";

			HosebirdService.oauth(consumerKey, consumerSecret, accessToken,
					accessTokenSecret);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}
}
