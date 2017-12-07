// Copyright (C) 2014 Federico Recio
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.federecio.dropwizard.swagger;

import io.dropwizard.Configuration;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;

import java.util.Optional;

/**
 * Wrapper around Dropwizard's configuration and the bundle's config that
 * simplifies getting some information from them.
 */
public class ConfigurationHelper {

    private final Configuration configuration;
    private final SwaggerBundleConfiguration swaggerBundleConfiguration;

    /**
     * Constructor
     *
     * @param configuration
     * @param swaggerBundleConfiguration
     */
    public ConfigurationHelper(Configuration configuration,
            SwaggerBundleConfiguration swaggerBundleConfiguration) {
        this.configuration = configuration;
        this.swaggerBundleConfiguration = swaggerBundleConfiguration;
    }

    public String getJerseyRootPath() {
        // if the user explicitly defined a path to prefix requests use it
        // instead of derive it
        if (swaggerBundleConfiguration.getUriPrefix() != null) {
            return swaggerBundleConfiguration.getUriPrefix();
        }

        final ServerFactory serverFactory = configuration.getServerFactory();

        final Optional<String> rootPath;
        if (serverFactory instanceof SimpleServerFactory) {
            rootPath = ((SimpleServerFactory) serverFactory)
                    .getJerseyRootPath();
        } else {
            rootPath = ((DefaultServerFactory) serverFactory)
                    .getJerseyRootPath();
        }

        return stripUrlSlashes(rootPath.orElse("/"));
    }

    public String getUrlPattern() {
        // if the user explicitly defined a path to prefix requests use it
        // instead of derive it
        if (swaggerBundleConfiguration.getUriPrefix() != null) {
            return swaggerBundleConfiguration.getUriPrefix();
        }

        final String applicationContextPath = getApplicationContextPath();
        final String rootPath = getJerseyRootPath();

        final String urlPattern;
        if ("/".equals(rootPath) && "/".equals(applicationContextPath)) {
            urlPattern = "/";
        } else if ("/".equals(rootPath)
                && !"/".equals(applicationContextPath)) {
            urlPattern = applicationContextPath;
        } else if (!"/".equals(rootPath)
                && "/".equals(applicationContextPath)) {
            urlPattern = rootPath;
        } else {
            urlPattern = applicationContextPath + rootPath;
        }

        return urlPattern;
    }

    /**
     * Return the full path to where the swagger UI should be located
     * from the point of view of the client.
     *
     * This is the path that the client will use to access swagger.json,
     * /swagger-static, and so on.
     *
     * Examples:
     *  Example 1 - nothing set:
     *      AppContext = (unset)
     *      uriPrefix = (unset)
     *      view = (unset)
     *  Result: ""
     *
     *  Example 2 - appContext is /
     *      AppContext = /
     *      uriPrefix = (unset)
     *      view = (unset)
     *  Result: ""
     *
     * Example 3 - appContext is set
     *      AppContext = /app
     *      uriPrefix = (unset)
     *      view = (unset)
     * Result: "/app"
     *
     * Example 4 - uriPrefix is /api
     *      AppContext = /
     *      uriPrefix = /api
     *      view = (unset)
     * Result: "/api"
     */
    public String getSwaggerClientPath() {
        String appContext = trimPath(getApplicationContextPath());
        String jerseyRoot = trimPath(getJerseyRootPath());
        String viewUri = trimPath(swaggerBundleConfiguration.getViewUriRootPath());

        // if jerseyRoot and appContext are both / then just use /
        // else if jersey == / and appContext is something else, use appContext
        // else if jersey != / and appContext == /, use jersey
        // if both are set, combine them
        String root;
        if ("/".equals(jerseyRoot) && "/".equals(appContext)) {
            root = "/";
        } else if ("/".equals(jerseyRoot)) {
            root = appContext;
        } else if ("/".equals(appContext)) {
            root = jerseyRoot;
        } else {
            root = appContext + jerseyRoot;
        }
        String x = root + viewUri;
        return x;
    }
    /**
     * Locate the path to where swagger-static asset bundle is loaded,
     * this is relative to application context as it is a servlet.
     * (not the point of view of the client)
     * TODO examples
     * /swagger-static which could map to /appcontext/swagger-static
     * /docs/swagger-static which maps to /appcontext/docs/swagger-static
     *
     * @return path to /swagger-static from the point of view of the application
     *
     */
    public String getSwaggerAssetBundleServletPath() {
        final String jerseyRootPath = getJerseyRootPath();
        final String uriPathPrefix = trimPath(jerseyRootPath);
        String x = uriPathPrefix + swaggerBundleConfiguration.getViewUriRootPath() + "/swagger-static";
        return x;
    }

    /**
     * Locate the full path where the swagger-static assets should be found.
     * This path needs to consider application context, servlet-paths do not.
     *
     * @return path to /swagger-static from the point of view of the client browser
     */
    public String getSwaggerStaticUriPath() {
        return getSwaggerClientPath() + "/swagger-static";
    }

    /**
     * Locate the path where swagger UI should be found from the point of view of the application.
     */
    public String getSwaggerUriPath() {
        final String jerseyRootPath = getJerseyRootPath();
        final String uriPathPrefix = jerseyRootPath.equals("/") ? "" : jerseyRootPath;
        String x =  uriPathPrefix + swaggerBundleConfiguration.getViewUriRootPath();
        return x;
    }

    /**
     * Directory where swagger.json can be found
     */
    public String getSwaggerJsonUriPath() {
        return swaggerBundleConfiguration.getViewUriRootPath();
    }

    /**
     * Directory where swagger UI can be found
     * @return
     */
    public String getSwaggerUIUriPath() {
        return swaggerBundleConfiguration.getViewUriRootPath();
    }

    private String trimPath(String path) {
        if (path == null) {
            return null;
        }
        return path.equals("/") ? "" : path;
    }
    /**
     * Get the Application Context Path from the current configuration.
     * For example from .yaml:
     * <pre>
     *      server:
     *          applicationContextPath: /app
     * </pre>
     * would return "/app"
     *
     * @return applicationContext path
     */
    private String getApplicationContextPath() {
        final ServerFactory serverFactory = configuration.getServerFactory();

        final String applicationContextPath;
        if (serverFactory instanceof SimpleServerFactory) {
            applicationContextPath = ((SimpleServerFactory) serverFactory)
                    .getApplicationContextPath();
        } else {
            applicationContextPath = ((DefaultServerFactory) serverFactory)
                    .getApplicationContextPath();
        }

        return stripUrlSlashes(applicationContextPath);
    }

    private String stripUrlSlashes(String urlToStrip) {
        if (urlToStrip.endsWith("/*")) {
            urlToStrip = urlToStrip.substring(0, urlToStrip.length() - 1);
        }

        if (urlToStrip.length() > 1 && urlToStrip.endsWith("/")) {
            urlToStrip = urlToStrip.substring(0, urlToStrip.length() - 1);
        }

        return urlToStrip;
    }
}
