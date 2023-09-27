/*
 * This file is part of OppiaMobile - https://digital-campus.org/
 *
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */
package org.digitalcampus.oppia.task

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.digitalcampus.mobile.learning.R
import org.digitalcampus.oppia.api.ApiEndpoint
import org.digitalcampus.oppia.api.RemoteApiEndpoint
import org.digitalcampus.oppia.listener.APIRequestListener
import org.digitalcampus.oppia.task.result.BasicResult
import org.digitalcampus.oppia.utils.HTTPClientUtils.getClient
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class APIUserRequestTask(ctx: Context, apiEndpoint: ApiEndpoint) : APIRequestTaskKotlin(ctx, apiEndpoint) {

    @JvmField
    protected var apiKeyInvalidated = false
    private var requestListener: APIRequestListener? = null

    private val uiScope = CoroutineScope(Dispatchers.Main)

    fun execute(url: String) {
        uiScope.launch {
            val result = withContext(Dispatchers.IO) {
                doInBackground(url)
            }

            onPostExecute(result)
        }
    }

    fun doInBackground(url: String): BasicResult{
        val now = System.currentTimeMillis()
        val result = BasicResult()

        try {
            val client = getClient(ctx)
            val request = createRequestBuilderWithUserAuth(apiEndpoint.getFullURL(ctx, url)).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                result.isSuccess = true
                result.resultMessage = response.body?.string()
            } else {
                return onFailure(response.code)
            }
        } catch (e: IOException) {
            Log.d(TAG, "IO exception", e)
            return onFailure()
        }
        val spent = System.currentTimeMillis() - now
        Log.d(TAG, "Spent $spent ms")
        return result
    }

    fun onPostExecute(result: BasicResult) {
        if (apiKeyInvalidated) {
            requestListener?.apiKeyInvalidated()
        } else {
            val message = result.resultMessage
            try {
                val json = JSONObject(message)
                if (json.has("message")) {
                    result.resultMessage = json.getString("message")
                }
            } catch (e: JSONException) {
                Log.d(TAG, e.message ?: "")
            }
            requestListener?.apiRequestComplete(result)
        }

        super.onSuccess(result)
    }

    private fun onFailure(code: Int? = null): BasicResult {
        val result: BasicResult
        when(code) {
            401 -> {
                result = BasicResult(false, ctx.getString(R.string.error_apikey_expired))
                invalidateApiKey(result)
                apiKeyInvalidated = true
            }
            403 -> result = BasicResult(false, ctx.getString(R.string.error_login))
            else -> result = BasicResult(false, ctx.getString(R.string.error_connection))
        }
        return result
    }

    fun setAPIRequestListener(srl: APIRequestListener?) {
        synchronized(this) { requestListener = srl }
    }
}