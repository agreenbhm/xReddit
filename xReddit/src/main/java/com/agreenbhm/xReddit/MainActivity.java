package com.agreenbhm.xReddit;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.*;
import java.net.URLConnection;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Thread.UncaughtExceptionHandler androidDefaultUEH;

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler(){
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("xReddit", "Uncaught exception is: ", ex);
            androidDefaultUEH.uncaughtException(thread, ex);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);

        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //TextView text = (TextView)findViewById(R.id.text);

        URL url = null;

        try {
            url = new URL("http://www.reddit.com/new.json");
        }
        catch (MalformedURLException e) {
            //System.out.println("URL error");
            android.util.Log.w("com.agreenbhm.xReddit", "url error");
            return;
        }

        HttpURLConnection con = null;
        String stringData = "";

        JSONObject jsonData = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonData1 = new JSONObject();

        try {
            con = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(con.getInputStream());
            stringData = readStream(in);
            System.out.println("Pulled data: " + stringData);
            System.out.println(stringData.equals(null));
        }
        catch (IOException e) {
            android.util.Log.w("com.agreenbhm.xReddit", "con error");
            //System.out.println("con error");
            return;
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }

        try {
            jsonData = new JSONObject(stringData);
            System.out.println("jsonData: " + jsonData);
            Log.v("xReddit", "jsonData: " + jsonData);
            jsonData1 = jsonData.getJSONObject("data");
            System.out.println("jsonData1: " + jsonData1);
            jsonArray = jsonData1.getJSONArray("children");
        }
        catch (JSONException e) {
            android.util.Log.e("xReddit", "JSON error: " + e);
        }

        TextView[] posts = new TextView[jsonArray.length()];
        FrameLayout fl = (FrameLayout)findViewById(R.id.container);
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll);

        View[] separator = new View[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject currentPostJSON = jsonArray.getJSONObject(i);
                JSONObject currentPost = currentPostJSON.getJSONObject("data");

                System.out.println("pre-json");
                System.out.println(currentPost);
                //String title = "";
                String title = currentPost.getString("title");
                System.out.println("pre-new");
                posts[i] = new TextView(this);
                posts[i].setText(title);
                posts[i].setHeight(80);
                posts[i].setEllipsize(TextUtils.TruncateAt.END);
                posts[i].setClickable(true);
                posts[i].setMaxLines(3);
                posts[i].setHorizontallyScrolling(false);
                posts[i].setSingleLine(false);
                posts[i].setPadding(3, 3, 3, 3);

                //posts[i] = (TextView) findViewById(R.id.text);
                posts[i].setText(title);
                System.out.println("pre-add");


                ll.addView(posts[i]);

                separator[i] = (View) view.findViewById(R.id.separator);


                //separator[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT));


                //ll.addView(separator[i]);

                //lv.addView(posts[i]);
                //fl.addView(posts[i]);
            }
            catch (JSONException e) {
                System.out.println("Error adding TextView");
                continue;
            }
        }


        //text.setText(data);
    }


    private String readStream(InputStream is) {
        BufferedReader reader = null;
        String data = "";
        try {
            reader = new BufferedReader(new InputStreamReader(is));

            String line = "";
            while ((line = reader.readLine()) != null) {
                data += line;
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


    }



}
