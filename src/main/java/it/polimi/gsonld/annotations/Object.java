package it.polimi.gsonld.annotations;

import java.lang.annotation.*;

/**
 * Created by riccardo on 24/08/2017.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Object {

    boolean expanded() default false;

    boolean flattened() default false;

    boolean framed() default false;
}