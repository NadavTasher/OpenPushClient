package nadav.tasher.openpush;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;

import nadav.tasher.openpush.jobs.SyncJob;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new JobCreator() {
            @Nullable
            @Override
            public Job create(@NonNull String s) {
                if (s.equals(SyncJob.TAG))
                    return new SyncJob();
                return null;
            }
        });
    }

}
