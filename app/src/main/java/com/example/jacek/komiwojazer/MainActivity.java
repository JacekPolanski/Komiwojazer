package com.example.jacek.komiwojazer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ITSPService mITSPService = null;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TSPService.class);
        intent.setAction(ITSPService.class.getName());
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }

    public void onButtonClick(View v) throws RemoteException {
        if (mBound) {
            int num = mITSPService.getResult(2);
            Toast.makeText(this, "number: " + num, Toast.LENGTH_SHORT).show();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mITSPService = ITSPService.Stub.asInterface(iBinder);
            mBound = true;
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e("a", "Service has unexpectedly disconnected");
            mITSPService = null;
            mBound = false;
        }

        @Override
        public void onBindingDied(ComponentName name) {

        }
    };
}
