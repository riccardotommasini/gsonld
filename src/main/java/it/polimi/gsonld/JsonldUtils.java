package it.polimi.gsonld;

import java.util.regex.Pattern;

/**
 * Created by riccardo on 28/08/2017.
 */
public class JsonldUtils {


    public static String JSONLD_TYPE = "@type";
    /**
     * Used to uniquely identify things that are being described in the document with IRIs or blank node identifiers. This keyword is described in section 5.3 Node Identifiers.
     **/
    public static String JSONLD_ID = "@id";
    public static String JSONLD_CONTEXT = "@context";
    public static String JSONLD_LIST = "@list";
    public static String JSONLD_LANG = "@language";
    public static String JSONLD_GRAPH = "@graph";
    public static String JSONLD_REVERSE = "@reverse";
    public static String JSONLD_INDEX = "@index";
    public static String JSONLD_BASE = "@base";
    public static String JSONLD_VOCAB = "@vocab";
    public static String JSONLD_SEPARATOR = ":";


    private static String uri_regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public static Pattern uri_pattern = Pattern.compile(uri_regex);


    public static class MediaTypes {
        public static String base_uri = "http://www.w3.org/ns/json-ld#";
        public static String FLATTENED = base_uri + "flattened";
        public static String EXPANDED = base_uri + "xpanded";
        public static String COMPACTED = base_uri + "compacted";
    }
}
