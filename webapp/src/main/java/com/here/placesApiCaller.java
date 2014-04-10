package com.here;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.util.HashMap;


public class placesApiCaller {

	private static String baseUrl = "http://places.cit.api.here.com/places/v1/discover/search";
	private static String appId = "4614bb9a70b0ec424d5d8810004c4b0b";
	private static String appCode = "TvglnNNykdPcXdt61yCiCA";
	

	
    static String readFromInputStream(InputStream input) throws IOException {
        java.util.Scanner scanner = new java.util.Scanner(input).useDelimiter("\\A");
        String result = "";
        
        if (scanner.hasNext()) {
            result = scanner.next();    
        }
        
        return result;        
    }
	
	private static String getUrl(String name, double longitude, double latitude){
		
		String url = baseUrl 
				+"?app_id="+appId+"&app_code="+appCode
				+"&at="+longitude+","+latitude
				+"&q="+name+"&pretty";
		
		return url;
		
	}
	
	//Overload for no long/lat
	//Not useful?  needs at query term.
	private static String getUrl(String name, String city, String locale)
	{
		String url = baseUrl 
				+"?app_id="+appId+"&app_code="+appCode
				+"&q="+name+"%20"+city+"%20"+locale+"&pretty";
		return url;
	}

	private static String getPlaceObjectList(String url){
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("accept", "application/json");
            String json = readFromInputStream(connection.getInputStream());
			return json;		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args){
		//System.out.println(getUrl("PLACE",1.001,2.002));
		
		System.out.print(getPlaceObjectList("http://places.cit.api.here.com/places/v1/discover/search?app_id=4614bb9a70b0ec424d5d8810004c4b0b&app_code=TvglnNNykdPcXdt61yCiCA&at=52.5044,13.3909&q=Speakers&pretty"));
		
	}
	
	
}