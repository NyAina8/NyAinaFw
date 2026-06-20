package mg.nyainafw.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyController {
    String value() default "";
    boolean enabled() default true;
}
