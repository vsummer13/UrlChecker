//package urlstring.qual;
package qual;

import org.checkerframework.framework.qual.SubtypeOf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Denotes that the representation of an object might not be encrypted. */

@SubtypeOf({Path.class,Query.class,Fragment.class })
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})


public @interface EmptyString {
}
