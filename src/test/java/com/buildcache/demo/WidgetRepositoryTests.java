package com.buildcache.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest swaps in an embedded database (H2, on the test classpath) automatically - no
// real Postgres needed to compile and run this, which is what lets CI's two plain `./gradlew
// build` runs (the whole point of this repo) succeed without any infrastructure at all.
@DataJpaTest
class WidgetRepositoryTests {

    @Autowired
    private WidgetRepository widgets;

    @Test
    void savesAndFindsAWidget() {
        widgets.save(new Widget("sprocket"));

        assertThat(widgets.findAll()).extracting(Widget::getName).contains("sprocket");
    }
}
