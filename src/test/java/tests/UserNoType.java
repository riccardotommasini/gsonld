package tests;

import it.polimi.gsonld.annotations.JsonLD;

/**
 * Created by riccardo on 24/08/2017.
 */
@JsonLD()
public class UserNoType {
    public String name;
    public String email;
    public int age;
    public boolean isDeveloper;

    public UserNoType(String name, String email, int age, boolean isDeveloper) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.isDeveloper = isDeveloper;
    }
}
