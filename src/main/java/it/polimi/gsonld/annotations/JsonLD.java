package it.polimi.gsonld.annotations;

import java.lang.annotation.*;

/**
 * Created by riccardo on 24/08/2017.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonLD {

    boolean expand_prefixes() default false;
    boolean autogen_prefixes() default false;
}