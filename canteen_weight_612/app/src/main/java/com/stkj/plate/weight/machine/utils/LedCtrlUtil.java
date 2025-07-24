package com.stkj.plate.weight.machine.utils;

import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_ALARM;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_BLUE_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_GB_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_GREEN_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_RB_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_RED_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_RG_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_WHITE_TYPE;

import android.util.Log;

import com.stkj.plate.weight.MainApplication;
import com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LedCtrlUtil {

    private final String TAG = "LedCtrlUtil";

    private static LedCtrlUtil instance;


    private boolean isStart = false;

    public static LedCtrlUtil getInstance(){
        if(instance == null){
            synchronized (LedCtrlUtil.class){
                if(instance == null){
                    instance = new LedCtrlUtil();
                }
            }
        }
        return instance;
    };

    private LedCtrlUtil() {

    }


    public void updateApp(String appPath, String appPackName, String appClassName){
        LedCtrlApi.getInstance().updateApp(MainApplication.instances, appPath, appPackName, appClassName);
    }

    public void addAppToGuard(String packageName, String mainActivityName){

        LedCtrlApi.getInstance().addAppToGuard(packageName, mainActivityName);
    }

    private Disposable ledDisposable;
    private Disposable ledOpenDisposable;
    private Disposable ledCloseDisposable;

    public void startGlintLed(final int type){

        ledDisposable = Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
//                Log.e(TAG, "Thread name"+ Thread.currentThread().getName());
                isStart = true;
                LedCtrlApi.getInstance().openAlarm();
                while (isStart){
                    switch (type){
                        case LED_RED_TYPE:
                            LedCtrlApi.getInstance().openRedLed();
                            break;
                        case LED_GREEN_TYPE:
                            LedCtrlApi.getInstance().openGreenLed();
                            break;
                        case LED_BLUE_TYPE:
                            LedCtrlApi.getInstance().openBlueLed();
                            break;
                        case LED_WHITE_TYPE:
                            LedCtrlApi.getInstance().openWhiteLed();
                            break;
                    }

                    Thread.sleep(500);

                    LedCtrlApi.getInstance().closeLed();

                    Thread.sleep(500);

                }

                emitter.onNext(true);
                emitter.onComplete();



            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {

                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   isStart = false;
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if(ledDisposable != null && !ledDisposable.isDisposed()){
                                    ledDisposable.dispose();
                                }
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                if(ledDisposable != null && !ledDisposable.isDisposed()){
                                    ledDisposable.dispose();
                                }
                            }
                        });

    }

    public void stopGlintLed() {

        isStart = false;
        if (ledDisposable != null && !ledDisposable.isDisposed()) {
            ledDisposable.dispose();
        }

        closeLedThread();

    }

    public void closeLedThread(){
        ledCloseDisposable = Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                LedCtrlApi.getInstance().closeAlarm();
                LedCtrlApi.getInstance().closeLed();
                emitter.onNext(true);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {

                               @Override
                               public void accept(Boolean aBoolean) throws Exception {

                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if(ledCloseDisposable != null && !ledCloseDisposable.isDisposed()){
                                    ledCloseDisposable.dispose();
                                    ledCloseDisposable = null;
                                }

                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                if(ledCloseDisposable != null && !ledCloseDisposable.isDisposed()){
                                    ledCloseDisposable.dispose();
                                    ledCloseDisposable = null;
                                }
                            }
                        });
    }


    public void openLed(final int ledType){

        ledOpenDisposable = Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                Log.d(TAG, "limeopenLed 192 ledType: " + ledType);
                switch (ledType){
                    case LED_RED_TYPE:
                        LedCtrlApi.getInstance().openRedLed();
                        break;
                    case LED_GREEN_TYPE:
                        LedCtrlApi.getInstance().openGreenLed();
                        break;
                    case LED_BLUE_TYPE:
                        LedCtrlApi.getInstance().openBlueLed();
                        break;
                    case LED_WHITE_TYPE:
                        LedCtrlApi.getInstance().openWhiteLed();
                        break;
                    case  LED_RG_TYPE:
                        LedCtrlApi.getInstance().openRGLed();
                        break;
                    case LED_GB_TYPE:
                        LedCtrlApi.getInstance().openGBLed();
                        break;
                    case LED_RB_TYPE:
                        LedCtrlApi.getInstance().openRBLed();
                        break;
                    case LED_ALARM:
                        LedCtrlApi.getInstance().openAlarm();
                        break;
                }
                emitter.onNext(true);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {

                               @Override
                               public void accept(Boolean aBoolean) throws Exception {

                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if(ledOpenDisposable != null && !ledOpenDisposable.isDisposed()){
                                    ledOpenDisposable.dispose();
                                    ledOpenDisposable = null;
                                }

                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                if(ledOpenDisposable != null && !ledOpenDisposable.isDisposed()){
                                    ledOpenDisposable.dispose();
                                    ledOpenDisposable = null;
                                }
                            }
                        });
    }

    public String getLedName(int ledType){
        return LedCtrlApi.getInstance().getLedName(ledType);
    }

    public boolean isStart() {
        return isStart;
    }


  public void reboot(){
      LedCtrlApi.getInstance().reboot();
  }

    public void sendAdbCmd(String cmd){
        LedCtrlApi.getInstance().sendAdbCmd(cmd);

    }

  public void updataServiceTime(String date){
      LedCtrlApi.getInstance().updataServiceTime(MainApplication.instances, date);
  }

}
