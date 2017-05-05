package org.ops4j.ramler.generator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NamesTest {

    @Test
    public void foo() {
        assertThat(Names.buildConstantName("oneTwoThree"), is("ONE_TWO_THREE"));
    }
}
