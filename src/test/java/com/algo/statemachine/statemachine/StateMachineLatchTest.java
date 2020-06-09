package com.algo.statemachine.statemachine;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: Priytam Jee Pandey
 * Date: 08/06/20
 * Time: 4:20 pm
 * email: priytam.pandey@cleartrip.com
 */
public class StateMachineLatchTest {

    @Test
    public void shouldBlock() throws Throwable {
        long time = System.currentTimeMillis();
        StateMachineLatch stateMachineLatch = new StateMachineLatch("", 2000);
        stateMachineLatch.block();
        Assert.assertTrue(System.currentTimeMillis() - time >= 2000);
    }

    @Test
    public void shouldExpire() throws Throwable {
        StateMachineLatch stateMachineLatch = new StateMachineLatch("", 2000);
        stateMachineLatch.block();
        Assert.assertTrue(stateMachineLatch.isExpired());
    }

    @Test
    public void shouldRelease() throws Throwable {
        long time = System.currentTimeMillis();
        StateMachineLatch stateMachineLatch = new StateMachineLatch("", 2000);
        new Thread( () -> {
            try {
                Thread.sleep(1000);
                stateMachineLatch.release(null);
            } catch (Throwable ignored) {
            }
        }).start();
        stateMachineLatch.block();
        Assert.assertFalse(System.currentTimeMillis() - time < 2000);
    }

    @Test
    public void eventShouldBeSame() throws Throwable {
        StateMachineLatch stateMachineLatch = new StateMachineLatch("a", 2000);
        stateMachineLatch.block();
        Assert.assertEquals("a", stateMachineLatch.getEvent());
    }
}