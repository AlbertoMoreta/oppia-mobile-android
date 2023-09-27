package org.digitalcampus.oppia.task

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import okhttp3.Response
import org.digitalcampus.mobile.learning.R
import org.digitalcampus.oppia.api.ApiEndpoint
import org.digitalcampus.oppia.api.Paths
import org.digitalcampus.oppia.api.RemoteApiEndpoint
import org.digitalcampus.oppia.application.SessionManager.getUsername
import org.digitalcampus.oppia.database.DbHelper
import org.digitalcampus.oppia.task.result.BasicResult
import org.digitalcampus.oppia.utils.HTTPClientUtils
import org.digitalcampus.oppia.utils.HTTPClientUtils.getClient
import org.digitalcampus.oppia.utils.TextUtilsJava
import java.io.IOException

class DeleteAccountTask(ctx: Context, apiEndpoint: ApiEndpoint = RemoteApiEndpoint) : APIUserRequestTask(ctx, apiEndpoint) {
    private var responseListener: ResponseListener? = null

    constructor(context: Context) : this(context, RemoteApiEndpoint) {
        // TODO Kotlin: Remove this constructor when every reference to this class is migrated to Kotlin
    }

    interface ResponseListener {
        fun onSuccess()
        fun onError(error: String?)
    }

    override fun execute(password: String) {
        uiScope.launch {
            val result = withContext(Dispatchers.IO) {
                doInBackground(password)
            }

            onPostExecute(result)
        }
    }

    private fun doInBackground(password: String): BasicResult {
        val passwordJSON = "{\"password\":\"$password\"}"
        val result = BasicResult()
        val client = getClient(ctx)
        val request =
            createRequestBuilderWithUserAuth(apiEndpoint.getFullURL(ctx, Paths.DELETE_ACCOUNT_PATH))
                .post(RequestBody.create(HTTPClientUtils.MEDIA_TYPE_JSON, passwordJSON)).build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                result.isSuccess = true
                result.resultMessage = response.body?.string()
                val currentUser = getUsername(ctx)
                DbHelper.getInstance(ctx).deleteUser(currentUser)
            } else {
                return onFailure(response)
            }
        } catch (e: IOException) {
            Log.d(TAG, "IO exception", e)
            return onFailure()
        }
        return result
    }

    private fun onPostExecute(result: BasicResult) {
        synchronized(this) {
            if (responseListener != null) {
                if (result.isSuccess) {
                    responseListener?.onSuccess()
                } else {
                    responseListener?.onError(result.resultMessage)
                }
            }
        }
    }

    private fun onFailure(response: Response? = null): BasicResult {
        val result = BasicResult(false)
        when(response?.code) {
            401 -> {
                invalidateApiKey(result)
                apiKeyInvalidated = true
            }

            400, 403 -> {
                val message = response.body!!.string()
                if (TextUtilsJava.isEmpty(message)) {
                    result.resultMessage = message
                } else {
                    result.resultMessage = ctx.getString(R.string.error_login)
                }
            }
            else -> {
                result.resultMessage = ctx.getString(R.string.error_connection)
            }
        }
        return result
    }

    fun setResponseListener(srl: ResponseListener?) {
        synchronized(this) { responseListener = srl }
    }
}