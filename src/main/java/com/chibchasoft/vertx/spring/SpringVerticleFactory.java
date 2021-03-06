/*
 * Copyright (c) 2016 chibchasoft.com
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution.
 *
 *      The Apache License v2.0 is available at
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Author <a href="mailto:jvelez@chibchasoft.com">Juan Velez</a>
 */
package com.chibchasoft.vertx.spring;

import java.util.Objects;

import org.springframework.context.ApplicationContext;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;

/**
 * <p>A Verticle Factory that relies on a {@link ApplicationContext Spring Application Context}
 * to get the requested Verticles. A verticleName must match a bean name/id within the Application
 * Context.</p>
 * <p>This factory relies on {@link ApplicationContextProvider} to provide the actual
 * Spring Application Context.</p>
 * <p>Verticles themselves should be prototype scope.</p>
 * 
 * @author <a href="mailto:jvelez@chibchasoft.com">Juan Velez</a>
 */
public class SpringVerticleFactory implements VerticleFactory {
    private static final String PREFIX = "spring";

    public SpringVerticleFactory() {
    }

    /* (non-Javadoc)
     * @see io.vertx.core.spi.VerticleFactory#prefix()
     */
    @Override
    public String prefix() {
        return PREFIX;
    }

    /* (non-Javadoc)
     * @see io.vertx.core.spi.VerticleFactory#blockingCreate()
     */
    @Override
    public boolean blockingCreate() {
        return true;
    }

    /* (non-Javadoc)
     * @see io.vertx.core.spi.VerticleFactory#createVerticle(java.lang.String, java.lang.ClassLoader)
     */
    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        Objects.requireNonNull(verticleName, "Verticle Name is required");
        verticleName = VerticleFactory.removePrefix(verticleName);
        Objects.requireNonNull(verticleName, "Verticle Name must be more than just the prefix");
        ApplicationContext ctx = getApplicationContext();
        if (!ctx.containsBean(verticleName))
            throw new IllegalArgumentException(String.format("No bean found for %s", verticleName));

        if (!ctx.isPrototype(verticleName))
            throw new IllegalArgumentException(String.format("Bean %s needs to be of Prototype scope", verticleName));

        return (Verticle) ctx.getBean(verticleName);
    }

    /**
     * Gets the application context to be used for obtaining the verticles.
     * @return The application context
     */
    private ApplicationContext getApplicationContext() {
        ApplicationContext appCtx = ApplicationContextProvider.getApplicationContext();
        if (appCtx == null) {
            throw new IllegalStateException("No Application Context Instance has been "
                                            + "set in ApplicationContextProvider.");
        }
        return appCtx;
    }
}