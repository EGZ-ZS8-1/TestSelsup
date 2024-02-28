package org.example;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final AtomicInteger requestCount;
    private final Object lock = new Object();
    private long lastRequestTimeMillis;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.requestCount = new AtomicInteger(0);
        this.lastRequestTimeMillis = System.currentTimeMillis();
    }

    public void createDocument(String participantInn, String docId, String docStatus, String docType,
                               boolean importRequest, String ownerInn, String producerInn,
                               LocalDate productionDate, String productionType, List<Product> products,
                               LocalDate regDate, String regNumber, String signature) {
        synchronized (lock) {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMillis = currentTimeMillis - lastRequestTimeMillis;
            long elapsedTime = timeUnit.convert(elapsedTimeMillis, TimeUnit.MILLISECONDS);

            if (elapsedTime >= 1) {
                requestCount.set(0);
                lastRequestTimeMillis = currentTimeMillis;
            }

            if (requestCount.get() >= requestLimit) {
                try {
                    long sleepTimeMillis = timeUnit.toMillis(1) - elapsedTimeMillis;
                    Thread.sleep(sleepTimeMillis);
                    requestCount.set(0);
                    lastRequestTimeMillis = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            System.out.println("createDocument method called with parameters: "
                    + participantInn + ", " + docId + ", " + docStatus + ", " + docType + ", "
                    + importRequest + ", " + ownerInn + ", " + producerInn + ", " + productionDate + ", "
                    + productionType + ", " + products + ", " + regDate + ", " + regNumber + ", " + signature);

            requestCount.incrementAndGet();
        }
    }
    @Getter
    @Setter
    public static class Product {
        private String certificateDocument;
        private LocalDate certificateDocumentDate;
        private String certificateDocumentNumber;
        private String ownerInn;
        private String producerInn;
        private LocalDate productionDate;
        private String tnvedCode;
        private String uitCode;
        private String uituCode;


    }
}