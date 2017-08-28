package tests;

import it.polimi.gsonld.annotations.Alias;
import it.polimi.gsonld.annotations.JsonLD;
import it.polimi.gsonld.annotations.Prefix;
import it.polimi.gsonld.annotations.Property;

/**
 * Created by riccardo on 28/08/2017.
 */

@Property("http://rdf.data-vocabulary.org/#step")
@Prefix(prefix = "xsd", uri = "http://www.w3.org/2001/XMLSchema#")
@Alias(value = "http://rdf.data-vocabulary.org/#step", alias = "step")
@Alias(value = "http://rdf.data-vocabulary.org/#description", alias = "description", type = "xsd:integer")
public class Step {

    @Property("description")
    public String description;

    @Property("step")
    public int step;

    public Step(int number, String description) {
        this.description = description;
        this.step = number;
    }

}
