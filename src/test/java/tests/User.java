package tests;

import it.polimi.gsonld.annotations.*;

/**
 * Created by riccardo on 24/08/2017.
 */
@JsonLD()
@Prefix(prefix = "schema", uri = "http://www.schema.org/")
@Type("schema:User")
public class User {
    @Property("schema:name")
    public String name;
    @NameSpace("http://www.usethisnamespace.com/")
    public String email;

    @NameSpace("http://www.usethisnamespace.com/")
    @Type()
    public String homepage;
    @NameSpace("http://www.usethisnamespace.com/")
    @Type("xsd:int")
    public int age;
    public boolean isDeveloper;

    public User(String name, String email, int age, boolean isDeveloper) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.isDeveloper = isDeveloper;
    }
}
