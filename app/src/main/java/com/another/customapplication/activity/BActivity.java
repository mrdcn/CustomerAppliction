package com.another.customapplication.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.another.customapplication.R;
import com.another.customapplication.view.DrawerLayout;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BActivity<T> extends MainActivity {

    DrawerLayout dragView;
    String ssid;
    ArrayList<Disposable> mDisposables = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        discriptor = "-----B";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        dragView = findViewById(R.id.drag);

        findViewById(R.id.open).setOnClickListener(v -> {
            dragView.showContnet();
        });

        findViewById(R.id.close).setOnClickListener(v -> {
            dragView.hideContent();
        });

        findViewById(R.id.bac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
//
//                ssid = manager.getConnectionInfo().getSSID();
//
//                Log.e("bac",ssid);
                dragView.showContnet();
            }
        });

        test();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(Disposable disposable : mDisposables){
            disposable.dispose();
        }
    }

    public static <T> ObservableTransformer<T,T> io_main(){
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return  upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    Disposable timer(){
        return Observable.timer(3, TimeUnit.SECONDS).compose(io_main()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Thread.sleep(2);
                Log.e("next timer","  ");
            }
    });
    }

    public void test(){

        Disposable intervalDisposable = Observable.interval(5,TimeUnit.SECONDS).compose(io_main()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                mDisposables.add(timer());
            }
        });

        mDisposables.add(intervalDisposable);

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("a");
                Thread.sleep(200);
                emitter.onNext("b");
                Thread.sleep(200);
                emitter.onNext("c");
                Thread.sleep(200);
                emitter.onNext("d");
                Log.e("rxjava_scheduler_" ,Thread.currentThread().getName());
            }
        });



        Observer<String> observer  = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                //  SubscribeOnObserver  implements Disposable
                Log.e("Rx--> OnSubscr", d.toString());
            }

            @Override
            public void onNext(String s) {



                Log.e("rxjava_scheduler_" + System.currentTimeMillis(),Thread.currentThread().getName() + "\t" + s);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public String toString() {
                return getClass().getSimpleName() + " -- > CustomObserver";
            }
        };
        observable.map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return 0;
            }
        })

       .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        });


    }




}
