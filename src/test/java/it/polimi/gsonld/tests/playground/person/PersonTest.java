package it.polimi.gsonld.tests.playground.person;

import com.github.jsonldjava.core.JsonLdUtils;
import com.github.jsonldjava.utils.JsonUtils;
import com.google.common.io.Files;
import com.google.gson.Gson;
import it.polimi.gsonld.JsonLDSerializerFactory;
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
public class PersonTest {

    private Gson customGson;
    private File person, expanded;

    @Before
    public void init() {
        this.person = new File("src/test/resources/person.jsonld");
        this.expanded = new File("src/test/resources/person_expanded.jsonld");
        JsonLDSerializerFactory.register("it.polimi.gsonld.tests.playground.person");
        this.customGson = JsonLDSerializerFactory.getGson();
    }

    @Test
    public void VocabTestField() throws IOException {
        String s = customGson.toJson(new Person());
        System.out.println(s);
        test(Files.asByteSource(person).openStream(), getInputStream(s));
    }


    @Test
    public void VocabTestMethods() throws IOException {
        String s = customGson.toJson(new PersonGetters());
        System.out.println(s);
        test(Files.asByteSource(person).openStream(), getInputStream(s));
    }

    public void ExpandedTestFields() throws IOException {
        String s = customGson.toJson(new PersonExpanded());
        System.out.println(s);
        test(Files.asByteSource(expanded).openStream(), getInputStream(s));
    }


    private InputStream getInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    private void test(InputStream expected, InputStream actual) throws IOException {
        Object e = JsonUtils.fromInputStream(expected);
        Object a = JsonUtils.fromInputStream(actual);

        assertTrue(JsonLdUtils.deepCompare(e, a));
    }
}
