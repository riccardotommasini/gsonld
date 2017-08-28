package it.polimi.gsonld.annotations;

import java.lang.annotation.*;

/**
 * Created by riccardo on 24/08/2017.
 */
@Repeatable(Aliases.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Alias {

    String alias();

    String value();

    String type() default "[Unassigned]";

}