package com.littlepay.triptracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TripTrackerApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(TripTrackerApplication.class, args);
        TapToTripOutputProcessor service = applicationContext.getBean(TapToTripOutputProcessor.class);
        service.processTapsToTrips();
    }

}
