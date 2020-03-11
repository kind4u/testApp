package com.nids.util.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.util.interfaces.NetworkCallBackInterface;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CommunicationUtil{

	//public static final String root_url_goorm = "https://spring-nids-kglhs.run.goorm.io";
	public static final String root_url_goorm = "https://nids-spring-psdg.run.goorm.io";
	public static final String root_url_aws = "http://nidsprojtestapp.372fabauwi.us-east-1.elasticbeanstalk.com";
	public static final String server_url = root_url_goorm;

	boolean stop_flag = false;
	Thread receiver_t;
	String str_response = "";
	String auth = "";

	NetworkCallBackInterface callback_Instance;
	public CommunicationUtil(NetworkCallBackInterface callback_Instance) {
		this.callback_Instance = callback_Instance;
	}
	
	public void signIn(String id, String pw){
		Thread t = new Thread(new UserAuth(id, pw));
		t.start();
	}

	public void findStation(String id){
		Thread t = new Thread(new Station(id));
		t.start();
	}

	public void getOutDoorData(String station_name){
		Thread t = new Thread(new OutDoor(station_name));
		t.start();
	}

	public void runReceiverThread(String auth){
		receiver_t = new Thread(new Receiver(auth));
		receiver_t.start();
	}

	public void stopReceiverThread(){
		if(receiver_t != null && !stop_flag){
			stop_flag = true;
		}
	}


	public class Station implements Runnable {
		String id;
		public Station(String id){
			this.id = id;
		}

		public void run(){
			// TODO Auto-generated method stub
			try {

				HttpClient httpclient =  new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/UserUtil");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "station"));
					nameValuePairs.add(new BasicNameValuePair("user_id", this.id));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);


					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();


					boolean post_result = jsonObj.get("result").getAsBoolean();
					String str_data = jsonObj.get("data").getAsString();

					VOStation vos = new VOStation(str_data);

					callback_Instance.findStation(post_result, vos);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					callback_Instance.findStation(false,  null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					callback_Instance.findStation(false, null);
				}
			}
			catch(Exception e) {
				callback_Instance.signInResult(false, "Connection Error", null);
			}
		}
	}

	public class OutDoor implements Runnable {

		String station;
		public OutDoor(String station){
			this.station = station;
		}

		public void run(){
			try {

				HttpClient httpclient =  new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/DataLoad");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "outdoor_mobile"));
					nameValuePairs.add(new BasicNameValuePair("station", URLEncoder.encode(this.station, "utf-8")));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);


					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					boolean post_result = jsonObj.get("result").getAsBoolean();
					String resp_data = jsonObj.get("data").getAsString();

					VOOutdoor out_data = new VOOutdoor(resp_data);

					callback_Instance.dataReqResultOutdoor(post_result, out_data);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					callback_Instance.dataReqResultOutdoor(false, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					callback_Instance.dataReqResultOutdoor(false, null);
				}
			}
			catch(Exception e) {
				callback_Instance.dataReqResultOutdoor(false, null);
			}
		}
	}

	public class UserAuth implements Runnable {
		String code;
		String id;
		String pw;
		public UserAuth(String id, String pw) { this.id = id; this.pw = pw; Log.d("Auth", this.id); Log.d("Auth", this.pw);}
		public UserAuth(String code) {
			this.code = code;
		}

		@Override
		public void run(){
			// TODO Auto-generated method stub
			try {

				HttpClient httpclient =  new DefaultHttpClient();//HttpClientBuilder.create().build();
                httpclient.getParams().setParameter("http.protocol.expect-continue", false);
                httpclient.getParams().setParameter("http.connection.timeout", 5000);
                httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/UserUtil");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "signin"));
					nameValuePairs.add(new BasicNameValuePair("id", this.id));
					nameValuePairs.add(new BasicNameValuePair("pw", this.pw));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);


					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();


					boolean post_result = jsonObj.get("result").getAsBoolean();
					String message = jsonObj.get("message").getAsString();


					Gson gson = new Gson();
					VOUser user_info = gson.fromJson(jsonObj.get("user_info").toString(), VOUser.class);


					Log.d("user info", user_info.getName());
					//System.out.println("user name" + user_info.getName());

					System.out.println("post result : " + String.valueOf(post_result));

					callback_Instance.signInResult(post_result, message, user_info);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					callback_Instance.signInResult(false, "Connection Error", null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					callback_Instance.signInResult(false, "Connection Error", null);
				}

			}
			catch(Exception e) {
				callback_Instance.signInResult(false, "Connection Error", null);
			}
		}
	}

	public class Receiver implements Runnable {
		
		int delay = 30000;
		String auth;
		public Receiver(String auth) { this.auth = auth; }
		
		public Receiver(int delay) {
			this.delay = delay;
		}
		
		public void send() throws Exception {
			//System.out.println("send ready");
				
			URI uri = new URI(server_url + "/DataLoad");
			uri = new URIBuilder(uri).addParameter("type", "withauth")
									 .addParameter("auth", this.auth)
									 .build();

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpResponse response = httpClient.execute(new HttpPost(uri));
			HttpEntity entity = response.getEntity();
			str_response = EntityUtils.toString(entity);

			System.out.println(str_response);

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(str_response);
			JsonObject jsonObj = element.getAsJsonObject();

			//JSONParser parser = new JSONParser();
			//JSONObject jsonObj = (JSONObject) parser.parse(str_response);

			Gson gson = new Gson();

			boolean post_result = jsonObj.get("result").getAsBoolean();
			List<VOSensorData> data_arr = (List<VOSensorData>)gson.fromJson(jsonObj.get("data").toString(), new TypeToken<List<VOSensorData>>(){}.getType());
			Log.d("data size", String.valueOf(data_arr.size()));
			System.out.println("post result : " + String.valueOf(post_result));

			if(post_result)
			{
				callback_Instance.dataReqResult(post_result, data_arr);
			}
		}
		
		@Override
		public void run() {

			while(!stop_flag) 
			{
				try {
					//System.out.println("running..");
					send();
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//System.out.println("InterruptedException");
					Log.d("CommunicationUtil", "Thread Interrupted");
					//e.printStackTrace();
				}
				catch(Exception e) {
					//System.out.println("Exception");
					Log.d("CommunicationUtil", "Thread Exception Occurred");
					//e.printStackTrace();
				}
			}
			Log.d("CommunicationUtil", "Thread End");
			//System.out.println("thread end");
		}
	}
}