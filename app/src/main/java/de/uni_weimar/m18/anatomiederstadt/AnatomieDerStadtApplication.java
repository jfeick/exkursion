package de.uni_weimar.m18.anatomiederstadt;

import android.app.Application;

import de.uni_weimar.m18.anatomiederstadt.util.LevelStateManager;

import com.baasbox.android.BaasBox;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/*
 * Copyright 2015. J.F.Eick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *   limitations under the License.
 */
public class AnatomieDerStadtApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        BaasBox.builder(this)
                .setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain(getString(R.string.api_domain))
                .setUseHttps(true)
                .setPort(getResources().getInteger(R.integer.api_port))
                .setAppCode(getString(R.string.app_code))
                .init();

    }

    private LevelStateManager levelStateManager = new LevelStateManager();

    public LevelStateManager getStateManager() {
        return levelStateManager;
    }
}
