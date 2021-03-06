package com.hyeran.android.a6thseminar

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.hyeran.android.a6thseminar.R
import com.hyeran.android.a6thseminar.adapter.BoardRecyclerViewAdapter
import com.hyeran.android.a6thseminar.data.BoardData
import com.hyeran.android.a6thseminar.db.SharedPreferencesController
import com.hyeran.android.a6thseminar.delete.DeleteDismemberResponse
import com.hyeran.android.a6thseminar.get.GetBoardListResponse
import com.hyeran.android.a6thseminar.get.GetCheckMemberInfoResponse
import com.hyeran.android.a6thseminar.network.ApplicationController
import com.hyeran.android.a6thseminar.network.NetworkService
import kotlinx.android.synthetic.main.activity_board.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardActivity : AppCompatActivity() {
    val WRITE_ACTIVITY_REQUEST_CODE = 1000
    lateinit var boardRecyclerViewAdapter: BoardRecyclerViewAdapter
    val dataList : ArrayList<BoardData> by lazy {
        ArrayList<BoardData>()
    }
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        setOnBtnClickListener()
        setRecyclerView()
        getBoardListResponse()
    }

    private fun setRecyclerView(){
        boardRecyclerViewAdapter = BoardRecyclerViewAdapter(this, dataList)
        rv_board_act_board_list.adapter = boardRecyclerViewAdapter
        rv_board_act_board_list.layoutManager = LinearLayoutManager(this)
    }

    private fun getBoardListResponse(){
        val getBoardListResponse = networkService.getBoardListResponse("application/json", 0, 30)
        getBoardListResponse.enqueue(object : Callback<GetBoardListResponse> {
            override fun onFailure(call: Call<GetBoardListResponse>, t: Throwable) {
                Log.e("<게시판-모든 게시글 조회> fail", t.toString())
            }
            override fun onResponse(call: Call<GetBoardListResponse>, response: Response<GetBoardListResponse>) {
                if (response.isSuccessful){
                    toast(response.body()!!.message)
                    val temp : ArrayList<BoardData> = response.body()!!.data
                    if (temp.size > 0){
                        val position = boardRecyclerViewAdapter.itemCount
                        boardRecyclerViewAdapter.dataList.addAll(temp)
                        boardRecyclerViewAdapter.notifyItemInserted(position)
                    }
                } else {
                    Log.e("<게시판-모든 게시글 조회> 응답 fail: ", response.code().toString())
                    Log.e("<게시판-모든 게시글 조회> 응답 fail: ", response.errorBody().toString())
                }
            }
        })
    }

    private fun setOnBtnClickListener(){
        btn_board_act_write_board.setOnClickListener {
            startActivityForResult<WriteActivity>(WRITE_ACTIVITY_REQUEST_CODE)
            // 새로운 액티비티를 띄운 다음에 꺼지고 이 응답에 대한 처리 -> onActivityResult (갱신을 위한 재통신)
        }
        btn_dismember.setOnClickListener {
//            deleteDismemberResponse()
            SharedPreferencesController.clearSPC(this@BoardActivity)
//            startActivity<MainActivity>()
        }
    }

//    private fun deleteDismemberResponse() {
//        checkMemberInfoResponse()
//        val token = SharedPreferencesController.getAuthorization(this)
//        val deleteDismemberResponse = networkService.deleteDismemberResponse("application/json", token, 1)
//        deleteDismemberResponse.enqueue(object : Callback<DeleteDismemberResponse> {
//            override fun onFailure(call: Call<DeleteDismemberResponse>?, t: Throwable?) {
//                Log.e("<회원-회원 탈퇴> fail", t.toString())
//            }
//
//            override fun onResponse(call: Call<DeleteDismemberResponse>?, response: Response<DeleteDismemberResponse>) {
//                if (response.isSuccessful){
//                    toast(response.body()!!.message)
//                } else {
//                    Log.e("<회원-회원 탈퇴> 응답 fail: ", response.code().toString())
//                    Log.e("<회원-회원 탈퇴> 응답 fail: ", response.errorBody().toString())
//                }
//            }
//        })
//    }
//
//    private fun checkMemberInfoResponse() {
//        val token = SharedPreferencesController.getAuthorization(this)
//        val getCheckMemberInfoResponse = networkService.getCheckMemberInfoResponse()
//        getCheckMemberInfoResponse.enqueue(object : Callback<GetCheckMemberInfoResponse> {
//            override fun onFailure(call: Call<GetCheckMemberInfoResponse>?, t: Throwable?) {
//                Log.e("<회원-회원 탈퇴> fail", t.toString())
//            }
//
//            override fun onResponse(call: Call<GetCheckMemberInfoResponse>?, response: Response<GetCheckMemberInfoResponse>) {
//                if (response.isSuccessful){
//                    toast(response.body()!!.message)
//                } else {
//                    Log.e("<회원-회원 탈퇴> 응답 fail: ", response.code().toString())
//                    Log.e("<회원-회원 탈퇴> 응답 fail: ", response.errorBody().toString())
//                }
//            }
//        })
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WRITE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                getBoardListResponse()  // 통신하는 함수만 쓰면 갱신을 할 수 있다. (근데 여기서는 페이징 기능 떄문에 지금 안됨)
                // isChanged라는 값을 넘겨줘서 true일 경우 재통신하는 코드를 써주면 된다.
            }
        }
    }
}
