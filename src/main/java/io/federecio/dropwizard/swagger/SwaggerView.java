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

import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import io.dropwizard.views.View;

/**
 * Serves the content of Swagger's index page which has been "templatized" to
 * support replacing the directory in which Swagger's static content is located
 * (i.e. JS files) and the path with which requests to resources need to be
 * prefixed.
 */
public class SwaggerView extends View {

    private final String swaggerAssetsPath;
    private final String swaggerJsonPath;

    private final SwaggerViewConfiguration viewConfiguration;

    public SwaggerView(@Nonnull final String contextRoot,
                       @Nonnull final String urlPattern,
                       @Nonnull SwaggerViewConfiguration config,
                       @Nonnull final String swaggerAssetPath,
                       @Nonnull final String swaggerJsonPath) {
        super(config.getTemplateUrl(), StandardCharsets.UTF_8);


        this.swaggerAssetsPath = swaggerAssetPath;
        this.swaggerJsonPath = swaggerJsonPath;

        this.viewConfiguration = config;
    }

    /**
     * Returns the title for the browser header
     */
    public String getTitle() {
        return viewConfiguration.getPageTitle();
    }

    /**
     * Returns the path with which all requests for Swagger's static content
     * need to be prefixed
     */
    public String getSwaggerAssetsPath() {
        return swaggerAssetsPath;
    }

    /**
     * Return the path to the location of the swagger.json file
     */
    public String getSwaggerJsonPath() {
        return swaggerJsonPath;
    }


    /**
     * Returns the location of the validator URL or null to disable
     */
    public String getValidatorUrl() {
        return viewConfiguration.getValidatorUrl();
    }

    /**
     * Returns whether to display the authorization input boxes
     */
    public boolean getShowAuth() {
        return viewConfiguration.isShowAuth();
    }

    /**
     * Returns whether to display the swagger spec selector
     */
    public boolean getShowApiSelector() {
        return viewConfiguration.isShowApiSelector();
    }
}
