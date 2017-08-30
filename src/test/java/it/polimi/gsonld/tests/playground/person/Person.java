package it.polimi.gsonld.tests.playground.person;

import it.polimi.gsonld.annotations.Object;
import it.polimi.gsonld.annotations.Type;
import it.polimi.gsonld.annotations.Vocab;

/**
 * Created by riccardo on 24/08/2017.
 */
@Object()
@Vocab()
@Type("Person")
public class Person {

    public String name = "Jane Doe";
    public String jobTitle = "Professor";
    public String telephone = "(425) 123-4567";
    public String url = "http://www.janedoe.com";

}
