package nadav.tasher.openpush.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nadav.tasher.openpush.workers.PullWorker;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            PullWorker.enqueueWork(context);
        }
    }
}
