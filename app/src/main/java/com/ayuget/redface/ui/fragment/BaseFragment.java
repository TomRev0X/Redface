/*
 * Copyright 2015 Ayuget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ayuget.redface.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import com.ayuget.redface.ui.misc.ThemeManager;
import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseFragment extends DaggerFragment {
    @Inject
    Bus bus;

    @Inject
    ThemeManager themeManager;

    private CompositeSubscription subscriptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read fragment args from bundle
        FragmentArgs.inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Proper RxJava subscriptions management with CompositeSubscription
        subscriptions = new CompositeSubscription();

        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        subscriptions.unsubscribe();

        super.onPause();
    }

    protected void subscribe(Subscription s) {
        subscriptions.add(s);
    }

    /**
     * Inflates fragment root view and deals with view injections through ButterKnife
     * <p>
     * Also applies app custom theme
     */
    protected ViewGroup inflateRootView(@LayoutRes int viewResId, LayoutInflater inflater, ViewGroup container) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), themeManager.getActiveThemeStyle());
        LayoutInflater themedInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup rootView = (ViewGroup) themedInflater.inflate(viewResId, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
