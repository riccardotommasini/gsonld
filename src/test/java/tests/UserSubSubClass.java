package tests;

import it.polimi.gsonld.annotations.BaseVocab;
import it.polimi.gsonld.annotations.Property;
import it.polimi.gsonld.annotations.Type;

/**
 * Created by riccardo on 24/08/2017.
 */
@Type("http://www.example5.org/SubUser")
@BaseVocab
public class UserSubSubClass extends UserSubClass {
    @Property("www.example6.org/Avg")
    @Type("xsd:float")
    public float avg;

    public UserSubSubClass(String name, String email, int age, boolean isDeveloper, String school, float avg) {
        super(name, email, age, isDeveloper, school);
        this.avg = avg;
    }


}
