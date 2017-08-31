package it.polimi.gsonld.tests.playground.library;

import it.polimi.gsonld.annotations.*;
import it.polimi.gsonld.annotations.Object;

/**
 * Created by riccardo on 30/08/2017.
 */

@Object
@Alias(alias = "dc", value = "http://purl.org/dc/elements/1.1/")
@Alias(alias = "ex", value = "http://example.org/vocab#")
@Alias(alias = "xsd", value = "http://www.w3.org/2001/XMLSchema#")
public class Library {

    @Graph(id = "http://example.org/library", type = "ex:Library")
    @Type
    @Property("ex:contains")
    public String a = "http://example.org/library/the-republic";

    @Graph(id = "http://example.org/library/the-republic", type = "ex:Book")
    @Property("ex:contains")
    public String x = "http://example.org/library/the-republic#introduction";
    @Property("dc:title")
    @Graph(id = "http://example.org/library/the-republic", type = "ex:Book")
    public String y = "The Republic";
    @Property("dc:creator")
    @Graph(id = "http://example.org/library/the-republic", type = "ex:Book")
    public String k = "Plato";

    @Graph(id = "http://example.org/library/the-republic#introduction", type = "ex:Chapter")
    @Property("dc:description")
    public String z = "An introductory chapter on The Republic.";
    @Graph(id = "http://example.org/library/the-republic#introduction", type = "ex:Chapter")
    @Property("dc:title")
    public String h = "The Introduction";

}
