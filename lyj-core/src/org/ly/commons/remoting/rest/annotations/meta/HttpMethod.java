/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ly.commons.remoting.rest.annotations.meta;

import java.lang.annotation.*;

/**
 * Associates the name of a HTTP method with an annotation.
 * A Java method annotated with a runtime annotation that is itself annotated with
 * this annotation will be used to handle HTTP requests of the indicated HTTP method.
 * It is an error for a method to be annotated with more than one annotation that is
 * annotated with HttpMethod.
 */
@Target(value= ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {
    String value();
}
