package com.another.customapplication.activity;

import android.os.Bundle;
import android.os.Trace;
import android.util.Log;

import com.another.customapplication.R;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        discriptor = "-----B";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        test();

    }

    public void test(){
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
