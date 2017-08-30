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
public class PersonGetters {

    private String name = "Jane Doe";
    private String jobTitle = "Professor";
    private String telephone = "(425) 123-4567";
    private String url = "http://www.janedoe.com";

    public String getName() {
        return name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getUrl() {
        return url;
    }
}
