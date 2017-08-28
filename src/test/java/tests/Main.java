package tests;

import com.google.gson.Gson;
import it.polimi.gsonld.JsonLDSerializerFactory;

/**
 * Created by riccardo on 23/08/2017.
 */
public class Main {

    public static void main(String[] args) {

        JsonLDSerializerFactory.register("it.polimi.jsonld.tests");

        Gson customGson = JsonLDSerializerFactory.getGson();

        User userObject = new User(
                "Norman",
                "norman@futurestud.io",
                26,
                true
        );


        System.out.println(customGson.toJson(userObject));

        UserNoType notype = new UserNoType(
                "Norman",
                "norman@futurestud.io",
                26,
                true
        );
        System.out.println(customGson.toJson(notype));


        UserSubClass subUser = new UserSubClass(
                "Norman",
                "norman@futurestud.io",
                26,
                true,
                "Polimi"
        );

        System.out.println(customGson.toJson(subUser));


        UserSubSubClass ss = new UserSubSubClass("Norman",
                "norman@futurestud.io",
                26,
                true,
                "Polimi",
                11 / 4
        );

        System.out.println(customGson.toJson(ss));

        System.out.println(customGson.toJson(new Recipe()));

    }
}



