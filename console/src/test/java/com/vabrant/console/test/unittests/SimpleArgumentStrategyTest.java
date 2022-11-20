package com.vabrant.console.test.unittests;

import com.vabrant.console.arguments.strategies.simple.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleArgumentStrategyTest {

    @Test
    void doubleStrategy() {
        SimpleDoubleArgumentStrategy strat = new SimpleDoubleArgumentStrategy();
        assertTrue(strat.isType("0d"));
        assertTrue(strat.isType("6D"));
        assertTrue(strat.isType("554.43d"));
        assertTrue(strat.isType(".57680d"));
        assertFalse(strat.isType("6"));
        assertFalse(strat.isType("fjfiowjfowjf"));
        assertFalse(strat.isType("6f"));
    }

    @Test
    void intStrategy() {
        SimpleIntArgumentStrategy strat = new SimpleIntArgumentStrategy();
        assertTrue(strat.isType("0"));
        assertTrue(strat.isType("8020"));
        assertTrue(strat.isType("09073"));
        assertTrue(strat.isType("0898"));
        assertFalse(strat.isType("0.3"));
        assertFalse(strat.isType("0f"));
        assertFalse(strat.isType("jfiojwfowjo"));
    }

    @Test
    void floatStrategy() {
        SimpleFloatArgumentStrategy strat = new SimpleFloatArgumentStrategy();
        assertTrue(strat.isType("0f"));
        assertTrue(strat.isType("045.78"));
        assertTrue(strat.isType("0.4f"));
        assertTrue(strat.isType(".89F"));
        assertFalse(strat.isType("0"));
        assertFalse(strat.isType("0d"));
        assertFalse(strat.isType("gifjweofow"));
    }

    @Test
    void longStrategy() {
        SimpleLongArgumentStrategy strat = new SimpleLongArgumentStrategy();
        assertTrue(strat.isType("0l"));
        assertTrue(strat.isType("74824728L"));
        assertFalse(strat.isType("0.99L"));
        assertFalse(strat.isType("l898"));
        assertFalse(strat.isType("89"));
    }

    @Test
    void instanceReferenceStrategy() {
        SimpleInstanceReferenceArgumentStrategy strat = new SimpleInstanceReferenceArgumentStrategy();
        assertTrue(strat.isType("object"));
        assertFalse(strat.isType("object.method"));
        assertFalse(strat.isType("0.4f"));
    }

    @Test
    void stringStrategy() {
        SimpleStringArgumentStrategy strat = new SimpleStringArgumentStrategy();
        assertTrue(strat.isType("\"Hello World\""));
        assertFalse(strat.isType("fowfjwof"));
    }


}