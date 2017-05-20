package org.ops4j.ramler.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class NamesTest {

    @Test
    public void foo() {
        assertThat(Names.buildConstantName("oneTwoThree")).isEqualTo("ONE_TWO_THREE");
    }
}
