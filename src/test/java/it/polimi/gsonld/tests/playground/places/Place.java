package it.polimi.gsonld.tests.playground.places;

import it.polimi.gsonld.annotations.*;
import it.polimi.gsonld.annotations.Object;

/**
 * Created by riccardo on 29/08/2017.
 */

@Object
@Alias(alias = "xsd", value = "http://www.w3.org/2001/XMLSchema#")
public class Place {

    @Property("http://schema.org/name")
    public String name = "The Empire State Building";

    @Property("http://schema.org/description")
    @As("description")
    public String descr = "The Empire State Building is a 102-story landmark in New York City.";

    @Type
    @Property("http://schema.org/image")
    public String image = "http://www.civil.usherbrooke.ca/cours/gci215a/empire-state-building.jpg";

    private Geo geo = new Geo();

    @Property("http://schema.org/geo")
    public Geo getGeo(){
        return geo;
    }

}
