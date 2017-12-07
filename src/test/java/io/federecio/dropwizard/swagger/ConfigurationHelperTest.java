package io.federecio.dropwizard.swagger;

import static junit.framework.TestCase.assertEquals;


import org.junit.Before;
import org.junit.Test;

import io.dropwizard.Configuration;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;

public class ConfigurationHelperTest {

    private Configuration configuration;
    private SwaggerBundleConfiguration swaggerBundleConfiguration;

    private void setApplicationContextPath(String appContext) {
        final ServerFactory serverFactory = configuration.getServerFactory();
        if (serverFactory instanceof SimpleServerFactory) {
            ((SimpleServerFactory) serverFactory).setApplicationContextPath(appContext);
        } else {
            ((DefaultServerFactory) serverFactory).setApplicationContextPath(appContext);
        }
    }

    private void setRootPath(String rootPath) {
        final ServerFactory serverFactory = configuration.getServerFactory();

        if (serverFactory instanceof SimpleServerFactory) {
            ((SimpleServerFactory) serverFactory).setJerseyRootPath(rootPath);
        } else {
            ((DefaultServerFactory) serverFactory).setJerseyRootPath(rootPath);
        }
    }

    private void setViewPath(String viewPath) {
        swaggerBundleConfiguration.setViewUriRoot(viewPath);
    }

    @Before
    public void setUp() {
        configuration = new TestConfiguration();
        swaggerBundleConfiguration = new SwaggerBundleConfiguration();
    }

    @Test
    public void defaults() {
        // no overrides
        ConfigurationHelper helper = new ConfigurationHelper(configuration, swaggerBundleConfiguration);

        assertEquals("", helper.getSwaggerClientPath());
        assertEquals("", helper.getSwaggerUriPath());
        assertEquals("/swagger-static", helper.getSwaggerStaticUriPath());
        assertEquals("/swagger-static", helper.getSwaggerAssetBundleServletPath());
    }

    @Test
    public void defaultsAppContextRoot() {
        // app context is /
        setApplicationContextPath("/");
        ConfigurationHelper helper = new ConfigurationHelper(configuration, swaggerBundleConfiguration);

        assertEquals("", helper.getSwaggerClientPath());
        assertEquals("", helper.getSwaggerUriPath());
        assertEquals("/swagger-static", helper.getSwaggerStaticUriPath());
        assertEquals("/swagger-static", helper.getSwaggerAssetBundleServletPath());
    }

    @Test
    public void appContextSet() {
        // app context is /app but everything thinks they are at /
        setApplicationContextPath("/app");
        ConfigurationHelper helper = new ConfigurationHelper(configuration, swaggerBundleConfiguration);

        // the client thinks it is at /app
        assertEquals("/app", helper.getSwaggerClientPath());
        assertEquals("", helper.getSwaggerUriPath());
        // the swagger-static uris to the client are at /app
        assertEquals("/app/swagger-static", helper.getSwaggerStaticUriPath());
        // the servlet thinks it is at /
        assertEquals("/swagger-static", helper.getSwaggerAssetBundleServletPath());
    }

    @Test
    public void appContextAndViewSet() {
        // app context is /app but everything thinks they are at /
        // view is /docs
        setApplicationContextPath("/app");
        setViewPath("/docs");
        ConfigurationHelper helper = new ConfigurationHelper(configuration, swaggerBundleConfiguration);

        assertEquals("/app/docs", helper.getSwaggerClientPath());
        // servlet thinks it is at /
        assertEquals("/docs", helper.getSwaggerUriPath());
        // the swagger-static uris to the client are at /app
        assertEquals("/app/docs/swagger-static", helper.getSwaggerStaticUriPath());
        // the servlet thinks it is at /
        assertEquals("/docs/swagger-static", helper.getSwaggerAssetBundleServletPath());
    }

    @Test
    public void appContextAndRootPathSet() {
        // app context is /app but everything thinks they are at /api
        setApplicationContextPath("/app");
        setRootPath("/api");
        ConfigurationHelper helper = new ConfigurationHelper(configuration, swaggerBundleConfiguration);

        assertEquals("/app/api", helper.getSwaggerClientPath());
        assertEquals("/api", helper.getSwaggerUriPath());
        assertEquals("/app/api/swagger-static", helper.getSwaggerStaticUriPath());
        assertEquals("/api/swagger-static", helper.getSwaggerAssetBundleServletPath());
    }

    @Test
    public void appContextAndRootPathAndViewSet() {
        setApplicationContextPath("/app");
        setRootPath("/api");
        setViewPath("/docs");
        ConfigurationHelper helper = new ConfigurationHelper(configuration, swaggerBundleConfiguration);

        assertEquals("/app/api/docs", helper.getSwaggerClientPath());
        assertEquals("/api/docs", helper.getSwaggerUriPath());
        assertEquals("/app/api/docs/swagger-static", helper.getSwaggerStaticUriPath());
        assertEquals("/api/docs/swagger-static", helper.getSwaggerAssetBundleServletPath());
    }

    @Test
    public void rootSet() {
        setApplicationContextPath("/");
        setRootPath("/api");
        ConfigurationHelper helper = new ConfigurationHelper(configuration, swaggerBundleConfiguration);

        assertEquals("/api", helper.getSwaggerClientPath());
        assertEquals("/api", helper.getSwaggerUriPath());
        assertEquals("/api/swagger-static", helper.getSwaggerStaticUriPath());
        assertEquals("/api/swagger-static", helper.getSwaggerAssetBundleServletPath());
    }

    @Test
    public void swaggerViewSet() {
        swaggerBundleConfiguration.setViewUriRoot("/docs");
        ConfigurationHelper helper = new ConfigurationHelper(configuration, swaggerBundleConfiguration);

        assertEquals("/docs", helper.getSwaggerClientPath());
        assertEquals("/docs", helper.getSwaggerUriPath());
        assertEquals("/docs/swagger-static", helper.getSwaggerStaticUriPath());
        assertEquals("/docs/swagger-static", helper.getSwaggerAssetBundleServletPath());
    }
}
