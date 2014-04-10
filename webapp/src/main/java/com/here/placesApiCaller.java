package com.here;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;


public class placesApiCaller {

	private static String baseUrl = "http://places.cit.api.here.com/places/v1/discover/search";
	private static String appId = "4614bb9a70b0ec424d5d8810004c4b0b";
	private static String appCode = "TvglnNNykdPcXdt61yCiCA";
	private static Boolean printFlag = false;
	
	public static void setPrint(Boolean value){
		printFlag = value;
	}
	
    static String readFromInputStream(InputStream input) throws IOException {
        java.util.Scanner scanner = new java.util.Scanner(input).useDelimiter("\\A");
        String result = "";
        
        if (scanner.hasNext()) {
            result = scanner.next();    
        }
        
        return result;        
    }
	
	private static String getUrl(String name, double longitude, double latitude){
		
		name = java.net.URLEncoder.encode(name);
		String url = baseUrl 
				+"?app_id="+appId+"&app_code="+appCode
				+"&at="+longitude+","+latitude
				+"&q="+name+"&pretty";

		if(printFlag){ System.out.println("URL = "+url); }
		return url;
		
	}
	
	//Overload for no long/lat
	//Not useful?  needs at query term.
	private static String getUrl(String name, String city, String locale)
	{
		String url = baseUrl 
				+"?app_id="+appId+"&app_code="+appCode
				+"&q="+name+"%20"+city+"%20"+locale+"&pretty";
		
		if(printFlag){ System.out.println("URL = "+url+"\n"); }
		return url;
	}

	private static String getPlaceObjectList(String url){
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("accept", "application/json");
            String json = readFromInputStream(connection.getInputStream());

    		if(printFlag){ System.out.println(json); }
            return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static Map<String,Object> parseJsonObjectList(String placeObjectList) 
			throws JsonParseException, JsonMappingException, IOException{
		Map<String,Object> map = new HashMap<String,Object>();
		ObjectMapper mapper = new ObjectMapper();
		 
		//convert JSON string to Map
		map = mapper.readValue(placeObjectList, 
		new TypeReference<HashMap<String,Object>>(){});
		
		return map;
	}
	
	private static int countResults(Map<String,Object> jsonObject)
	{
		Map<String,Object> results = (Map<String,Object>) jsonObject.get("results");
		List items = (List) results.get("items");
		return items.size();
	}
	
	public static int howManyExist(String name, double lo, double la)
	{

		String url = getUrl(name, lo, la);
		String json = getPlaceObjectList(url);
		try {
			Map<String,Object> jsonObjects = parseJsonObjectList(json);
			return countResults(jsonObjects);
			
			
		} catch (Exception e) {
			e.printStackTrace();		
			return -1;
		}
	}
	
	public static void main(String[] args)
			throws JsonParseException, JsonMappingException, IOException{

		System.out.println(howManyExist("Speakers",52.5044,13.3909));
		
	}
	
	
}