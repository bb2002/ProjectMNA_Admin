package kr.saintdev.pmnadmin.models.tasks;

/**
 * Created by yuuki on 18. 4. 21.
 */

public interface OnBackgroundWorkListener {
    void onSuccess(int requestCode, BackgroundWork worker);
    void onFailed(int requestCode, Exception ex);
}
