package org.wpstarters.repositoriesprocessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(value = RetentionPolicy.CLASS)
@Target(value = { TYPE })
public @interface DefaultJpaRepositories {

}
