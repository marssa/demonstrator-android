package demonstrator.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FrontEndAndroidActivity extends Activity {
	/** Called when the activity is first created. */

	
	//url for server
	String url = "http://192.168.2.1:8082/demonstrator-backend-web/rest/";
	
	
	// url for trimslice
	//String url = "http://192.168.2.104:8080/demonstrator-backend-web/rest/";	
	
	
	String result = "";
	Handler h = new Handler();
	boolean navUnder;
	ImageView speed;
	ToggleButton tbunl;
	ToggleButton tbnav1;
	ImageView lights;

	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		TabHost tabhost = (TabHost) findViewById(R.id.tabHost);
		tabhost.setup();
		speed = (ImageView)findViewById(R.id.imageView_speed);
		TabSpec spec1 = tabhost.newTabSpec("Drive");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Drive", getResources()
				.getDrawable(R.drawable.drive));

		TabSpec spec2 = tabhost.newTabSpec("Light Control");
		spec2.setContent(R.id.tab2);
		spec2.setIndicator("Light Control",
				getResources().getDrawable(R.drawable.lightbulb));

		TabSpec spec3 = tabhost.newTabSpec("Navigation");
		spec3.setContent(R.id.tab3);
		spec3.setIndicator("Navigation",
				getResources().getDrawable(R.drawable.compass));

		tabhost.addTab(spec1);
		tabhost.addTab(spec2);
		tabhost.addTab(spec3);

		tabhost.getTabWidget().getChildAt(0).getLayoutParams().width = 350;
		tabhost.getTabWidget().getChildAt(1).getLayoutParams().width = 550;
		tabhost.getTabWidget().getChildAt(2).getLayoutParams().width = 350;

		tbnav1 = (ToggleButton) findViewById(R.id.toggleNavLights);
		tbnav1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if (((ToggleButton) v).isChecked()) {
						changenavtrue();
						tbnav1.setChecked(true);
					} else {
						changenavfalse();
						tbnav1.setChecked(false);
					}
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), "An error happened",
							Toast.LENGTH_SHORT);
				}
			}
		});

		tbunl = (ToggleButton) findViewById(R.id.toggleUnderwaterLights);

		tbunl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (((ToggleButton) v).isChecked()) {
					changeunderwatertrue();
					tbunl.setChecked(true);
				} else {
					changeunderwaterfalse();
					tbunl.setChecked(false);
				}
			}
		});

		// Drive straight increase speed
		Button btnup = (Button) findViewById(R.id.btnup);
		btnup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				up();
			}
		});

		// Decrease speed
		Button btndown = (Button) findViewById(R.id.btndown);
		btndown.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				down();
			}
		});

		// Stop
		Button btnstop = (Button) findViewById(R.id.btnstop);
		btnstop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stop();
			}
		});

		// rotate left speed
		Button btnLeft = (Button) findViewById(R.id.btnleft);
		btnLeft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				left();
			}
		});

		// rotate right speed
		Button btnRight = (Button) findViewById(R.id.btnright);
		btnRight.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				right();
			}
		});

		h.post(new Runnable() {
			public void run() {
				navigation();
				underwater();
				speed();
				checkstatusforpic();
				h.postDelayed(this, 1000);
			}
		});
		
		lights =(ImageView)findViewById(R.id.demimage);
	}

	public void checkstatusforpic()
	{
		if(!tbnav1.isChecked() && !tbunl.isChecked())
		{
			lights.setImageResource(R.drawable.demonstratornightalloff);	
		}
		
		else if(tbnav1.isChecked() && !tbunl.isChecked())
		{
			lights.setImageResource(R.drawable.demonstratornightnavon);
		}
		else if (!tbnav1.isChecked() && tbunl.isChecked())
		{
			lights.setImageResource(R.drawable.demonstratornightundwateron);
		}
		else if(tbnav1.isChecked() && tbunl.isChecked())
		{
			lights.setImageResource(R.drawable.demonstratornightallon);
		}
	}

	public class WebServiceTaskGetn extends AsyncTask<String, Void, String> {
		String result;
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
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
			JSONObject jObject;
			String navLights = null;
			try {
				jObject = new JSONObject(result);
				navLights = jObject.getString("value");
			} catch (JSONException e) {				
				e.printStackTrace();
			}

			if (navLights == "false") {
				tbnav1.setChecked(false);
			} else if (navLights == "true") {
				tbnav1.setChecked(true);
			}
		}
	}

	public class WebServiceTaskSpeed extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
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

			switch(Integer.valueOf(result)){
			case 5:
				speed.setImageResource(R.drawable.speed5);
				break;
			case 4:
				speed.setImageResource(R.drawable.speed4);
				break;
			case 3:
				speed.setImageResource(R.drawable.speed3);
				break;
			case 2:
				speed.setImageResource(R.drawable.speed2);
				break;
			case 1:
				speed.setImageResource(R.drawable.speed1);
				break;
			case 0:
				speed.setImageResource(R.drawable.speed0);
				break;
			case -1:
				speed.setImageResource(R.drawable.speed_1);
				break;
			case -2:
				speed.setImageResource(R.drawable.speed_2);
				break;
			case -3:
				speed.setImageResource(R.drawable.speed_3);
				break;
			case -4:
				speed.setImageResource(R.drawable.speed_4);
				break;
			case -5:
				speed.setImageResource(R.drawable.speed_5);
				break;
			}
		}
	}

	public class WebServiceTaskGetu extends AsyncTask<String, Void, String> {

		String result;

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
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
			JSONObject jObject;
			String navLights = null;
			try {
				jObject = new JSONObject(result);
				navLights = jObject.getString("value");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (navLights == "false") {
				tbunl.setChecked(false);
			} else if (navLights == "true") {
				tbunl.setChecked(true);
			}
		}
		
	}

	public void navigation() {
		WebServiceTaskGetn taskgetn = new WebServiceTaskGetn();
		taskgetn.execute(new String[] { url + "lights/navigation" });

	}

	public void underwater() {
		WebServiceTaskGetu taskgetu = new WebServiceTaskGetu();
		taskgetu.execute(new String[] { url + "lights/underwater" });

	}

	public void speed() {
		WebServiceTaskSpeed taskgetn = new WebServiceTaskSpeed();
		taskgetn.execute(new String[] { url + "motors/stern/speed" });

	}

	// lights
	public void changenavtrue() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "lights/navigation/true" });
	}

	public void changenavfalse() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "lights/navigation/false" });
	}

	public void changeunderwatertrue() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "lights/underwater/true" });
	}

	public void changeunderwaterfalse() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "lights/underwater/false" });
	}

	// speed

	public void up() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "motors/stern/speed/increase" });
	}

	//
	public void down() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "motors/stern/speed/decrease" });
	}

	public void stop() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "motors/stern/stop" });

	}

	public void right() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "rudder/rotateMore/true" });
	}

	public void left() {
		WebServiceTaskPut taskput = new WebServiceTaskPut();
		taskput.execute(new String[] { url + "rudder/rotateMore/false" });

	}

}