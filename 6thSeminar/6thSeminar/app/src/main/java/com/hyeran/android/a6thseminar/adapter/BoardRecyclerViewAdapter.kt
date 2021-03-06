package com.hyeran.android.a6thseminar.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hyeran.android.a6thseminar.BoardDetailActivity
import com.hyeran.android.a6thseminar.R
import com.hyeran.android.a6thseminar.data.BoardData
import com.hyeran.android.a6thseminar.db.SharedPreferencesController
import com.hyeran.android.a6thseminar.network.ApplicationController
import com.hyeran.android.a6thseminar.network.NetworkService
import com.hyeran.android.a6thseminar.post.PostLikeResponse
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
기본적인 Glide 사용법
Glide.with(context or activity)
        .load(이미지 URI)
        .into(띄워질 ImageView Id)
 */

class BoardRecyclerViewAdapter(val ctx: Context, var dataList: ArrayList<BoardData>) : RecyclerView.Adapter<BoardRecyclerViewAdapter.Holder>() {
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        view = LayoutInflater.from(ctx).inflate(R.layout.rv_item_board, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.title.text = dataList[position].b_title
        holder.like_cnt.text = dataList[position].b_like.toString()
        holder.date.text = dataList[position].b_date

        val requestOptions = RequestOptions()
//        requestOptions.placeholder(R.drawable.기본적으로 띄울 이미지)
//        requestOptions.error(R.drawable.에러시 띄울 이미지)
//        requestOptions.override(150) - 사진 크기를 줄일 수 있다.
        Glide.with(ctx)
                .setDefaultRequestOptions(requestOptions)
                .load(dataList[position].b_photo)
                .thumbnail(0.5f)
                .into(holder.image)

        holder.like.setOnClickListener {
            val token = SharedPreferencesController.getAuthorization(ctx)

            val postLikeResponse = networkService.postLikeResponse("application/json", token, dataList[position].b_id)
            postLikeResponse.enqueue(object: Callback<PostLikeResponse> {
                override fun onFailure(call: Call<PostLikeResponse>?, t: Throwable?) {
                    Log.e("<게시판-좋아요/취소> 통신 fail: ", t.toString())
                }

                override fun onResponse(call: Call<PostLikeResponse>?, response: Response<PostLikeResponse>) {
                    if (response.isSuccessful) {
                        ctx.toast(response.body()!!.message)
                    } else {
                        Log.e("<게시판-좋아요/취소> 응답 fail: ", response.code().toString())
                        Log.e("<게시판-좋아요/취소> 응답 fail: ", response.errorBody().toString())
                    }
                }

            })
        }

        view.setOnClickListener {
            ctx.startActivity<BoardDetailActivity>("contentIdx" to dataList[position].b_id)
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_rv_item_board_title) as TextView
        var like_cnt: TextView = itemView.findViewById(R.id.tv_rv_item_board_like_cnt) as TextView
        val date: TextView = itemView.findViewById(R.id.tv_rv_item_board_date) as TextView
        val image: ImageView = itemView.findViewById(R.id.iv_rv_item_board_image) as ImageView
        val like: TextView = itemView.findViewById(R.id.tv_rv_item_board_like) as TextView
    }
}