The problem
Given an input file in CSV format containing list of taps (ON and OFF) in separate lines, you will need to create
an output file containing trips made by customers.
taps.csv [input file]
You are welcome to assume that the input file is well formed and is not missing data.
Example input file:
ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN
1, 22-01-2023 13:00:00, ON, Stop1, Company1, Bus37, 5500005555555559
2, 22-01-2023 13:05:00, OFF, Stop2, Company1, Bus37, 5500005555555559
3, 22-01-2023 09:20:00, ON, Stop3, Company1, Bus36, 4111111111111111
4, 23-01-2023 08:00:00, ON, Stop1, Company1, Bus37, 4111111111111111
5, 23-01-2023 08:02:00, OFF, Stop1, Company1, Bus37, 4111111111111111
6, 24-01-2023 16:30:00, OFF, Stop2, Company1, Bus37, 5500005555555559
trips.csv [output file]
You will need to match the tap ons and tap offs to create trips. You will need to determine how much to charge
for the trip based on whether it was complete, incomplete or cancelled and where the tap on and tap off
occurred. You will need to write the trips out to a file called trips.csv.
Example output file (for Tap IDs 1 &2) :
Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID,
PAN, Status
22-01-2018 13:00:00, 22-01-2018 13:05:00, 900, Stop1, Stop2, $3.25, Company1,
Bus37, 5500005555555559, COMPLETED


SOLUTION - Created a springboot application to read taps.csv and generate trips.csv
ASSUMPTIONS- 
1.Data in csv is valid and has no missing enteries
2. N/A is used in CSV to denote fields that do not make sense for trip type
3. For a trip to be cancelled, there is should be a time limit. After that time limit the next tap will be ON and CSV is assumed to be correct in this scenario.

GENERATED trips.csv can be found under resources folder.

Due to time constraints, main focus is on business logic and implemenation.









