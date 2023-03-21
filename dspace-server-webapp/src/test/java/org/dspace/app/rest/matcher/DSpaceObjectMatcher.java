/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.matcher;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.notNullValue;

import org.hamcrest.Matcher;

public class DSpaceObjectMatcher {

    private DSpaceObjectMatcher() { }

    public static Matcher<? super Object> match() {
        return allOf(
            hasJsonPath("$.uuid", notNullValue())
        );
    }

}
