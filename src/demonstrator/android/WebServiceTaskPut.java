package demonstrator.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class WebServiceTaskPut extends AsyncTask<String, Void, String> {

	String result;
	@Override
	protected String doInBackground(String... urls) {
		String response = "";
		
		DefaultHttpClient client;
		for (String url : urls) {
			 client = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(url);
			try {
				HttpResponse execute = client.execute(httpPut);
				InputStream content = execute.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					response += s;
				}
				

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}		
	
		return response;
	}
	
	@Override
	protected void onPostExecute(String result) {
		this.result = result;
	}


	public String Displaymsg() {
		return result;
		
	}
	
	

}