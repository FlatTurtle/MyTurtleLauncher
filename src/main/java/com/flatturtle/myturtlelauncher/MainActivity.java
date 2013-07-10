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
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Add content
        setContentView(R.layout.activity_main);

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

}
