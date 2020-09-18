package com.example.williamtestiket.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.williamtestiket.Component.EndlessRecyclerViewScrollListener
import com.example.williamtestiket.R
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainContract.View {
    lateinit var mainPresenter: MainPresenter
    lateinit var subject: PublishSubject<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

    }


    fun init() {
        mainPresenter = MainPresenter(this)
        mainPresenter.start(this, createObservableSearch(et_search))
        val mLayoutManager = LinearLayoutManager(this@MainActivity)
        listUsers.layoutManager = mLayoutManager
        listUsers.itemAnimator = DefaultItemAnimator()
        listUsers.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(mLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                mainPresenter.nextPageGithubUsers()
            }
        }
        )
    }

    fun createObservableSearch(et_search : EditText) : Observable<String> {
        subject = PublishSubject.create<String>()
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                subject.onNext(s.toString())
            }
        }
        et_search.addTextChangedListener(textWatcher)
        return subject
    }

    override fun setAdapter(userAdapter: UserAdapter) {
        listUsers.adapter = userAdapter
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun hideShowEmptyState(isShow: Boolean) {
        if (isShow) {
            tv_no_data_found.visibility = View.VISIBLE
            listUsers.visibility = View.GONE
        } else {
            tv_no_data_found.visibility = View.GONE
            listUsers.visibility = View.VISIBLE
        }
    }

    override fun hideShowProgressLoading(isShow: Boolean) {
        if (isShow) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

}