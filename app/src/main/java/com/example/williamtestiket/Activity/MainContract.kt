package com.example.williamtestiket.Activity

import android.app.Activity
import com.example.williamtestiket.Activity.UserAdapter
import io.reactivex.rxjava3.core.Observable

interface MainContract {

    interface View {
        fun setAdapter(userAdapter: UserAdapter)
        fun showToast(message: String)
        fun hideShowEmptyState(isShow: Boolean)
        fun hideShowProgressLoading(isShow: Boolean)
    }

    interface Presenter {
        fun start(activity: Activity, searchObservable: Observable<String>)
        fun nextPageGithubUsers()
    }
}