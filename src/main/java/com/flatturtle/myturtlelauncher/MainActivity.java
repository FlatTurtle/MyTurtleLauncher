/**
 * FlatTurtle BVBA
 * @author Michiel Vancoillie
 *
 * Launcher for the other application (MyTurtleController)
 * It's required because if there are updates on the other application, it would reset the default (home) launcher settings.
 *
 * Should never need updates.
 */
package com.flatturtle.myturtlelauncher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private PendingIntent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Add content
        setContentView(R.layout.activity_main);

        // Restart application on crash
        intent = PendingIntent.getActivity(this.getApplication()
                .getBaseContext(), 0, new Intent(getIntent()), getIntent().getFlags());
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable ex) {
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,
                        intent);
                System.exit(2);
            }
        });

        // Re-launch button
        RelativeLayout btnRelaunch = (RelativeLayout) findViewById(R.id.btnRelaunch);
        btnRelaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeOff();
            }
        });

        // All systems GO
        takeOff();
    }


    /**
     * Launch the MyTurtleController app :)
     */
    private void takeOff(){
        try{
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.flatturtle.myturtlecontroller");
            startActivity(LaunchIntent);
        }catch (NullPointerException e){
            // Whoops MyTurtleController not installed or not found
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.alert_title);
            alert.setMessage(R.string.alert_message);
            alert.setPositiveButton("Ok", null);
            alert.show();
        }
    }

    /**
     * Prevent leaving the application without password
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_POWER:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_BACK:
                // Create an alert to prompt password
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("A password is needed to quit the application");
                alert.setMessage("Password:");

                final EditText input = new EditText(this);
                alert.setView(input);

                // Check password
                final DialogInterface.OnClickListener submitListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        if (value.toString().equals(getString(R.string.admin_safety_password))) {
                            shutDown();
                        }
                    }
                };
                alert.setPositiveButton("Ok", submitListener);

                // Cancel exit
                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // Canceled.
                            }
                        });
                final AlertDialog alertDialog = alert.create();

                // ENTER key to submit
                input.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN)
                                && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            submitListener.onClick(null, 0);
                            alertDialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

                // Show the alert
                alertDialog.show();
                return true;
            default:
                return false;
        }
    }

    /**
     * User confirmed shutdown with password, also change settings or really
     * quit?
     */
    public void shutDown() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Quit, or change settings?");

        // Really quit
        alert.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                System.exit(0);
            }
        });

        // Show settings page
        alert.setNegativeButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton){
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                });

        // Show the alert
        alert.show();
    }

}
