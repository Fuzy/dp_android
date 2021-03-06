package imis.client.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.asynctasks.result.Result;
import imis.client.ui.fragments.TaskFragment;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Generic class for asynchronous request to server.
 * @param <T>  the type of the parameters sent to the task upon execution.
 * @param <U>  the type of the progress units published during the background computation.
 * @param <V>  the type of the result of the background computation.
 */
public abstract class NetworkingAsyncTask<T, U, V> extends AsyncTask<T, U, V> implements Serializable {
    private static final String TAG = NetworkingAsyncTask.class.getSimpleName();
    protected TaskFragment mFragment;
    protected T[] params;
    protected final Context context;

    @SuppressWarnings({"unchecked", "varargs"})
    protected NetworkingAsyncTask(Context context, T... params) {
        this.context = context;
        this.params = params;
    }

    public void setFragment(TaskFragment fragment) {
        mFragment = fragment;
    }

    public void execute() {
        Log.d(TAG, "execute() params: " + Arrays.toString(params));
        this.execute(params);
    }

    @Override
    protected void onPostExecute(V v) {
        Result result = (Result) v;
        Log.d(TAG, "onPostExecute() result " + result);

        if (result.isOk()) {
            if (result.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                AppUtil.showInfo(context, context.getString(R.string.no_records));
            }
        } else if (result.isUnknownErr()) {
            AppUtil.showError(context, result.getMsg());
        } else if (result.isClientError()) {
            if (result.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                AppUtil.showError(context, context.getString(R.string.unauthorized));
            }
        }

        if (mFragment != null) {
            mFragment.taskFinished((Result) v);
        }

        super.onPostExecute(null);
    }


}
