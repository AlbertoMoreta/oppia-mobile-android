package org.digitalcampus.oppia.task

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import okhttp3.Request
import okhttp3.Request.Builder
import org.digitalcampus.mobile.learning.R
import org.digitalcampus.oppia.api.ApiEndpoint
import org.digitalcampus.oppia.api.RemoteApiEndpoint
import org.digitalcampus.oppia.application.SessionManager.getUsername
import org.digitalcampus.oppia.application.SessionManager.invalidateCurrentUserApiKey
import org.digitalcampus.oppia.database.DbHelper
import org.digitalcampus.oppia.exception.UserNotFoundException
import org.digitalcampus.oppia.listener.APIRequestFinishListener
import org.digitalcampus.oppia.task.APIRequestTaskKotlin
import org.digitalcampus.oppia.task.result.BasicResult
import org.digitalcampus.oppia.utils.HTTPClientUtils.getUrlWithCredentials

abstract class APIRequestTaskKotlin(protected var ctx: Context, var apiEndpoint: ApiEndpoint = RemoteApiEndpoint) {

    @JvmField
    val TAG = APIRequestTaskKotlin::class.simpleName

    private var listener: APIRequestFinishListener? = null
    @JvmField
    protected var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)

    private var nameRequest: String? = null

    fun createRequestBuilderWithUserAuth(url: String): Builder {
        val db = DbHelper.getInstance(ctx)
        var requestBuilder: Builder? = null
        try {
            val u = db.getUser(getUsername(ctx))
            Log.i(TAG, "user api key: " + u.apiKey)
            requestBuilder = Builder().url(getUrlWithCredentials(url, u.username, u.apiKey))
        } catch (e: UserNotFoundException) {
            Log.d(TAG, "User not found: ", e)
        }
        if (requestBuilder == null) {
            //the user was not found, we create it without the header
            requestBuilder = Builder().url(url)
        }
        return requestBuilder
    }

    protected fun onSuccess(result: BasicResult) {
        listener?.onRequestFinish(nameRequest)
    }

    protected fun invalidateApiKey(result: BasicResult) {
        // If the server returns a 401 error it means an invalid APIkey
        invalidateCurrentUserApiKey(ctx)
        result.isSuccess = false
        result.resultMessage =
            ctx.getString(R.string.error_apikey_expired)
    }

    fun setAPIRequestFinishListener(listener: APIRequestFinishListener?, nameRequest: String?) {
        this.nameRequest = nameRequest
        this.listener = listener
    }

}