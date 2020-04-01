package nadav.tasher.openpush.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import nadav.tasher.openpush.services.PullService;

public class PullReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PullService.class);
        context.startService(i);
    }
}
