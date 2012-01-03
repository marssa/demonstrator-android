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
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FrontEndAndroidActivity extends Activity {
	/** Called when the activity is first created. */

	// url for android device
	String url = "http://192.168.2.106:8182/";

	String result = "";
	Handler h = new Handler();
	TextView date;
	TextView responsetxt;
	String navi = "nav";
	String underw = "Under";
	TextView txtv;
	TextView txtu;
	TextView txtr;

	ToggleButton tbunl;
	ToggleButton tbnav1;

	// Timer timer = new;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		txtu = (TextView) findViewById(R.id.txtspeed);
		// txtu.setText("Speed:");

		txtr = (TextView) findViewById(R.id.txtrudder);

		TabHost tabhost = (TabHost) findViewById(R.id.tabHost);
		tabhost.setup();

		TabSpec spec1 = tabhost.newTabSpec("Drive");
		spec1.setContent(R.id.tab1); 
		spec1.setIndicator("Drive", getResources().getDrawable(R.drawable.drive));

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
				if (((ToggleButton) v).isChecked()) {
					try {
						changenavtrue();
					} catch (Exception e) {

						Toast.makeText(getBaseContext(), "An error happened",
								Toast.LENGTH_SHORT);
					}
				} else {
					try {
						changenavfalse();

					} catch (Exception e) {

						Toast.makeText(getBaseContext(), "An error happened",
								Toast.LENGTH_SHORT);
					}
				}
			}
		});

		tbunl = (ToggleButton) findViewById(R.id.toggleUnderwaterLights);

		tbunl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (((ToggleButton) v).isChecked()) {
					try {

						changeunderwatertrue();
						txtv.setText("true");

					} catch (Exception e) {

						try {
							Displaymsg(e.getMessage());
						} catch (JSONException e1) {

							e1.printStackTrace();
						}
					}

				} else
					try {
						changeunderwaterfalse();
						txtv.setText("false");
					} catch (Exception e) {

						e.printStackTrace();
					}
			}
		});

		// Drive straight full speed
		Button btndup = (Button) findViewById(R.id.btndoubleup);
		btndup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doubleup();
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

		// Full Reverse speed
		Button btnddown = (Button) findViewById(R.id.btndoubledown);
		btnddown.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doubledown();
			}
		});

		// Turn left
		Button btnleft = (Button) findViewById(R.id.btnleft);
		btnleft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				left();
			}
		});

		// Turn full left
		Button btndleft = (Button) findViewById(R.id.btndoubleleft);
		btndleft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doubleleft();
			}
		});

		// Turn right
		Button btnright = (Button) findViewById(R.id.btnright);
		btnright.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				right();
			}
		});

		// Turn full right
		Button btndright = (Button) findViewById(R.id.btndright);
		btndright.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doubleright();
			}
		});

		// Turn full right
		Button btnstop = (Button) findViewById(R.id.btnstop);
		btnstop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stop();
			}
		});

		h.post(new Runnable() {
			public void run() {
				CheckStatus();
				h.postDelayed(this, 1000);
			}
		});

	}

	private class Readspeed extends AsyncTask<String, Void, String> {
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

		protected void onPostExecute(String result) {
			try {

				checkspeed(result);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void checkspeed(String msg) throws JSONException {
		try {
			if (msg == "") {
				Toast.makeText(getBaseContext(), "Empty", Toast.LENGTH_SHORT);
			} else if (msg == null) {
				Toast.makeText(getBaseContext(), "null", Toast.LENGTH_SHORT);
			} else {

				JSONObject jObject = new JSONObject(msg);
				JSONObject speed = jObject.getJSONObject("motor");
				String sp = speed.getString("value");

				txtu.setText("SPEED: " + sp);

				JSONObject jObject2 = new JSONObject(msg);
				JSONObject rudder = jObject2.getJSONObject("rudder");
				String rud = rudder.getString("value");

				txtr.setText("RUDDER: " + rud);

			}
		} catch (Exception ex) {
			Toast.makeText(getBaseContext(), "error", Toast.LENGTH_SHORT);
		}
	}

	private void Displaymsg(String msg) throws JSONException {
		try {

			if (msg == "") {
				Toast.makeText(getBaseContext(), "empty", Toast.LENGTH_SHORT)
						.show();
			} else if (msg == null) {
				Toast.makeText(getBaseContext(), "Null", Toast.LENGTH_SHORT)
						.show();
			} else {

				JSONObject jObject = new JSONObject(msg);
				JSONObject navlights = jObject.getJSONObject("navLights");
				String nav = navlights.getString("value");

				if (nav == "false") {
					tbnav1.setChecked(false);
				}

				else if (nav == "true") {
					tbnav1.setChecked(true);
				}

				JSONObject jObject2 = new JSONObject(msg);
				JSONObject underwater = jObject2
						.getJSONObject("underwaterLights");
				String under = underwater.getString("value");

				if (under == "false") {
					tbunl.setChecked(false);

				}

				else if (under == "true") {
					tbunl.setChecked(true);

				}
			}
		} catch (Exception ex) {
			Toast.makeText(getBaseContext(), ex.getMessage(),
					Toast.LENGTH_SHORT);
		}

	}

	private class Readmsg extends AsyncTask<String, Void, String> {
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

		protected void onPostExecute(String result) {
			try {
				Displaymsg(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void CheckStatus() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "lightControlPage/statusAll" });

		Readspeed s = new Readspeed();
		s.execute(new String[] { url + "motionControlPage/rudderAndSpeed" });

	}

	public void changenavtrue() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "lighting/navigationLights/true" });
	}

	public void changenavfalse() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "lighting/navigationLights/false" });
	}

	public void changeunderwatertrue() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "lighting/underwaterLights/true" });
	}

	public void changeunderwaterfalse() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "lighting/underwaterLights/false" });
	}

	public void doubleup() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "motor/speed/100" });
	}

	public void up() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "motor/increaseSpeed" });
	}

	public void down() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "motor/decreaseSpeed" });
	}

	public void doubledown() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "motor/speed/-100" });
	}

	public void stop() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "motor/speed/0" });

	}

	public void right() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "rudder/rotateMore/true" });
	}

	public void doubleright() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "rudder/rotateFull/true" });
	}

	public void left() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "rudder/rotateMore/false" });
	}

	public void doubleleft() {
		Readmsg task = new Readmsg();
		task.execute(new String[] { url + "rudder/rotateFull/false" });
	}

}