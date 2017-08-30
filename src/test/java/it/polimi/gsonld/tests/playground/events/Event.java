package it.polimi.gsonld.tests.playground.events;

import it.polimi.gsonld.annotations.Alias;
import it.polimi.gsonld.annotations.Object;
import it.polimi.gsonld.annotations.Prefix;
import it.polimi.gsonld.annotations.Type;

/**
 * Created by riccardo on 29/08/2017.
 */

@Object
@Alias(alias = "xsd", value = "http://www.w3.org/2001/XMLSchema#")
@Alias(alias = "ical", value = "http://www.w3.org/2002/12/cal/ical#")
public class Event {

    @Prefix("ical")
    @Type("xsd:dateTime")
    public String dtstart = "2011-04-09T20:00Z";

    @Prefix("ical")
    public String getLocation() {
        return "New Orleans Arena, New Orleans, Louisiana, USA";
    }

    private String summary = "Lady Gaga Concert";

    @Prefix("ical")
    public String getSummary() {
        return summary;
    }
}
