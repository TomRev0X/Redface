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

package com.ayuget.redface.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ayuget.redface.data.api.model.Response;
import com.ayuget.redface.data.api.model.User;
import com.ayuget.redface.data.rx.EndlessObserver;
import com.ayuget.redface.data.rx.SubscriptionHandler;

public class EditPostActivity extends ReplyActivity {
    private static final String LOG_TAG = EditPostActivity.class.getSimpleName();

    private SubscriptionHandler<User, Response> editSubscriptionHandler = new SubscriptionHandler<>();

    private int editedPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            editedPostId = intent.getIntExtra(UIConstants.ARG_EDITED_POST_ID, 0);
        }
    }

    @Override
    protected boolean canSwitchUser() {
        return false;
    }

    @Override
    protected void postReply() {
        User activeUser = userManager.getActiveUser();
        String message = replyEditText.getText().toString();

        subscribe(editSubscriptionHandler.load(activeUser, mdService.editPost(activeUser, getCurrentTopic(), editedPostId, message, true), new EndlessObserver<Response>() {
            @Override
            public void onNext(Response response) {
                if (response.isSuccessful()) {
                    onReplySuccess();
                } else {
                    onReplyFailure();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(LOG_TAG, "Unknown exception while editing post", throwable);
                onReplyFailure();
            }
        }));
    }

    protected void onReplySuccess() {
        clearResponseFromCache(userManager.getActiveUser());

        // Flag that reply is successful to prevent it to be cached in the
        // response cache (onPause happens later in this activity lifecycle)
        setReplySuccessful(true);

        replyToActivity(RESULT_OK, true);
    }

    protected void onReplyFailure() {
        replyToActivity(UIConstants.REPLY_RESULT_KO, true);
    }
}
