package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import entity.payment.CreditCard;
import entity.payment.PaymentTransaction;

/**
 * class cung cap cac phuong thuc giup gui request len sever va nhan du lieu tra ve
 * Date 12/10/2021
 * @author TuanDH
 * @version 1.0
 */
public class API {

	/**
	 * Thuoc tinh giup format ngay thang tho dinh dang
	 * @author TuanDH
	 */
	public static DateFormat DATE_FORMATER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	/**
	 * Thuoc tinh giup log ra thong tin ra console
	 * @author TuanDH
	 */
	private static Logger LOGGER = Utils.getLogger(Utils.class.getName());

	/**
	 * @author TuanDH
	 * Phuong thuc giup goi cac api dang GET
	 * @param url: duong dan toi sever can request
	 * @param token: doan ma bam can cung cap de xac thuc nguoi dung
	 * @return: respone: phan hoi tu sever (data string)
	 * @throws Exception
	 */
	public static String get(String url, String token) throws Exception {
		// phan 1: setup
		HttpURLConnection conn = setupConnection(url, "GET", token);
		// phan 2: doc du lieu tra ve tu server
		return readRespne(conn);
	}

	int var;

	/**
	 * @author TuanDH
	 * Phuong thuc giup goi cac api dang POST (thanh toan,..)
	 * @param url: duong dan toi sever can request
	 * @param data: du lieu dua len server de xu y (dang JSON)
	 * @return respone: phan hoi tu server (dang string)
	 * @throws IOException
	 */
	public static String post(String url, String data
	) throws IOException {
		allowMethods("PATCH");
		// phan 1: setup
		HttpURLConnection conn = setupConnection(url, "PATCH", null);
		// phan 2: gui du lieu
		Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		writer.write(data);
		writer.close();
		// phan 3: doc du lieu gui ve tu server
		return readRespne(conn);
	}

	private static String readRespne(HttpURLConnection conn) throws IOException {
		BufferedReader in;
		String inputLine;
		if (conn.getResponseCode() / 100 == 2) {
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder response = new StringBuilder(); // su dung String Builder cho viec toi uu ve mat bo nho
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		in.close();
		LOGGER.info("Respone Info: " + response.toString());
		return response.toString();
	}

	private static HttpURLConnection setupConnection(String url, String method, String token)
			throws MalformedURLException, IOException, ProtocolException {
		URL line_api_url = new URL(url);
//		LOGGER.info("Request Info:\nRequest URL: " + url + "\n" + "Payload Data: " + data + "\n");
		HttpURLConnection conn = (HttpURLConnection) line_api_url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Bearer " + token);
		return conn;
	}

	/**
	 * @author TuanDH
	 * Phuong thuc cho phep goi cac loai giao thuc API khac nhau nhu PATCH, PUT,.. (chi hoat dong voi Java 11)
	 * @deprecated chi hoat dong voi Java <=11
	 * @param methods: giao thuc can cho phep (PATCH, PUT,..)
	 */
	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
			methodsField.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);

			methodsField.set(null/* static field */, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}
