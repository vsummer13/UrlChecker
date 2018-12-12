//package urlstring.qual;
package qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.ImplicitFor;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.LiteralKind;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TargetLocations;
import org.checkerframework.framework.qual.TypeUseLocation;

/**
 * The bottom type in the Regex type system. Programmers should rarely write this type.
 *
 * @checker_framework.manual #regex-checker Regex Checker
 * @checker_framework.manual #bottom-type the bottom type
 */
@InvisibleQualifier
@ImplicitFor(literals = LiteralKind.NULL, typeNames = java.lang.Void.class)
@SubtypeOf({U.class, PartialUrl.class,Host.class,Scheme.class,Path.class,Query.class,Fragment.class})
@DefaultFor(value = {TypeUseLocation.LOWER_BOUND})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@TargetLocations({TypeUseLocation.EXPLICIT_LOWER_BOUND, TypeUseLocation.EXPLICIT_UPPER_BOUND})
public @interface UBottom {
}
