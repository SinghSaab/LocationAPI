package self.locationapi;

import android.app.IntentService;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-04-10.
 */
public class ActivityIntentService extends IntentService {

    private static final String TAG = "ActivitiesIntentService";

    public ActivityIntentService() {
//        calls the super IntentService(String) constructor with the name of a worker thread.
        super(TAG);
    }


//    get the ActivityRecognitionResult from the Intent by using extractResult()
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent i = new Intent(Constants.STRING_ACTION);

//        Get the activityRecognition results and save in ArrayList with confidence level(0-100)
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
        i.putExtra(Constants.STRING_EXTRA, detectedActivities);

//        Broadcast the intent
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

//        Add "service" tag in manifest file as well
    }
}
