package com.indialone.rxjavamultithreadingandadvanced

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import io.reactivex.*
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observables.ConnectableObservable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import java.io.FileOutputStream
import com.jakewharton.rxbinding4.view.clicks
import com.trello.rxlifecycle4.RxLifecycle
import com.trello.rxlifecycle4.android.ActivityEvent
import com.trello.rxlifecycle4.android.RxLifecycleAndroid
import io.reactivex.disposables.Disposable
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.subjects.BehaviorSubject
import java.lang.Exception
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var button: Button

    private val lifecycleSubject: BehaviorSubject<ActivityEvent> = BehaviorSubject.create()
    private val lifecycle: io.reactivex.rxjava3.core.Observable<ActivityEvent> =
        io.reactivex.rxjava3.core.Observable.create { ActivityEvent.CREATE }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)




        // error handling in rxjava
        /*
            1- throw in an exception in observable
            2- also OutOfMemoryException()
            3- also made our own Exception class by extending Exception class

            4- we also have onErrorReturnItem(item: Any) method it return the last item after error occurs
            5- onErrorReturn() we can return the last item or you can get your own type of item
            6- onErrorResumeNext(next: ObservableSource<Any>) - it will not return the error but it will
               pass the control to the another observable for continue emitting
            7- Observable.retry() method , we can use it when process stops due to low network
               then this method provide retry functionality to continue with retry
            8- Observable.retryWhen() when multiple retry occurred and we want to retry after some time
               then it will help or when network available automatically retry the service
               - or auto retry after some time
         */

/*

        val observable: Observable<Int> = Observable.range(1, 10)
            .flatMap { value ->
                if (value == 3) Observable.error(Throwable("An Error occured"))
                else Observable.just(value)
            }
*/

        /*
        * error handling

        val observable: Observable<Int> = Observable.create { emitter ->
            emitter.onError(Throwable("An Error Occurred"))
        }
        observable.subscribe(object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(t: Int) {

            }

            override fun onError(e: Throwable) {
                Log.e(TAG, e.message.toString())
            }

            override fun onComplete() {

            }
        })
*/


        // here for lifecycleSubject we can define it in onStart(), onResume(), onDestroy(), onCreate()

        /*
            we also extends MainActivity with LifecycleActivity()
            then implement the onCreate() method

            class MainActivity : LifecycleActivity() {

                private provider : LifecycleProvider<Lifecycle.Event> =
                    AndroidLifecycle.createLifecycleProvider(this)

                override fun onCreate() {
                    super.onCreate()
                    // here myObservable is for simplicity of example

                    myObservable
                        .compose(provider.bindToLifecycle())
                        .subscribe()

                }

            }
         */
        lifecycleSubject.onNext(ActivityEvent.CREATE)
        val clicks: io.reactivex.rxjava3.core.Observable<Unit> = button.clicks()
        clicks.compose(
            RxLifecycle.bindUntilEvent(
                lifecycle,
                ActivityEvent.CREATE
            )
        ).subscribe()


        // ** RxLifecycle dependency
        // this is for auto destroy observables when activity or fragment destroys
/*
        val clicks: io.reactivex.rxjava3.core.Observable<Unit> = button.clicks()
        clicks.compose(
            RxLifecycle.bindUntilEvent(
                lifecycle,
                ActivityEvent.DESTROY
            )
        ).subscribe()

        clicks.compose(RxLifecycleAndroid.bindActivity(lifecycle)).subscribe()
*/

        /*
        ** observing the clicks using rxjava and jackworton library

        val clicks: io.reactivex.rxjava3.core.Observable<Unit> = button.clicks()

        val clickAggregate: io.reactivex.rxjava3.core.Observable<MutableList<Unit>>? =
            clicks.buffer(3000, TimeUnit.MILLISECONDS)
        clickAggregate!!.subscribe { Log.e(TAG, "single clicks: ${it.size}") }

        // this are the double clicks
        val doubleClicks = clickAggregate.filter { list -> list.size >= 2 }
//        doubleClicks.subscribe { Log.e(TAG, "double clicks: ${it.size}") }


         */


    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(ActivityEvent.START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(ActivityEvent.RESUME)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleSubject.onNext(ActivityEvent.DESTROY)
    }

    override fun onPause() {
        super.onPause()
        lifecycleSubject.onNext(ActivityEvent.PAUSE)
    }

    override fun onStop() {
        super.onStop()
        lifecycleSubject.onNext(ActivityEvent.STOP)
    }

    /*
           write text in the file
           two types for defining
           - Observable and Completable
    */

    private fun writeTextToFileWithObservable(
        context: Context,
        fileName: String,
        text: String
    ): Observable<Boolean> {
        return Observable.fromCallable {
            val outputStream: FileOutputStream = context
                .openFileOutput(fileName, Context.MODE_PRIVATE)

            outputStream.write(text.toByteArray())
            outputStream.close()
            true
        }.subscribeOn(Schedulers.io())
    }

    private fun writeTextToFileWithCompletable(
        context: Context,
        fileName: String,
        text: String
    ): Completable {
        return Completable.fromAction {
            val outputStream: FileOutputStream =
                context.openFileOutput(fileName, Context.MODE_PRIVATE)
            outputStream.write(text.toByteArray())
            outputStream.close()
        }.subscribeOn(Schedulers.io())
    }


    private fun print(message: String) {
        val threadName = Thread.currentThread().name
        Log.d(TAG, "$message : Thread Name: $threadName")
    }

}
/*
    using map operator

    **output**
     Got User: User(username=Uru) : Thread Name: RxCachedThreadScheduler-1
     D/MainActivity: Got User: User(username=Natu) : Thread Name: RxCachedThreadScheduler-1
    Got User: User(username=Hardy) : Thread Name: RxCachedThreadScheduler-1


         val usernames = listOf(
                    "Uru","Natu","Hardy"
                )

        val networkClient = NetworkClient()


        Observable
            .fromIterable(usernames)
            .subscribeOn(Schedulers.io())
            .map {
                networkClient.fetchUser(it)
            }
            .subscribe {
                print("Got User: $it")
            }
*/

/*


         val usernames = listOf(
                    "Uru","Natu","Hardy"
                )

        val networkClient = NetworkClient()

        val flatMap = Observable
            .fromIterable(usernames)
            .flatMap {
                Observable
                    .fromCallable {
                        networkClient.fetchUser(it)
                    }
                    .subscribeOn(Schedulers.io())
            }
            .subscribe {
                print("Got User: ${it.username}")
            }

        output ::
        D/MainActivity: Got User: Uru : Thread Name: RxCachedThreadScheduler-1
        D/MainActivity: Got User: Natu : Thread Name: RxCachedThreadScheduler-2

*/


/*
        ** cache() method

        val appSettingsObservable = Observable
            .fromCallable {
                Log.d(TAG, "This is the App settings")
            }
            .timestamp().cache()

        appSettingsObservable
            .subscribe { result ->
                Log.d(TAG, "this is the foreground first observer: ${result.time()}")
            }

        appSettingsObservable
            .subscribe { result ->
                Log.d(TAG, "this is cached: ${result.time()}")
            }

            ** output **
        D/MainActivity: This is the App settings
            this is the foreground first observer 1627533917206
            this is after time completed 1627533917206

 */

/*

    val observable: Observable<Int> = Observable
                .create {
                    print("In Subscribe!!")
                    it.onNext(1)
                    it.onNext(2)
                    it.onNext(3)
                    it.onComplete()
                }

            observable.subscribeOn(Schedulers.computation())
                .doOnNext { print("value1: $it") }
                .observeOn(Schedulers.newThread())
                .doOnNext { print("value2: $it") }
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())        // no impact on anything
                .doOnNext { print("value4: $it") }
                .observeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { print("value5 : $it") }

    ** output **
    D/MainActivity: In Subscribe!! : thread name: RxComputationThreadPool-1
        value1: 1 : thread name: RxComputationThreadPool-1
        value1: 2 : thread name: RxComputationThreadPool-1
    D/MainActivity: value1: 3 : thread name: RxComputationThreadPool-1
    D/MainActivity: value2: 1 : thread name: RxNewThreadScheduler-2
    D/MainActivity: value2: 2 : thread name: RxNewThreadScheduler-2
        value2: 3 : thread name: RxNewThreadScheduler-2
    D/MainActivity: value4: 1 : thread name: RxNewThreadScheduler-3
    D/MainActivity: value4: 2 : thread name: RxNewThreadScheduler-3
        value4: 3 : thread name: RxNewThreadScheduler-3
    D/MainActivity: value5 : 1 : thread name: main
        value5 : 2 : thread name: main
    D/MainActivity: value5 : 3 : thread name: main


 */

