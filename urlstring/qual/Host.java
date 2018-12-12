
package qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.checkerframework.framework.qual.SubtypeOf;

/** Denotes that the representation of an object might not be encrypted. */

@SubtypeOf({PossiblyUrl.class, U.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})


public @interface Host {
	String value() default "";
}
