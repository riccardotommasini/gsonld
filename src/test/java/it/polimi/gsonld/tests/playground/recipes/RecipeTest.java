package it.polimi.gsonld.tests.playground.recipes;

import com.github.jsonldjava.core.JsonLdUtils;
import com.github.jsonldjava.utils.JsonUtils;
import com.google.common.io.Files;
import com.google.gson.Gson;
import it.polimi.gsonld.JsonLDSerializerFactory;
import it.polimi.gsonld.tests.playground.places.Place;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by riccardo on 28/08/2017.
 */
public class RecipeTest {

    private Gson customGson;
    private File recipe;

    @Before
    public void init() {
        this.recipe = new File("src/test/resources/recipe.jsonld");
        JsonLDSerializerFactory.register("it.polimi.gsonld.tests.playground.recipes");
        this.customGson = JsonLDSerializerFactory.getGson();
    }

    @Test
    public void VocabTestField() throws IOException {
        String s = customGson.toJson(new Recipe());
        System.out.println(s);
        test(Files.asByteSource(recipe).openStream(), getInputStream(s));
    }

    private InputStream getInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    private void test(InputStream expected, InputStream actual) throws IOException {
        Object e = JsonUtils.fromInputStream(expected);
        Object a = JsonUtils.fromInputStream(actual);

        assertTrue(JsonLdUtils.deepCompare(e,a));
    }
}
