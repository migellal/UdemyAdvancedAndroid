package pl.kursyandroid.advancedandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "REACTIVE_JUST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable.just("a", "b", "c", "d", "e")
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "subscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "next: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "complete");
                    }
                });

        Observable.just("a", "b", "c", "d", "e").subscribe(
                val -> Log.d(TAG, val),
                error -> Log.e(TAG, error.getMessage()),
                () -> Log.d(TAG, "complete"),
                d -> Log.d(TAG, "subscribe")
        );
    }
}
