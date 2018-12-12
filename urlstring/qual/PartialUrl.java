
package qual;

import java.lang.annotation.Target;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.SubtypeOf;



@InvisibleQualifier
@SubtypeOf(PossiblyUrl.class)
@Target({}) 



public @interface PartialUrl {


    String value() default "";
}
