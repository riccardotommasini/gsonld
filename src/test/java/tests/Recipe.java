package tests;

import it.polimi.gsonld.annotations.*;

/**
 * Created by riccardo on 24/08/2017.
 */
@JsonLD()
@Prefix(prefix = "schema", uri = "http://www.schema.org/")
@Type("schema:Recipe")
@Alias(value = "http://rdf.data-vocabulary.org/#yield", alias = "yield")
@Alias(value = "http://rdf.data-vocabulary.org/#instructions", alias = "instructions")
@Alias(value = "http://rdf.data-vocabulary.org/#ingredients", alias = "ingredient")
public class Recipe {

    @Property("ingredient")
    public String mint = "12 fresh mint leaves";


    @Property("ingredient")
    public String lime = "1/2 lime, juiced with pulp";

    @Property("ingredient")
    public String sugar = "1 tablespoons white sugar";


    @Property("ingredient")
    public String ice = "1 cup ice cubes";


    @Property("ingredient")
    public String rum = "2 fluid ounces white rum";


    @Property("ingredient")
    public String soda = "1/2 cup club soda";

    @Property("instructions")
    public Step[] steps = new Step[]{
            new Step(1, "Crush lime juice, mint and sugar together in glass."),
            new Step(2, "Fill glass to top with ice cubes."),
            new Step(3, "Pour white rum over ice."),
            new Step(4, "Fill the rest of glass with club soda, stir."),
            new Step(5, "Garnish with a lime wedge.")
    };


}
