package edu.montana.csci.csci366.archivecat.archiver.runners;

import edu.montana.csci.csci366.archivecat.archiver.jobs.DownloadJob;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ThreadedJobRunner implements DownloadJobRunner {
    public void executeJobs(List<? extends DownloadJob> downloadJobs) {
        // DONE - run each job in its own thread.  Use a CountdownLatch
        //        to ensure that all threads complete before exiting this
        //        method
        CountDownLatch latch = new CountDownLatch(downloadJobs.size());
        for (DownloadJob job : downloadJobs) {
            new Thread(() -> {
                job.run();
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
            System.out.println("DONE");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
