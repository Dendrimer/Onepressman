package amcd.opm;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.app.Fragment;

import java.util.ArrayList;

import amcd.opm.listeners.PebbleListener;

public class MainActivity extends AppCompatActivity {
    final String[] screen = {"Welcome","Event Creation","Contact Selection",""};
    String currentScreen;
    String eventName;//name of the emergency in this case
    String description;//this is the text message
    ArrayList<String> contactNumbers = new ArrayList<>();//the numbers to be messaged
    EventProfile eventProfile;
    EventProfile selectedProfile;
    boolean useGPS = false;//whether or not to include GPS data
    ArrayList<EventProfile> profiles = new ArrayList<>();
    PebbleListener listener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getFragmentManager();
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        try {
            currentScreen = savedInstanceState.getString("lastScreen");
        }
        catch(Exception e) {

                currentScreen = screen[0];

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                if(currentScreen.equals(screen[0])) {
                    FragmentTransaction fragmentTranscation = fragmentManager.beginTransaction();
                    createEvent fragment = new createEvent();
                    button.setText("SELECT CONTACTS");
                    currentScreen = screen[1];
                    fragmentTranscation.replace(R.id.main_fragment, fragment);
                    // fragmentTranscation.add(R.id.create_event,fragment);
                    //fragmentTranscation.remove(fragmentManager.findFragmentById(R.id.main_fragment));

                    fragmentTranscation.commit();

                }
                else if(currentScreen.equals(screen[1])){
                    FragmentTransaction fragmentTranscation = fragmentManager.beginTransaction();
                    selectContacts frag = new selectContacts();
                    button.setText("FINISH");
                    currentScreen = screen[2];
                    fragmentTranscation.replace(R.id.main_fragment, frag);
                    fragmentTranscation.commit();


                }
                else if(currentScreen.equals(screen[2])){
                    createProfile();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    listEvents fr = new listEvents();
                    button.setText("ADD AN EVENT");
                    currentScreen = screen[3];
                    fragmentTransaction.replace(R.id.main_fragment, fr);
                    fragmentTransaction.commit();
                }
                else if(currentScreen.equals(screen[3])){
                    FragmentTransaction fragmentTranscation = fragmentManager.beginTransaction();
                    createEvent fragment = new createEvent();
                    button.setText("SELECT CONTACTS");
                    currentScreen = screen[1];
                    fragmentTranscation.replace(R.id.main_fragment, fragment);
                    // fragmentTranscation.add(R.id.create_event,fragment);
                    //fragmentTranscation.remove(fragmentManager.findFragmentById(R.id.main_fragment));

                    fragmentTranscation.commit();
                }

            }
        });

        listener = new PebbleListener(getApplicationContext(), manager); // Link listener to main activity

    }

    public void setEventName(String name){
        this.eventName = name;

    }

    public void setDescription(String desc){
        this.description = desc;
    }

    public void setUseGPS(boolean use){
        this.useGPS = use;
    }

    public void appendContactNumber(String contactNumber){

        contactNumbers.add(contactNumber);
    }

    public void createProfile(){
        eventProfile = new EventProfile(eventName, contactNumbers, description, useGPS);
        profiles.add(eventProfile);

        return;
    }

    public ArrayList<EventProfile> getProfiles(){
        return profiles;
    }

    public void setSelectedProfile(EventProfile sel){
        this.selectedProfile = sel;
        listener.setProfile(sel);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putString("lastScreen", currentScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
