 
package in.apcfss.common;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

 @Component("commonutils")
public class CommonFunctionUtils
{

	public String formatfractionNumber(final String number) {
		return number.replaceAll("\\.0*$", "");
	}

	public static String validateFileName(final String fileName) {
		return fileName.replaceAll("\\s", "").replace("'", "").replace("\"", "").replace("°", "");
	}

	public static String formatDateStringToDatabaseDate(final String date) {
		String returnDate = null;
		try {
			final SimpleDateFormat sdDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			final SimpleDateFormat dataBaseFormat = new SimpleDateFormat("yyyy-MM-dd");
			returnDate = dataBaseFormat.format(sdDateFormat.parse(date));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return returnDate;
	}

	public Date formatDate(final String date, final String format) throws Exception {
		return new SimpleDateFormat(format).parse(date);
	}


	public static boolean isNull(final Object value) {
		return value == null || "".equals(value.toString().trim());
	}

	public static boolean validateString(final String value) {
		boolean flag = false;
		if (value != null && !value.trim().equalsIgnoreCase("null") && !value.trim().equals("")) {
			flag = true;
		}
		return flag;
	}

	public static String generateOTP() {
		final Random random = new Random();
		final int numbers = 100000 + (int)(random.nextFloat() * 899900.0f);
		return new StringBuilder(String.valueOf(numbers)).toString();
	}

	public static String sendSMS(final String isSingleBulk, final String mobileNumbers, final String message) {
		String statusCode = "";
		String getResponseCode = "";
		String getResponseMessage = "";
		String returnFlag = "";
		HttpURLConnection connection = null;
		try {
			final String query = "username=" + URLEncoder.encode("aplrs") + "&password=" + URLEncoder.encode("aplrs@172") + "&from=APDTCP&to=" + mobileNumbers + "&type=1" + "&msg=" + message;
			final URL url = new URL("https://www.smsstriker.com/API/sms.php");
			connection = (HttpURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			HttpURLConnection.setFollowRedirects(true);
			connection.setRequestProperty("Content-length", String.valueOf(query.length()));
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");
			final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			final int queryLength = query.length();
			output.writeBytes(query);
			final DataInputStream input = new DataInputStream(connection.getInputStream());
			for (int c = input.read(); c != -1; c = input.read()) {
				statusCode = String.valueOf(statusCode) + (char)c;
			}
			getResponseCode = new StringBuilder(String.valueOf(connection.getResponseCode())).toString();
			getResponseMessage = connection.getResponseMessage();
			returnFlag = String.valueOf(statusCode.trim()) + "-" + getResponseCode + "-" + getResponseMessage;
			input.close();
		}
		catch (Exception e) {
			System.out.println("Something bad just happened.");
			System.out.println(e);
			e.printStackTrace();
			returnFlag = null;
		}
		return returnFlag;
	}

 
	public boolean isNumeric(final String str) {
		if (str == null || str.length() == 0) {
			return false;
		}
		char[] charArray;
		for (int length = (charArray = str.toCharArray()).length, i = 0; i < length; ++i) {
			final char c = charArray[i];
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	public static void log(final String log) {
		System.out.println(log);
	}

	public String getFinYear() {
		Calendar cal=Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month =cal.get(Calendar.MONTH)+1;
		if(month<=3) {
			year=year-1;
		}
		String strYear=String.valueOf(year);
		return strYear;
	}
	public static String getCurrentDate()
	{
		Date date = new Date();
		String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
		return currentDate;
	}

	public Timestamp getCurrentTimeStamp() {
		Timestamp currentTimeStamp = new Timestamp(new Date().getTime());
		return currentTimeStamp;
	}

	public String getCurrentYear() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String strYear=String.valueOf(year);
		return strYear;
	}
 


	public static String getBase64Code(String base64,String docid,String user,String returnUrl) throws IOException {
		final URL url = new URL("https://esign.apcfss.in/tosign/getdocument");
		//	final URL url = new URL("https://esign.herb.apcfss.in/tosign/getdocument");
		final HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		returnUrl="https://acr.works.apcfss.in/"+returnUrl;
		// returnUrl="http://172.16.197.59:8080/ACR/"+returnUrl;
		final String input = "{\"docid\":\""+docid+"\",\"esignoption\":\"1\",\"user\":\""+user+"\",\"rurl\":\""+returnUrl+"\",\"base64\":\""+base64+"\"}";
		final OutputStream os = connection.getOutputStream();
		os.write(input.getBytes());
		os.flush();
		final InputStream content = connection.getInputStream();
		final BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line,result2="";
		while ((line = in.readLine()) != null) {
			result2=line;
		}
		System.out.println("result2--rr--"+result2);
		return result2;
	}


	public static void sentRedirect(String transid) throws IOException {
		//	final URL url = new URL("https://esign.apcfss.in/?"+transid);
		final URL url = new URL("https://esign.herb.apcfss.in/?"+transid);
		final HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
		System.out.println("URL connection:"+connection);

	}

	public static String getReturnBase64Code(String docId,String transid) throws IOException {
		//	final URL url = new URL("https://esign.apcfss.in/tosign/fetchdocument");
		final URL url = new URL("https://esign.herb.apcfss.in/tosign/fetchdocument");
		final HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		final String input = "{\"docid\":\""+docId+"\",\"transid\":\""+transid+"\"}";
		System.out.println("input-----"+input);
		final OutputStream os = connection.getOutputStream();
		os.write(input.getBytes());
		os.flush();
		final InputStream content = connection.getInputStream();
		final BufferedReader in = new BufferedReader(new InputStreamReader(content));
		String line,result2="";
		while ((line = in.readLine()) != null) {
			result2=line;
		}
		System.out.println("result2-------"+result2);
		return result2;
	}

	public static String timeZone(Timestamp date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));
		String local = sdf.format(new Date(date.getTime()));
		return local;
	}

	public static String getTimeInMilliSeconds() {
		Date date = new Date();
		return date.getTime() + "";
	}

	public static String commaSeparationForAmount(String x){

		String minus = "";

		if(x.contains("-"))

		{

			minus = "-";

			x = x.replace("-","");

		}

		if(x.length() == 1)

		{

			if("0".equals(x))

				return "..";

			else

				return minus+x;

		}

		else if(x.length() == 4)

			return minus+x.substring(0,1)+","+x.substring(1,4);

		else if(x.length() == 5)

			return minus+x.substring(0,2)+","+x.substring(2,5);

		else if(x.length() == 6)

			return minus+x.substring(0,1)+","+x.substring(1,3)+","+x.substring(3,6);

		else if(x.length() == 7)

			return minus+x.substring(0,2)+","+x.substring(2,4)+","+x.substring(4,7);

		else if(x.length() == 8)

			return minus+x.substring(0,1)+","+x.substring(1,3)+","+x.substring(3,5)+","+x.substring(5,8);

		else if(x.length() == 9)

			return minus+x.substring(0,2)+","+x.substring(2,4)+","+x.substring(4,6)+","+x.substring(6,9);

		else if(x.length() > 9)

			return minus+x.substring(0,x.length()-7)+","+x.substring(x.length()-7,x.length()-5)+","+x.substring(x.length()-5,x.length()-3)+","+x.substring(x.length()-3,x.length());

		else return minus+x;

	} 

	public synchronized static boolean fileUpload(final MultipartFile fileObj, final String dirPath, final String fileName, String reqFileName, final HttpServletRequest request) {
		try {

			final InputStream is = fileObj.getInputStream();
			String filePath = null;
			final String filename = null;
			if (isNull(fileName)) {
				filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + fileObj.getName();
				System.out.println(filePath);
			}
			else {
				if ("application/msword".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else if ("application/vnd.ms-excel".equalsIgnoreCase(fileObj.getContentType()) || "application/msexcel".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else if ("image/jpeg".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else if ("image/png".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else if ("application/pdf".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else if ("text/xml".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else if ("text/plain".equalsIgnoreCase(fileObj.getContentType())) {
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				else {
					if (!"application/octet-stream".equalsIgnoreCase(fileObj.getContentType())) {
						return false;
					}
					reqFileName = reqFileName;
					filePath = String.valueOf(String.valueOf(String.valueOf(dirPath))) + reqFileName;
				}
				if (!isNull(reqFileName)) {
					request.setAttribute(reqFileName, (Object)filename);
				}
			}

			final OutputStream bos = new FileOutputStream(filePath);
			int bytesRead = 0;
			final byte[] buffer = new byte[18192];
			while ((bytesRead = is.read(buffer, 0, 18192)) != -1) {
				bos.write(buffer, 0, bytesRead);
			}
			bos.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
