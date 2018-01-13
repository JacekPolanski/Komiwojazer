package com.example.jacek.komiwojazer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

public class TSPService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final ITSPService.Stub mBinder = new ITSPService.Stub() {
        @Override
        public int getResult(int a) throws RemoteException {
            return a;
        }
    };
}
