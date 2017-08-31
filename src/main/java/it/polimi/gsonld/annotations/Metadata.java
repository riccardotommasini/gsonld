package it.polimi.gsonld.annotations;

import java.lang.annotation.*;

/**
 * Created by riccardo on 30/08/2017.
 * - if associated to class, all the elements go in the graph. If an @Id is associated to the class that became the graph @id //TODO @NamedGraph
 * - if associated to a field or method, the related property goes into a graph
 * - the annotation @GraphMetadata works the other way round, if associated to a field or method, that property
 * is a metadata. I think this might be useful when I have less metadata than graph elements.
 * //TODO @GraphMetadata
 * //TODO Prefixed metada e.g. @Timestamp -> generatedAt
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Metadata {

    String id() default "[unassigned]"; // @Graph ID
    String type() default "@id"; // @Graph ID

}
