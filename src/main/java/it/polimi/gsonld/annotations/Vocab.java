package it.polimi.gsonld.annotations;

import java.lang.annotation.*;

/**
 * Created by riccardo on 24/08/2017.
 * Used to expand properties and values in @type with a common prefix IRI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Vocab {

    String value() default "http://schema.org/";

}