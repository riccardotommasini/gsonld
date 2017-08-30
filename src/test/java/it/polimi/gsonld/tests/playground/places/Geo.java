package it.polimi.gsonld.tests.playground.places;

import it.polimi.gsonld.annotations.As;
import it.polimi.gsonld.annotations.Property;
import it.polimi.gsonld.annotations.Type;

/**
 * Created by riccardo on 29/08/2017.
 */
@Property("http://schema.org/geo")
public class Geo {

    @Type("xsd:float")
    @Property("http://schema.org/latitude")
    @As("latitude")
    public float lat = Float.parseFloat("40.75");

    @Property("http://schema.org/longitude")
    @Type("xsd:float")
    @As()
    public Float longitude = Float.parseFloat("73.98");;

}
