package edu.montana.csci.csci366.archivecat.archiver.runners;

import edu.montana.csci.csci366.archivecat.archiver.jobs.DownloadJob;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolJobRunner implements DownloadJobRunner {
    public void executeJobs(List<? extends DownloadJob> downloadJobs) {
        // DONE implement - use a ThreadPoolExecutor with 10 threads to execute the jobs
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(downloadJobs.size());
        for (DownloadJob job : downloadJobs)
            service.submit(() -> {
                try {
                    job.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        try {
            latch.await();
            System.out.println("DONE");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
