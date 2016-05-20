package net.nickhall.streamproxy.service.impl;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import no.digipost.dropwizard.TypeSafeConfigBundle;

public class StreamProxyApplication extends Application<StreamProxyConfiguration> {
    @Override
    public void initialize(Bootstrap<StreamProxyConfiguration> bootstrap) {
        bootstrap.addBundle(new TypeSafeConfigBundle());
    }

    @Override
    public void run(StreamProxyConfiguration streamProxyConfiguration, Environment environment) throws Exception {
        final StreamProxyService eventProxyService = new StreamProxyService();
        environment.jersey().register(eventProxyService);
    }

    public static void main(String[] args) throws Exception {
        new StreamProxyApplication().run(args);
    }
}
