package com.another.customapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;

import com.another.customapplication.aidltest.IRemoteService;
import com.another.customapplication.aidltest.IRemoteServiceCallback;

/**
 * Created by another on 18-1-14.
 */

public class TestService extends Service {


    private static int mCount = 0;

    @Override
    public IBinder onBind(Intent intent) {

        if ("hehe".equals(intent.getAction()))
            return mRemoteServiceBinder;

        return null;
    }

    private final IRemoteService.Stub mRemoteServiceBinder = new IRemoteService.Stub() {

        @Override
        public int getPid(IRemoteServiceCallback callback) {

            try {
                callback.valueCounted(mCount++);

            } catch (RemoteException e) {

            }

            return Process.myPid();
        }
    };
}
