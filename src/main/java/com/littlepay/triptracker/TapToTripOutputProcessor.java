package com.littlepay.triptracker;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class TapToTripOutputProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TapToTripOutputProcessor.class);

    private static final String[] HEADERS_TAPS = {"ID", "DateTimeUTC", "TapType", "StopId", "CompanyId", "BusID", "PAN"};

    private static final String[] HEADERS_TRIPS = {"Started", "Finished", "DurationSecs", "FromStopId", "ToStopId", "ChargeAmount", "CompanyId", "BusID", "PAN", "Status"};

    private static final String COMPLETED_TRIP = "COMPLETED";
    private static final String INCOMPLETE_TRIP = "INCOMPLETE";
    private static final String CANCELLED_TRIP = "CANCELLED";

    private static final String NOT_APPLICABLE = "N/A";

    private static final String TAP_ON = "ON";

    private static final String TAP_OFF = "OFF";

    private static final String FILE_PATH = "src/main/resources/";

    public void processTapsToTrips() {
        try {
            LOGGER.debug("reading csv file..");
            List<BusTap> busTaps = readCsvFile();
            List<BusTrip> busTrips = getBusTripsFromTaps(busTaps);
            writeToCSV(busTrips);
        } catch (IOException e) {
            LOGGER.error("Error processing taps {}", e.getMessage());
        }
    }

    private List<BusTap> readCsvFile() throws IOException {
        File file = new File(FILE_PATH + "taps.csv");
        List<BusTap> busTaps = new ArrayList<>();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader(HEADERS_TAPS).setDelimiter(", ").setSkipHeaderRecord(true).build();
        try (BufferedReader br = new BufferedReader(new FileReader(file)); CSVParser parser = csvFormat.parse(br)) {
            for (CSVRecord busTapRecord : parser) {
                BusTap busTap = new BusTap();
                busTap.setId(busTapRecord.get(HEADERS_TAPS[0]));
                busTap.setDateTimeUTC(busTapRecord.get(HEADERS_TAPS[1]));
                busTap.setTapType(busTapRecord.get(HEADERS_TAPS[2]));
                busTap.setStopId(busTapRecord.get(HEADERS_TAPS[3]));
                busTap.setCompanyId(busTapRecord.get(HEADERS_TAPS[4]));
                busTap.setBusId(busTapRecord.get(HEADERS_TAPS[5]));
                busTap.setPanNo(busTapRecord.get(HEADERS_TAPS[6]));
                busTaps.add(busTap);
            }
        } catch (Exception e) {
            LOGGER.error("Error during readCsvFile: {}", e.getMessage());
        }
        busTaps.sort(Comparator.comparing(BusTap::getDateTimeUTC).thenComparing(BusTap::getPanNo).thenComparing(BusTap::getBusId));
        return busTaps;
    }


    private List<BusTrip> getBusTripsFromTaps(List<BusTap> busTaps) {
        List<BusTrip> busTripList = new ArrayList<>();

        for (int i = 0; i < busTaps.size() - 1; i++) {
            BusTap tap = busTaps.get(i);
            BusTap nextTap = busTaps.get(i + 1);

            if (tap.getTapType().equals(TAP_ON) && nextTap.getTapType().equals(TAP_ON)) {
                processIncompleteTrip(tap, busTripList);

            } else if (tap.getTapType().equals(TAP_ON) && nextTap.getTapType().equals(TAP_OFF)) {
                processCompletedOrCancelledTrip(tap, nextTap, busTripList);
                i++; // Skip the next tap since it's an "OFF" tap
            }
        }
        return busTripList;
    }

    private void processIncompleteTrip(BusTap tap, List<BusTrip> outputList) {
        String fees = "$" + getMaxCharges(tap.getStopId());
        BusTrip tripOutput = new BusTrip(tap.getDateTimeUTC(), NOT_APPLICABLE, NOT_APPLICABLE, tap.getStopId(), NOT_APPLICABLE, fees, tap.getCompanyId(), tap.getBusId(), tap.getPanNo(), INCOMPLETE_TRIP);
        outputList.add(tripOutput);
    }

    private void processCompletedOrCancelledTrip(BusTap tap, BusTap nextTap, List<BusTrip> outputList) {
        if (tap.getBusId().equals(nextTap.getBusId()) && tap.getPanNo().equals(nextTap.getPanNo())) {
            String fees = "$0.00";
            String tripType;
            String timeInSecs = calculateTimeInSec(tap.getDateTimeUTC(), nextTap.getDateTimeUTC());

            if (tap.getStopId().equalsIgnoreCase(nextTap.getStopId())) {
                tripType = CANCELLED_TRIP;
            } else {
                fees = "$" + getFees(tap.getStopId(), nextTap.getStopId());
                tripType = COMPLETED_TRIP;
            }

            BusTrip tripOutput = new BusTrip(tap.getDateTimeUTC(), nextTap.getDateTimeUTC(), timeInSecs, tap.getStopId(), nextTap.getStopId(), fees, tap.getCompanyId(), tap.getBusId(), tap.getPanNo(), tripType);
            outputList.add(tripOutput);
        }
    }

    private double getFees(String stop1, String stop2) {
        for (BusCharges charges : BusCharges.values()) {
            if (isMatchingStops(stop1, stop2, charges)) {
                return Double.parseDouble(charges.getFee());
            }
        }
        return 0.0; // Default fee if no matching charges are found
    }

    private boolean isMatchingStops(String stop1, String stop2, BusCharges charges) {
        return (stop1.equalsIgnoreCase(charges.getStop1()) && stop2.equalsIgnoreCase(charges.getStop2())) || (stop1.equalsIgnoreCase(charges.getStop2()) && stop2.equalsIgnoreCase(charges.getStop1()));
    }

    private double getMaxCharges(String stopId) {
        double fees;
        List<Double> chargesList = new ArrayList<>();
        for (BusCharges charges : BusCharges.values()) {
            if (stopId.equalsIgnoreCase(charges.getStop1()) || stopId.equalsIgnoreCase(charges.getStop2()))
                chargesList.add(Double.parseDouble(charges.getFee()));
        }
        fees = Collections.max(chargesList);
        return fees;
    }

    private String calculateTimeInSec(String dateTime1, String dateTime2) {
        String timeInSec = "0";
        Date tapOnTime = formatDateTime(dateTime1);
        Date tapOffTime = formatDateTime(dateTime2);
        if (tapOffTime != null && tapOnTime != null) {
            timeInSec = String.valueOf((tapOffTime.getTime() - tapOnTime.getTime()) / 1000);
        }
        return timeInSec;
    }

    private Date formatDateTime(String date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date time = null;
        try {
            time = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    private void writeToCSV(List<BusTrip> busTrips) {
        File file = new File(FILE_PATH + "trips.csv");
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader(HEADERS_TRIPS).setDelimiter(", ").build();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)); CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (BusTrip busTrip : busTrips) {
                csvPrinter.printRecord(busTrip.getStarted(), busTrip.getFinished(), busTrip.getDurationSecs(), busTrip.getFromStopId(), busTrip.getToStopId(), busTrip.getChargeAmount(), busTrip.getCompanyId(), busTrip.getBusId(), busTrip.getPanNo(), busTrip.getStatus());
            }
            csvPrinter.flush();
        } catch (IOException e) {
            LOGGER.error("Error during writeToCSV: {}", e.getMessage());
        }
    }

}
