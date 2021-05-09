package com.example.dhruv.pg_accomodation.call_support;

import com.example.dhruv.pg_accomodation.R;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;



public class PlaceCallActivity extends BaseActivity {

    private String recepientID;
    private String recepientName;
    private String callerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        recepientID = getIntent().getStringExtra("recepientID");
        recepientName = getIntent().getStringExtra("recepientName");
        callerName = getIntent().getStringExtra("callerName");
    }

    @Override
    protected void onServiceConnected() {
        launchCallScreen();
    }

    private void launchCallScreen() {
        String userName = recepientID;
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Call call = getSinchServiceInterface().callUser(userName);
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
                        + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            callScreen.putExtra("recepientName", recepientName);
            callScreen.putExtra("callerName", callerName);
            startActivity(callScreen);
            finish();
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }
}
