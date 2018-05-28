package pl.kursyandroid.advancedandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.GroupedObservable;


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

        transformingOperators();
        filteringOperators();
    }

    private void filteringOperators() {
        Observable.just(1, 2, 3).first(0).subscribe(
                v -> Log.d(TAG, "" + v)
        );

        Observable<Integer> observable = Observable.range(1, 10);
        observable.filter(x -> x > 5).subscribe(
                x -> Log.d(TAG, "" + x)
        );

        Observable.just(1, 1, 1, 2, 2, 2, 2, 3, 3).distinct().subscribe(
                x -> Log.d(TAG, "" + x));
    }

    private void transformingOperators() {
        String even = "even";
        Observable<Integer> observable = Observable.range(1, 10);

        Observable<Integer> values = observable.map(x -> (x * 5) - 4);
        values.subscribe(
                v -> Log.d(TAG, String.valueOf(v)));

        Observable<GroupedObservable<String, Integer>> groupedObservable = observable.groupBy(t -> {
                    if (t % 2 == 0) {
                        return even;
                    } else {
                        return "odd";
                    }
                }
        );
        groupedObservable.subscribe(s -> {
            Log.d(TAG, "key: " + s.getKey());
            if (even.equals(s.getKey())) {
                s.subscribe(g -> {
                    Log.d(TAG, "item: " + g);
                });
            }
        });

        observable.buffer(2).subscribe(
                val -> Log.d(TAG, val.toString()));
    }
}
