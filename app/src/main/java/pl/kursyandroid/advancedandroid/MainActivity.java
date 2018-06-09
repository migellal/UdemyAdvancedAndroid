package pl.kursyandroid.advancedandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "REACTIVE";
    @BindView(R.id.sourceEditText)
    EditText sourceText;
    @BindView(R.id.infoTextView1)
    TextView info1;
    @BindView(R.id.infoTextView2)
    TextView info2;
    @BindView(R.id.infoTextView3)
    TextView info3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        simpleReactive();
        transformingOperators();
        filteringOperators();
        combiningOperators();
        advancedReactive();

        RxTextView.afterTextChangeEvents(sourceText)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tvChangeEvent ->
                                info1.setText("1= " + tvChangeEvent.view().getText())
                );

        RxTextView.afterTextChangeEvents(sourceText)
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tvChangeEvent ->
                                info2.setText("2= " + tvChangeEvent.view().getText())
                );

        RxTextView.afterTextChangeEvents(sourceText)
                .filter(v -> v.view().getText().length() % 10 == 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tvChangeEvent ->
                                info3.setText("3= " + tvChangeEvent.view().getText())
                );
    }

    private void advancedReactive() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        Observable.fromIterable(list)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> Log.d(TAG, x)
                );
    }

    private void simpleReactive() {
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

    private void combiningOperators() {
        Observable<Integer> observable = Observable.range(1, 10);
        Observable<Integer> observable2 = Observable.range(31, 10);

        Observable.merge(observable, observable2).subscribe(
                x -> Log.d(TAG, "" + x)
        );

        List<Observable<Integer>> list = new ArrayList<>();
        list.add(observable);
        list.add(observable2);
        Observable<Integer> obs = Observable.zip(list, t -> {
            Integer result = 0;
            for (Object o : t) {
                result += (Integer) o;
            }
            return result;
        });
        obs.subscribe(r -> Log.d(TAG, "merge: " + r));

        observable.startWith(-1).subscribe(
                r -> Log.d(TAG, "" + r)
        );
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
