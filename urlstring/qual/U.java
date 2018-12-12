package qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.checkerframework.framework.qual.SubtypeOf;

/** Denotes that the representation of an object is encrypted. */
@SubtypeOf(PossiblyUrl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface U {
    boolean hasScheme() default true;
    int parameters() default 3;
    //0 = end with host
    //1 end with path
    //2 = end with query
    //3 = end with fragment
    // i subset of j if i < j
}
