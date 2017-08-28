package tests;

import it.polimi.gsonld.annotations.Prefix;
import it.polimi.gsonld.annotations.Property;
import it.polimi.gsonld.annotations.Type;

/**
 * Created by riccardo on 24/08/2017.
 */
@Type("http://www.example3.org/SubUser")
@Prefix(prefix = "xsd", uri = "http://www.w3.org/2001/XMLSchema#")
@Prefix(prefix = "rdfs", uri = "http://www.w3.org/2001/XMLSchema#")
public class UserSubClass extends User {
    @Property("schema:School")
    public String school;

    public UserSubClass(String name, String email, int age, boolean isDeveloper, String school) {
        super(name, email, age, isDeveloper);
        this.school = school;
    }


}
