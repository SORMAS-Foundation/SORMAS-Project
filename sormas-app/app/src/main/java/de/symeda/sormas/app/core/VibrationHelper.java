package de.symeda.sormas.app.core;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

public class VibrationHelper {


    private static VibrationHelper sSoleInstance;
    private static Context mContext;
    private static long[] inputFieldErrorPattern = {0, 300, 100, 500};;

    private VibrationHelper(Context context) {
        this.mContext = context;
    }

    public static VibrationHelper getInstance(Context context){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new VibrationHelper(context);
        }

        return sSoleInstance;
    }

    public static void onInputFieldError() {
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        if (v.hasVibrator()) {
            v.vibrate(inputFieldErrorPattern, -1);
        } else {
            Log.v("Can Vibrate", "NO");
        }
    }
}
