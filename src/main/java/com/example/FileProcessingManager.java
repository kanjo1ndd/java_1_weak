package com.example;
import java.io.File;
import java.util.concurrent.*;

public class FileProcessingManager {

    private final ExecutorService executor;
    private final JsonFileParser parser = new JsonFileParser();
    private final StatisticsAggregator aggregator;

    public FileProcessingManager(int threads, StatisticsAggregator aggregator) {
        this.executor = Executors.newFixedThreadPool(threads);
        this.aggregator = aggregator;
    }

    public void processDirectory(File dir, String attribute) throws InterruptedException {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) return;
        CountDownLatch latch = new CountDownLatch(files.length);
        for (File f : files) {
            executor.submit(() -> {
                try {
                    parser.parseFile(f, book -> aggregator.accept(book, attribute));
                } catch (Exception ex) {
                    System.err.println("Error parsing " + f + ": " + ex.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    public void shutdown() {
        executor.shutdown();
    }
}