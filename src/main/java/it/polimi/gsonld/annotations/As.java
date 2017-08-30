package it.polimi.gsonld.annotations;

import java.lang.annotation.*;

/**
 * Created by riccardo on 29/08/2017.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface As {

    String value() default "[Unassigned]";

}
