package it.polimi.gsonld.tests.playground.recipes;

import it.polimi.gsonld.annotations.Alias;
import it.polimi.gsonld.annotations.Property;
import it.polimi.gsonld.annotations.Type;

/**
 * Created by riccardo on 28/08/2017.
 */

@Alias(alias = "xsd", value = "http://www.w3.org/2001/XMLSchema#")
@Alias(value = "http://rdf.data-vocabulary.org/#step", alias = "step")
@Alias(value = "http://rdf.data-vocabulary.org/#description", alias = "description")
public class Step {

    @Property("description")
    public String description;

    @Property("http://rdf.data-vocabulary.org/#step")
    @Type("xsd:integer")
    public int step;

    public Step(int number, String description) {
        this.description = description;
        this.step = number;
    }

}
