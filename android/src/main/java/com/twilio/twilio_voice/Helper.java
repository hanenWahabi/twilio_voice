package com.twilio.twilio_voice;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twilio.voice.CallInvite;

public class Helper {

    /**
     * Attempts to resolve {@link Constants#PARAMETER_CALLER_NAME} from call invite, alternatively
     * uses shared preferences and {@link Constants#PARAMETER_CALLER_ID} to determined caller name.
     * <p>
     * If none of the above parameters are found/exists, use ${@link CallInvite#getFrom()} and removes decoration to get client ID.
     * If the above fails, we use {@link Constants#CLIENT_DEFAULT_CALLER} to resolve caller name if stored/available.
     * <p>
     * If all else fails, used `fallback` as name.
     *
     * @param callInvite Call Invite object
     * @param fallback   Fallback value if no name is found
     * @return caller name, else null
     */
    static String getUsableName(@NonNull CallInvite callInvite, @NonNull SharedPreferences preferences, @NonNull String fallback) {
        return resolveCallerName(callInvite, resolveCallerId(preferences, callInvite, fallback));
    }

    /**
     * Accepts call invite, attempts to get caller name from parameters if it exists
     *
     * @param callInvite Call Invite object
     * @param fallback   Fallback value if no name is found
     * @return caller name, else null
     */
    static String resolveCallerName(@NonNull CallInvite callInvite, @NonNull String fallback) {
        return callInvite.getCustomParameters().get(Constants.PARAMETER_CALLER_NAME);
    }

    /**
     * Accepts call invite, attempts to get caller ID from parameters and resolve against preferences
     *
     * @param preferences Application preferences to resolve against
     * @param callInvite  Call Invite object
     * @param fallback    Fallback name if caller ID cannot be resolved/found
     * @return caller name, else fallback is used
     */
    static String resolveCallerId(@NonNull SharedPreferences preferences, @NonNull CallInvite callInvite, @NonNull String fallback) {
        if (callInvite.getCustomParameters().containsKey(Constants.PARAMETER_CALLER_ID)) {
            String callerId = callInvite.getCustomParameters().get(Constants.PARAMETER_CALLER_ID);
            if (callerId != null) {
                return preferences.getString(callerId, fallback);
            }
            String clientOrigin = callInvite.getFrom();
            if(clientOrigin != null) {
                clientOrigin = clientOrigin.replace("client:", "");
                return preferences.getString(clientOrigin, fallback);
            }
        }
        return fallback;
    }
}
