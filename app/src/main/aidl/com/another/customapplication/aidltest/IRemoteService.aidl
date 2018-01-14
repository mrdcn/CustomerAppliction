// IRemoteService.aidl
package com.another.customapplication.aidltest;

import com.another.customapplication.aidltest.IRemoteServiceCallback;
// Declare any non-default types here with import statements
interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     int getPid(IRemoteServiceCallback callback);
}
