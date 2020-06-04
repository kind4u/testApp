package com.nids.util.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.interfaces.NetworkCallBackInterface;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CommunicationUtil {

	//public static final String root_url_goorm = "https://spring-nids-kglhs.run.goorm.io";
	public static final String root_url_goorm = "https://nids-spring-psdg.run.goorm.io";
	public static final String root_url_aws = "http://nidsprojtestapp.372fabauwi.us-east-1.elasticbeanstalk.com";
	public static final String server_url = root_url_goorm;

	boolean stop_flag = false;
	Thread receiver_t;
	String str_response = "";
	String auth = "";

	NetworkCallBackInterface callback_Instance;
	JoinCallBackInterface joincallback_Instance;

	public CommunicationUtil(NetworkCallBackInterface callback_Instance) {
		this.callback_Instance = callback_Instance;
	}

	public CommunicationUtil(JoinCallBackInterface joincallback_Instance) {
		this.joincallback_Instance = joincallback_Instance;
	}

	public void registCar(String num, String id, int model){
		Thread t = new Thread(new CarInfo(num, id, model));
		t.start();
	}

	public void deleteCar(String num, String id, int model){
		Thread t = new Thread(new DeleteCar(num, id, model));
		t.start();
	}

	public void editCar(String num, String id, int model){
		Thread t = new Thread(new EditCar(num, id, model));
		t.start();
	}

	public void checkCar(String id){
		Thread t = new Thread(new CheckCar(id));
		t.start();
	}

	public void signUp(String id, String pw, String name, String zip_code, String addr, String addr_detail, int gender, String tmX, String tmY)	{
		Thread t = new Thread(new UserJoin(id, pw, name, zip_code, addr, addr_detail, gender, tmX, tmY));
		t.start();
	}

	public void naverSignUp(String id, String name, String age)	{
		Thread t = new Thread(new NaverUserJoin(id, name, age));
		t.start();
	}

	public void findPosition(String amdCd, String rnMgtSn, String udrtYn, String buldMnnm, String buldSlno) {
		Thread t = new Thread(new Position(amdCd, rnMgtSn, udrtYn, buldMnnm, buldSlno));
		t.start();
	}

	public void checkExist(String id) {
		Thread t = new Thread(new CheckUser(id));
		t.start();
	}

	public void signIn(String id, String pw) {
		Thread t = new Thread(new UserAuth(id, pw));
		t.start();
	}

	public void findData(String id) {
		Thread t = new Thread(new LoadData(id));
		t.start();
	}

	// GPS 위치측정 값 기반으로 가까운 측정소 검색 스레드 생성
	public void findStationWithGPS(String lat, String lon) {
		Thread t = new Thread(new StationGPS(lat, lon));
		t.start();
	}

	public void getInDoorData(String id)	{
		Thread t = new Thread(new InDoor(id));
		t.start();
	}

	// parameter 측정소의 미세먼지 데이터 값 추출 스레드 생성
	public void getOutDoorData(String station_name) {
		Thread t = new Thread(new OutDoor(station_name));
		t.start();
	}

	public void modifyPassword(String id, String pw)	{
		Thread t = new Thread(new ModPw(id, pw));
		t.start();
	}

	public void runReceiverThread(String auth) {
		receiver_t = new Thread(new Receiver(auth));
		receiver_t.start();
	}

	public void stopReceiverThread() {
		if (receiver_t != null && !stop_flag) {
			stop_flag = true;
		}
	}

	public class CarInfo implements Runnable{
		String num;
		String id;
		int model;

		public CarInfo(String num, String id, int model){this.num =num; this.id=id; this.model = model;}

		@Override
		public void run(){
			try {
				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/CarUtil");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "registCar"));
					nameValuePairs.add(new BasicNameValuePair("num", this.num));
					nameValuePairs.add(new BasicNameValuePair("id", this.id));
					nameValuePairs.add(new BasicNameValuePair("model", Integer.toString(this.model)));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);

					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					boolean post_insert = jsonObj.get("insert").getAsBoolean();
					String result = jsonObj.get("result").getAsString();

					System.out.println("post insert : " + String.valueOf(post_insert));

					joincallback_Instance.carResult(post_insert, result,"Success");
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					joincallback_Instance.carResult(false, "500", "ClientProtocolException");
				} catch (IOException e) {
					e.printStackTrace();
					joincallback_Instance.carResult(false, "500", "IOException");
				}
			} catch (Exception e)	{
				e.printStackTrace();
				joincallback_Instance.carResult(false,"500", "httpClientException");
			}
		}
	}

	public class DeleteCar implements Runnable{
		String num;
		String id;
		int model;

		public DeleteCar(String num, String id, int model){this.num =num; this.id=id; this.model = model;}

		@Override
		public void run(){
			try {
				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/CarUtil");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "deleteCar"));
					nameValuePairs.add(new BasicNameValuePair("num", this.num));
					nameValuePairs.add(new BasicNameValuePair("id", this.id));
					nameValuePairs.add(new BasicNameValuePair("model", Integer.toString(this.model)));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);

					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					boolean post_delete = jsonObj.get("delete").getAsBoolean();
					String result = jsonObj.get("result").getAsString();

					System.out.println("post delete : " + String.valueOf(post_delete));

					joincallback_Instance.deleteCarResult(post_delete, result,"Success");
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					joincallback_Instance.deleteCarResult(false, "500", "ClientProtocolException");
				} catch (IOException e) {
					e.printStackTrace();
					joincallback_Instance.deleteCarResult(false, "500", "IOException");
				}
			} catch (Exception e)	{
				e.printStackTrace();
				joincallback_Instance.deleteCarResult(false,"500", "httpClientException");
			}
		}
	}

	public class EditCar implements Runnable{
		String num;
		String id;
		int model;

		public EditCar(String num, String id, int model){this.num =num; this.id=id; this.model = model;}

		@Override
		public void run(){
			try {
				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/CarUtil");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "editCar"));
					nameValuePairs.add(new BasicNameValuePair("num", this.num));
					nameValuePairs.add(new BasicNameValuePair("id", this.id));
					nameValuePairs.add(new BasicNameValuePair("model", Integer.toString(this.model)));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);

					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					boolean post_edit = jsonObj.get("edit").getAsBoolean();
					String result = jsonObj.get("result").getAsString();

					System.out.println("post edit : " + String.valueOf(post_edit));

					joincallback_Instance.editCarResult(post_edit, result,"Success");
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					joincallback_Instance.editCarResult(false, "500", "ClientProtocolException");
				} catch (IOException e) {
					e.printStackTrace();
					joincallback_Instance.editCarResult(false, "500", "IOException");
				}
			} catch (Exception e)	{
				e.printStackTrace();
				joincallback_Instance.editCarResult(false,"500", "httpClientException");
			}
		}
	}

	public class CheckCar implements Runnable {
		String id;

		public CheckCar(String id) {
			this.id = id;
		}

		@Override
		public void run() {
			try {
				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/CarUtil");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "checkCar"));
					nameValuePairs.add(new BasicNameValuePair("id", this.id));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);

					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					boolean exist = jsonObj.get("exist").getAsBoolean();
					String result = jsonObj.get("result").getAsString();

					joincallback_Instance.checkCarResult(result, exist);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					joincallback_Instance.checkCarResult("error", true);
				} catch (IOException e) {
					e.printStackTrace();
					joincallback_Instance.checkCarResult("error", true);
				}
			} catch (Exception e) {
				e.printStackTrace();
				joincallback_Instance.checkCarResult("error", true);
			}
		}
	}

	public class CheckUser implements Runnable {
		String id;

		public CheckUser(String id) {
			this.id = id;
		}

		@Override
		public void run() {
			try {
				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/UserUtil");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "check_exist"));
					nameValuePairs.add(new BasicNameValuePair("id", this.id));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);

					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					boolean exist = jsonObj.get("exist").getAsBoolean();
					String result = jsonObj.get("result").getAsString();

					joincallback_Instance.existResult(result, exist);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					joincallback_Instance.existResult("error", true);
				} catch (IOException e) {
					e.printStackTrace();
					joincallback_Instance.existResult("error", true);
				}
			} catch (Exception e) {
				e.printStackTrace();
				joincallback_Instance.existResult("error", true);
			}
		}
	}

	public class LoadData implements Runnable {
		String id;

		public LoadData(String id) {
			this.id = id;
		}

		public void run() {
			try {

				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/DataLoad");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "withid"));
					nameValuePairs.add(new BasicNameValuePair("id", this.id));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);


					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();


					String post_result = jsonObj.get("result").getAsString();
					String timeStamp = jsonObj.get("date").getAsString();
					String resp_data = jsonObj.get("data").getAsString();		// VOOutdoor 객체
					String lat = jsonObj.get("lat").getAsString();
					String lon = jsonObj.get("lon").getAsString();

					VOSensorData sensorData = new VOSensorData(resp_data, timeStamp, lat, lon);

					List<VOSensorData> dataList = new ArrayList<VOSensorData>();
					dataList.add(sensorData);

					callback_Instance.dataReqResult(post_result, dataList);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					callback_Instance.dataReqResult("500", null);
				} catch (IOException e) {
					e.printStackTrace();
					callback_Instance.dataReqResult("500", null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				callback_Instance.dataReqResult("500", null);
			}
		}
	}

	public class InDoor implements Runnable	{
		String id;

		public InDoor(String id) {this.id = id;}

		@Override
		public void run() {
			try	{
				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/DataLoad");
				try	{
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "withid"));		// 모바일 전용으로 매핑
					nameValuePairs.add(new BasicNameValuePair("id", URLEncoder.encode(this.id, "utf-8")));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);			// station 파라미터로 넘긴 측정소의 미세먼지 데이터 값 전송


					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					String post_result = jsonObj.get("result").getAsString();
					String timeStamp = jsonObj.get("date").getAsString();
					String resp_data = jsonObj.get("data").getAsString();		// VOOutdoor 객체
					String lat_data = jsonObj.get("lat").getAsString();
					String lon_data = jsonObj.get("lon").getAsString();


					VOSensorData sensorData = new VOSensorData(resp_data, timeStamp, lat_data, lon_data);
					//VOSensorData sensorData = new VOSensorData(resp_data, lat_data, lon_data);

					List<VOSensorData> dataList = new ArrayList<VOSensorData>();
					dataList.add(sensorData);

					callback_Instance.dataReqResult(post_result, dataList);		// MainActivity의 callback 메소드 호출
				} catch (IOException e) {
					e.printStackTrace();
					callback_Instance.dataReqResult("500", null);
				} catch (ParseException e) {
					e.printStackTrace();
					callback_Instance.dataReqResult("500", null);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					callback_Instance.dataReqResult("500", null);
				}

			}	catch(Exception e)	{
				e.printStackTrace();
				callback_Instance.dataReqResult("500", null);
			}
		}
	}

	// 측정소의 미세먼지 데이터 값 수신
	public class OutDoor implements Runnable {

		String station;

		public OutDoor(String station) {
			this.station = station;
		}

		public void run() {
			try {

				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/DataLoad");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "outdoor_mobile"));		// 모바일 전용으로 매핑
					nameValuePairs.add(new BasicNameValuePair("station", URLEncoder.encode(this.station, "utf-8")));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);			// station 파라미터로 넘긴 측정소의 미세먼지 데이터 값 전송


					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					boolean post_result = jsonObj.get("result").getAsBoolean();
					String resp_data = jsonObj.get("data").getAsString();		// VOOutdoor 객체

					VOOutdoor out_data = new VOOutdoor(resp_data);
					out_data.setStationName(this.station);

					callback_Instance.dataReqResultOutdoor(post_result, out_data);		// MainActivity의 callback 메소드 호출
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					callback_Instance.dataReqResultOutdoor(false, null);
				} catch (IOException e) {
					e.printStackTrace();
					callback_Instance.dataReqResultOutdoor(false, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				callback_Instance.dataReqResultOutdoor(false, null);
			}
		}
	}

	public class UserAuth implements Runnable {
		String id;
		String pw;

		public UserAuth(String id, String pw) {
			this.id = id;
			this.pw = pw;
			Log.d("Auth", this.id);
			Log.d("Auth", this.pw);
		}

		@Override
		public void run() {
			try {

				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
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
					e.printStackTrace();
					callback_Instance.signInResult(false, "Connection Error", null);
				} catch (IOException e) {
					e.printStackTrace();
					callback_Instance.signInResult(false, "Connection Error", null);
				}

			} catch (Exception e) {
				e.printStackTrace();
				callback_Instance.signInResult(false, "Connection Error", null);
			}
		}
	}

	// GPS 정보 기반 가까운 측정소 정보 추출 (wgs84 위경도 기준 사용)
	public class StationGPS implements Runnable {
		String lat;
		String lon;

		public StationGPS(String lat, String lon) {
			this.lat = lat;
			this.lon = lon;
		}

		@Override
		public void run() {
			try {

				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/UserUtil");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("type", "stationgps"));		// goorm 서버 내 stationgps 메소드 매핑
					nameValuePairs.add(new BasicNameValuePair("lat", this.lon));
					nameValuePairs.add(new BasicNameValuePair("lon", this.lat));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);		// 가까운 측정소 정보가 담긴 entity 패키지


					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();


					boolean post_result = jsonObj.get("result").getAsBoolean();
					String str_data = jsonObj.get("data").getAsString();		// 측정소 정보가 담긴 VOStation 객체

					VOStation vos = new VOStation(str_data);

					callback_Instance.findStation(post_result, vos);			// MainActivity의 callback 메소드 호출
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					callback_Instance.findStation(false, null);
				} catch (IOException e) {
					e.printStackTrace();
					callback_Instance.findStation(false, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				callback_Instance.findStation(false, null);
			}
		}
	}

	public class Receiver implements Runnable {

			int delay = 30000;
			String auth;

			public Receiver(String auth) {
				this.auth = auth;
			}

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

				String post_result = jsonObj.get("result").getAsString();
				List<VOSensorData> data_arr = (List<VOSensorData>) gson.fromJson(jsonObj.get("data").toString(), new TypeToken<List<VOSensorData>>() {
				}.getType());
				Log.d("data size", String.valueOf(data_arr.size()));
				System.out.println("post result : " + String.valueOf(post_result));

				if (post_result == "0") {
					callback_Instance.dataReqResult(post_result, data_arr);
				}
			}

			@Override
			public void run() {

				while (!stop_flag) {
					try {
						//System.out.println("running..");
						send();
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						Log.d("CommunicationUtil", "Thread Interrupted");
						e.printStackTrace();
						//e.printStackTrace();
					} catch (Exception e) {
						Log.d("CommunicationUtil", "Thread Exception Occurred");
						e.printStackTrace();
					}
				}
				Log.d("CommunicationUtil", "Thread End");
			}
		}

		public class UserJoin implements Runnable {
			String id;
			String pw;
			String name;
			String zip_code;
			String addr;
			String addr_detail;
			int gender;
			String tmX;
			String tmY;

			UserJoin(String id, String pw, String name, String zip_code, String addr, String addr_detail, int gender, String tmX, String tmY) {
				this.id = id;
				this.pw = pw;
				this.name = name;
				this.zip_code = zip_code;
				this.addr = addr;
				this.addr_detail = addr_detail;
				this.gender = gender;
				this.tmX = tmX;
				this.tmY = tmY;
			}

			@Override
			public void run() {
				try {
					HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
					httpclient.getParams().setParameter("http.protocol.expect-continue", false);
					httpclient.getParams().setParameter("http.connection.timeout", 5000);
					httpclient.getParams().setParameter("http.socket.timeout", 5000);

					HttpPost httppost = new HttpPost(server_url + "/UserUtil");
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
						nameValuePairs.add(new BasicNameValuePair("type", "Register"));
						nameValuePairs.add(new BasicNameValuePair("id", this.id));
						nameValuePairs.add(new BasicNameValuePair("pw", this.pw));
						nameValuePairs.add(new BasicNameValuePair("name", this.name));
						nameValuePairs.add(new BasicNameValuePair("addr1", this.zip_code));
						nameValuePairs.add(new BasicNameValuePair("addr2", this.addr));
						nameValuePairs.add(new BasicNameValuePair("addr3", this.addr_detail));
						nameValuePairs.add(new BasicNameValuePair("gender", Integer.toString(this.gender)));
						nameValuePairs.add(new BasicNameValuePair("tmX", this.tmX));
						nameValuePairs.add(new BasicNameValuePair("tmY", this.tmY));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
						// Execute HTTP Post Request
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						str_response = EntityUtils.toString(entity);

						System.out.println(str_response);

						JsonParser parser = new JsonParser();
						JsonElement element = parser.parse(str_response);
						JsonObject jsonObj = element.getAsJsonObject();

						boolean post_insert = jsonObj.get("insert").getAsBoolean();
						String result = jsonObj.get("result").getAsString();

						System.out.println("post insert : " + String.valueOf(post_insert));

						joincallback_Instance.signUpResult(post_insert, result,null);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						joincallback_Instance.signUpResult(false, "500", "ClientProtocolException");
					} catch (IOException e) {
						e.printStackTrace();
						joincallback_Instance.signUpResult(false, "500", "IOException");
					}
				} catch (Exception e) {
					e.printStackTrace();
					joincallback_Instance.signUpResult(false, "500", "httpClientException");
				}

			}
		}

	public class NaverUserJoin implements Runnable {
		String id;
		String name;
		String age;

		NaverUserJoin(String id, String name, String age) {
			this.id = id;
			this.name = name;
			this.age = age;
		}

		@Override
		public void run() {
			try {
				HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
				httpclient.getParams().setParameter("http.protocol.expect-continue", false);
				httpclient.getParams().setParameter("http.connection.timeout", 5000);
				httpclient.getParams().setParameter("http.socket.timeout", 5000);

				HttpPost httppost = new HttpPost(server_url + "/UserUtil");
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
					nameValuePairs.add(new BasicNameValuePair("type", "NaverRegister"));
					nameValuePairs.add(new BasicNameValuePair("id", this.id));
					nameValuePairs.add(new BasicNameValuePair("name", this.name));
					nameValuePairs.add(new BasicNameValuePair("ager", this.age));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					str_response = EntityUtils.toString(entity);

					System.out.println(str_response);

					JsonParser parser = new JsonParser();
					JsonElement element = parser.parse(str_response);
					JsonObject jsonObj = element.getAsJsonObject();

					boolean post_insert = jsonObj.get("insert").getAsBoolean();
					String result = jsonObj.get("result").getAsString();

					System.out.println("post insert : " + String.valueOf(post_insert));

					joincallback_Instance.signUpResult(post_insert, result,null);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					joincallback_Instance.signUpResult(false, "500", "ClientProtocolException");
				} catch (IOException e) {
					e.printStackTrace();
					joincallback_Instance.signUpResult(false, "500", "IOException");
				}
			} catch (Exception e) {
				e.printStackTrace();
				joincallback_Instance.signUpResult(false, "500", "httpClientException");
			}

		}
	}

		public class Position implements Runnable {        // 좌표 찾기 위한 함수
			String amdCd;
			String rnMgtSn;
			String udrtYn;
			String buldMnnm;
			String buldSlno;

			Position(String amdCd, String rnMgtSn, String udrtYn, String buldMnnm, String buldSlno) {
				this.amdCd = amdCd;
				this.rnMgtSn = rnMgtSn;
				this.udrtYn = udrtYn;
				this.buldMnnm = buldMnnm;
				this.buldSlno = buldSlno;
			}

			@Override
			public void run() {
				try {
					HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
					httpclient.getParams().setParameter("http.protocol.expect-continue", false);
					httpclient.getParams().setParameter("http.connection.timeout", 5000);
					httpclient.getParams().setParameter("http.socket.timeout", 5000);

					HttpPost httppost = new HttpPost(server_url + "/UserUtil");
					try {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
						nameValuePairs.add(new BasicNameValuePair("type", "position"));
						nameValuePairs.add(new BasicNameValuePair("admCd", this.amdCd));
						nameValuePairs.add(new BasicNameValuePair("rnMgtSn", this.rnMgtSn));
						nameValuePairs.add(new BasicNameValuePair("udrtYn", this.udrtYn));
						nameValuePairs.add(new BasicNameValuePair("buldMnnm", this.buldMnnm));
						nameValuePairs.add(new BasicNameValuePair("buldSlno", this.buldSlno));
						httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
						// Execute HTTP Post Request
						HttpResponse response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						str_response = EntityUtils.toString(entity);

						System.out.println(str_response);

						JsonParser parser = new JsonParser();
						JsonElement element = parser.parse(str_response);
						JsonObject jsonObj = element.getAsJsonObject();

						boolean position_result = jsonObj.get("result").getAsBoolean();
						String position_data = jsonObj.get("data").getAsString();

						System.out.println("post insert : " + String.valueOf(position_result));

						joincallback_Instance.positionResult(position_result, position_data);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						joincallback_Instance.positionResult(false, "ClientProtocolException");
					} catch (IOException e) {
						e.printStackTrace();
						joincallback_Instance.positionResult(false, "IOException");
					}
				} catch (Exception e) {
					e.printStackTrace();
					joincallback_Instance.positionResult(false, "httpClientException");
				}

			}
		}

		public class ModPw implements Runnable	{
		String id;
		String pw;

		ModPw(String id, String pw)	{
			this.id = id;
			this.pw = pw;
		}

			@Override
			public void run() {
				try {

					HttpClient httpclient = new DefaultHttpClient();//HttpClientBuilder.create().build();
					httpclient.getParams().setParameter("http.protocol.expect-continue", false);
					httpclient.getParams().setParameter("http.connection.timeout", 5000);
					httpclient.getParams().setParameter("http.socket.timeout", 5000);

					HttpPost httppost = new HttpPost(server_url + "/UserUtil");
					try {
						// Add your data
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
						nameValuePairs.add(new BasicNameValuePair("type", "modPw"));
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

						System.out.println("post result : " + String.valueOf(post_result));

						callback_Instance.modifyResult(post_result);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						callback_Instance.modifyResult(false);
					} catch (IOException e) {
						e.printStackTrace();
						callback_Instance.modifyResult(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
					callback_Instance.modifyResult(false);
				}
			}
		}
	}
