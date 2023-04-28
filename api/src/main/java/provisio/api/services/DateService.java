package provisio.api.services;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class DateService {

    protected int getDateDifference(String checkIn, String checkOut){
        DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        return Days.daysBetween(isoFormat.parseDateTime(checkIn), isoFormat.parseDateTime(checkOut)).getDays();
    }

    protected boolean validateDates(String checkIn, String checkOut){
        DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime checkInDateTime = isoFormat.parseDateTime(checkIn);
        DateTime checkOutDateTime = isoFormat.parseDateTime(checkOut);
        boolean isValidCheckInDate  = checkOutDateTime.plusDays(1).isAfterNow(); //add 1 day for leeway / wiggle room
        boolean isValidDates = checkOutDateTime.isAfter(checkInDateTime);
        return (isValidCheckInDate && isValidDates);
    }

    protected ArrayList<String> getRange(String checkIn, String checkOut){
        DateTimeFormatter isoDateOnlyFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        ArrayList<String> days = new ArrayList<>();
        DateTime checkOutDateTime = isoDateOnlyFormat.parseDateTime(checkOut);
        for (
                MutableDateTime currentDateTime = new MutableDateTime(isoDateOnlyFormat.parseDateTime(checkIn));
                currentDateTime.isBefore(checkOutDateTime);
                currentDateTime.addDays(1)
            )
        {
            days.add(isoDateOnlyFormat.print(currentDateTime));
        }
        return days;
    }

}
