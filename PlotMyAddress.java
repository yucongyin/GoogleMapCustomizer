

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.ObjectInputStream.GetField;
import java.lang.IllegalStateException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;


import com.google.gson.Gson;

import com.google.gson.JsonSyntaxException;


import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class PlotMyAddress {
	public static void main(String... args) {
		Person person = new Person();
		Address address = new Address();
		Scanner input = null;
		Formatter output = null;
		String[] records = null;
		String[] names = null;					
		String[] streetInfo = null;
		String[] cityProv = null;
		String nextLine;
		

		try {
			input = new Scanner(Paths.get("C:\\CST8284\\input\\InputAddresses.txt"));
			output = new Formatter("C:\\CST8284\\output\\OutputAddresses.csv");

			while(input.hasNext()) {
				nextLine = input.nextLine();

				while (nextLine != null) {
					records = new String[4];

					for (int i = 0; i< 4; i++) {
						records[i] = nextLine;
						if (input.hasNext()) 
						{
							nextLine=input.nextLine();
						}						
					}

					names = records[0].split("\\s*(\\s|,)\\s*");					
					streetInfo = records[1].split("\\s+");
					cityProv = records[2].split("\\s*(\\s|,)\\s*");		

					person.setFirstName(names[0]);
					person.setLastName(names[1]);

					if (names[1].equalsIgnoreCase("and")) {
						person.setLastName(names[3]);
					}

					if (names.length == 2) {						
						person.setSpouseFirstName("");
						person.setSpouseLastName("");
					} else {
						person.setSpouseFirstName(names[2]);
						person.setSpouseLastName(names[3]);
					}

					address.setStreetNumber(streetInfo[0]);
					address.setStreetName(streetInfo[1]);
					address.setStreetType(streetInfo[2]);

					if(streetInfo.length == 3) {
						address.setStreetOrientation("");
					} else {
						address.setStreetOrientation(streetInfo[3]);
					}

					address.setCityName(cityProv[0]);
					address.setProvince(cityProv[1]);

					//records[3] is postal code, ignore

					output.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n", 
							person.getFirstName(), 
							person.getLastName(),
							person.getSpouseFirstName(),
							person.getSpouseLastName(),
							address.getStreetNumber(),
							address.getStreetName(),
							address.getStreetType(),
							address.getStreetOrientation(),
							address.getCityName(),
							address.getProvince());

					if ( nextLine.length() == 0 && input.hasNext())
						nextLine =input.nextLine();

					if (!input.hasNext()) {
						break;
					}
				}
			}
		}
		catch(IOException | NoSuchElementException | IllegalStateException e) {
			System.out.println("main exception");
		}
		finally {
			input.close();
			output.close();			
		}
		
		
		readFiles();
		
	}//end of main
	//end of the solution to assignment2
	//Yucong_Yin's coding for assignment 3 starts here
	//create a static method to read the file.
	
	public static void readFiles()  {
		Person person = new Person();
		Address address = new Address();
		String nextline;
		String[] records = null;
		Scanner reader  = null;
		String apikey = null;
		String locationURL = null;
		String urladdress = null;
		String result = "";
		String response = "";
		List<String> json = new ArrayList<String>();
		Gson gson = new Gson();
		String[] urls = new String[12];
		 GoogleGeoCodeResponses geo = null;
		 int i = 0;
		 URL url = null;
		 Formatter out = null;
		try {
			out = new Formatter("C:\\CST8284\\output\\LatLong.csv");
			reader  = new Scanner(Paths.get("C:\\CST8284\\output\\OutputAddresses.csv"));
			out.format("Latitude,Longtitude,Name,Icon,IconScale,IconAltitude%n");

			while(reader.hasNext()) {
				nextline = reader.nextLine();//nextline stores eachline from the csv file
				
				records = nextline.split(",");
				
				person.setFirstName(records[0]);
				person.setLastName(records[1]);
				person.setSpouseFirstName(records[2]);
				person.setSpouseLastName(records[3]);
				address.setStreetNumber(records[4]);
				address.setStreetName(records[5]);
				address.setStreetType(records[6]);
				address.setStreetOrientation(records[7]);
				address.setCityName(records[8]);
				address.setProvince(records[9]);
				//generate the urls for http request
				if(address.getStreetOrientation() == "") 
					urladdress = String.format("%s+%s+%s,+%s,+%s,+CA", address.getStreetNumber(),address.getStreetName(),address.getStreetType(),address.getCityName(),address.getProvince());
				else
					urladdress = String.format("%s+%s+%s,+%s,+%s,+%s,+CA", address.getStreetNumber(),address.getStreetName(),address.getStreetType(),address.getStreetOrientation(),address.getCityName(),address.getProvince());		locationURL = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s%s",urladdress,apikey);
					apikey = "&key=AIzaSyBusutsNc2SnwktR801ILkmhyPa9L6n3HQ";
					locationURL = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s%s",urladdress,apikey);


					
						//store urls in an array just make it easier for me to run some tests
						urls[i] = locationURL;
						
						//connect to the api and read the output
						try {
							url = new URL(urls[i]);
							HttpURLConnection connection = (HttpURLConnection) url.openConnection();
							connection.setRequestMethod("GET");
							connection.setRequestProperty("Accept", "application/json");
							BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
							while ((response = br.readLine()) != null) {
							    
							    result += response;
							    
							}
							
							//Call the object of gson which is my library
							geo = gson.fromJson(result,GoogleGeoCodeResponses.class);
							
							double lat = Double.parseDouble(geo.results[0].geometry.location.lat);

							double lng = Double.parseDouble(geo.results[0].geometry.location.lng);
							System.out.println("Lat: "+lat+" Lng: "+lng);

							if(person.getSpouseFirstName().equals("") ||person.getSpouseLastName().equals("")) {
								out.format("%s,%s,%s,%s,%s,%s,%s%n",lat,lng,person.getFirstName(),person.getLastName(),"111","1","1");
							}
							else {
								out.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",lat,lng,person.getFirstName(),person.getLastName(),person.getSpouseFirstName(),person.getSpouseLastName(),"111","1","1");

							
							}
								
							response = "";
							result = "";
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							System.out.println("Blank line in csv, input file has wrong information.");
						}
						++i;



			}
			
				





			


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			reader.close();
			out.close();
			
		}
		
		
		


			
			
		

		

	
	}//end of readFiles
	
	
}//end of class


//NOTICE: This class is copies from an external source
//source link: https://stackoverflow.com/questions/7265833/how-to-serialize-and-deserialize-a-json-object-from-google-geocode-using-java
//Answer:2
//Author: Smile2Life
class GoogleGeoCodeResponses {
public String status;
public results[] results;

public GoogleGeoCodeResponses() {
}

public class results {
   public String formatted_address;
   public geometry geometry;
   public String[] types;
   public address_component[] address_components;
}

public class geometry {
   public bounds bounds;
   public String location_type;
   public location location;
   public bounds viewport;
}

public class bounds {

   public location northeast;
   public location southwest;
}

public class location {
   public String lat;
   public String lng;
}

public class address_component {
   public String long_name;
   public String short_name;
   public String[] types;
}}
