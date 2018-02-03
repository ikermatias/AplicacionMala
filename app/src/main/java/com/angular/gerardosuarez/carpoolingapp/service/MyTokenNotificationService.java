package com.angular.gerardosuarez.carpoolingapp.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by sebastian on 16/01/18.
 */

public class MyTokenNotificationService extends FirebaseInstanceIdService {


    private static final String TAG = "MyFirebaseIIDService";


    private String tokenNotificacion;




    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        tokenNotificacion = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + tokenNotificacion);

        System.out.println("Este es mi token: " + tokenNotificacion);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(tokenNotificacion);

    }

    public void setTokenNotificacion(String token){

        tokenNotificacion=token;
    }
    public String getTokenNotificacion(){

        onTokenRefresh();

        return tokenNotificacion;
    }


    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


}
