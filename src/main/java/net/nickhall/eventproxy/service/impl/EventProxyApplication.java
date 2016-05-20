package net.nickhall.eventproxy.service.impl;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import no.digipost.dropwizard.TypeSafeConfigBundle;

public class EventProxyApplication extends Application<EventProxyConfiguration> {
    @Override
    public void initialize(Bootstrap<EventProxyConfiguration> bootstrap) {
        bootstrap.addBundle(new TypeSafeConfigBundle());
    }

    @Override
    public void run(EventProxyConfiguration eventProxyConfiguration, Environment environment) throws Exception {
        final EventProxyService eventProxyService = new EventProxyService();
        environment.jersey().register(eventProxyService);
    }

    public static void main(String[] args) throws Exception {
        new EventProxyApplication().run(args);
    }
}
