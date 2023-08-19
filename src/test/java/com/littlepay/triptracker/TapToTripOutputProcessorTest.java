package com.littlepay.triptracker;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class TapToTripOutputProcessorTest {

    @Test
    void processTapsToTrips() {
        File f = new File("src/main/resources/trips.csv");
        assertThat(f).exists();
    }
}