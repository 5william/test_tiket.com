package com.example.williamtestiket.Activity

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.williamtestiket.Component.BaseAPI
import com.example.williamtestiket.Model.GithubUserModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MainPresenter(var view: MainContract.View) : MainContract.Presenter {
    private lateinit var activity: Activity
    private lateinit var adapter: UserAdapter
    private var userModels = ArrayList<GithubUserModel>()
    private var page = 1
    private var perPage = 10
    private var totalCount = 0
    private var isLoading = false
    private lateinit var searchObservable: Observable<String>
    private lateinit var subject: PublishSubject<ArrayList<GithubUserModel>>
    private var query = ""

    override fun start(activity: Activity, searchObservable: Observable<String>) {
        this.activity = activity
        adapter = UserAdapter(activity, userModels)
        view.setAdapter(adapter)
        this.searchObservable = searchObservable
        subject = PublishSubject.create<ArrayList<GithubUserModel>>()
        searchObservable.debounce(700, TimeUnit.MILLISECONDS)
            .filter { it.length >= 3 }
            .observeOn(AndroidSchedulers.mainThread())
            .switchMap{
                view.hideShowProgressLoading(true)
                searchGithubUsers(it)
            }.subscribeOn(Schedulers.io())
            .doOnError{
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                view.hideShowProgressLoading(false)
            }
            .subscribe {
                view.hideShowEmptyState(it.isEmpty())
                if (it.isNotEmpty()) {
                    userModels.addAll(it)
                    adapter.notifyDataSetChanged()
                }
                view.hideShowProgressLoading(false)
            }
    }


    fun searchGithubUsers(query: String): Observable<ArrayList<GithubUserModel>> {
        if(!isLoading) {
            this.query = query
            page = 1
            isLoading = true
            var githubUsers = ArrayList<GithubUserModel>()
            var url = "https://api.github.com/search/users?q=$query&per_page=$perPage&page=$page"
            BaseAPI.baseRequest(BaseAPI.Method.GET, activity, url, JSONObject(), object : BaseAPI.ServerListener{
                override fun onSuccess(response: JSONObject) {
                    userModels.clear()
                    isLoading = false
                    val jsonUsers = response.getJSONArray("items")
                    totalCount = response.getInt("total_count")
                    for(i in 0 until jsonUsers.length()){
                        val objUser = jsonUsers.getJSONObject(i)
                        var githubUserModel = GithubUserModel(objUser.getString("login"), objUser.getString("avatar_url"))
                        githubUsers.add(githubUserModel)
                    }
                    subject.onNext(githubUsers)
                }

                override fun onFailed(errorCode: Int, message: String) {
                    isLoading = false
                    view.showToast(message)
                }

            })
            page++
        }
        return subject
    }

    override fun nextPageGithubUsers() {
        if(!isLoading && userModels.size<totalCount) { // To not load if is currently loading & if it is last page
            Log.d("MainPresenter", "searchGithubUsers: start search for $query")
            adapter.setLoading(true)
            isLoading = true
            var githubUsers = ArrayList<GithubUserModel>()
            var url = "https://api.github.com/search/users?q=$query&per_page=$perPage&page=$page"
            BaseAPI.baseRequest(BaseAPI.Method.GET, activity, url, JSONObject(), object : BaseAPI.ServerListener{
                override fun onSuccess(response: JSONObject) {
                    adapter.setLoading(false)
                    isLoading = false
                    val jsonUsers = response.getJSONArray("items")
                    totalCount = response.getInt("total_count")
                    for(i in 0 until jsonUsers.length()){
                        val objUser = jsonUsers.getJSONObject(i)
                        var githubUserModel = GithubUserModel(objUser.getString("login"), objUser.getString("avatar_url"))
                        githubUsers.add(githubUserModel)
                    }
                    userModels.addAll(githubUsers)
                    adapter.notifyDataSetChanged()
                }

                override fun onFailed(errorCode: Int, message: String) {
                    adapter.setLoading(false)
                    isLoading = false
                    view.showToast(message)
                }

            })
            page++
        }
    }
}