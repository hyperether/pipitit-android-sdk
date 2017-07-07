package com.hyperether.pipitit;

import android.app.Application;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.schedulers.Schedulers;

/**
 * Class for managing application related behavior
 *
 * @author Nebojsa Brankovic
 * @author Slobodan Prijic
 * @version 1.0 - 7/7/2017
 */
public class PipititApp extends Application {

    private final Relay<Object> _bus = PublishRelay.create().toSerialized();

    private static PipititApp instance;

    public static synchronized PipititApp getInstance() {
        if (instance == null) {
            instance = new PipititApp();
        }
        return instance;
    }

    public void send(Object o) {
        if (hasObservers())
            _bus.accept(o);
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }

    public Relay<Object> get_bus() {
        return _bus;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        _bus.unsubscribeOn(Schedulers.single());
    }
}
