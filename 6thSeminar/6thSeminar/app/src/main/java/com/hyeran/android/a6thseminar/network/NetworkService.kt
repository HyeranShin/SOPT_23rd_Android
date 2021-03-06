package com.hyeran.android.a6thseminar.network

import com.google.gson.JsonObject
import com.hyeran.android.a6thseminar.delete.DeleteBoardResponse
import com.hyeran.android.a6thseminar.delete.DeleteDismemberResponse
import com.hyeran.android.a6thseminar.get.GetBoardDetailResponse
import com.hyeran.android.a6thseminar.get.GetBoardListResponse
import com.hyeran.android.a6thseminar.get.GetCheckMemberInfoResponse
import com.hyeran.android.a6thseminar.post.*
import com.hyeran.android.a6thseminar.put.PutModifyBoardResponse
import com.hyeran.android.a6thseminar.put.PutModifyMemeberInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

// 통신에 쓰이는 추상 메소드들
// 통신 하나 당 추상 메소드 하나
interface NetworkService {

    //**************************로그인**************************
    @POST("/login")
    fun postLogInResponse(
            @Header("Content-Type") content_type: String,
            @Body() body: JsonObject
    ) : Call<PostLogInResponse>

    //**************************회원**************************
    // 회원 조회
    @GET("/users/{userIdx}")
    fun getCheckMemberInfoResponse(
            @Header("Content-Type") content_type: String,
            @Header("Authorization") token: String,
            @Path("userIdx") userIdx: Int
    ) : Call<GetCheckMemberInfoResponse>

    // 회원 가입
    @POST("/users")  // @메소드타입("경로")
    fun postSignUpResponse(
            // 똑~같이 써야하니까 api 문서에서 복붙
            @Header("Content-Type") content_type: String,
            // 정적으로 값을 줘도 된다.
            // @Header("Content-Type") content_type: String = "application/json"
            @Body() body: JsonObject    // 폼 형식
    ) : Call<PostSignUpResponse>

    // 회원 정보 수정
    @Multipart
    @PUT("/users/{userIdx}")
    fun putModifyMemberInfoResponse(
            @Header("Authorization") token: String,
            @Path("userIdx") userIdx: Int,
            @Part("name") name: String?,
            @Part("part") part: String?,
            @Part("email") email: String?,
            @Part profile: MultipartBody.Part?
    ) : Call<PutModifyMemeberInfoResponse>

    // 회원 탈퇴
    @DELETE("/users/{userIdx}")
    fun deleteDismemberResponse(
            @Header("Content-Type") content_type: String,
            @Header("Authorization") token: String,
            @Path("userIdx") userIdx: Int
    ) : Call<DeleteDismemberResponse>

    //**************************게시판**************************
    // 모든 게시글 조회
    @GET("/contents")
    fun getBoardListResponse(
            @Header("Content-Type") content_type: String,
            @Query("offset") offset: Int,
            @Query("limit") limit: Int
    ) : Call<GetBoardListResponse>

    // 게시글 상세
    @GET("/contents/{contentIdx}")
    fun getBoardDetailResponse(
            @Header("Content-Type") content_type: String,
            @Header("Authorization") token: String?,
            @Path("contentIdx") contentIdx: Int
    ) : Call<GetBoardDetailResponse>

    // 게시글 작성
    @Multipart  // 파라미터 형식
    @POST("/contents")
    fun postWriteBoardResponse(
            @Header("Authorization") token: String,
            @Part("title") title: RequestBody,  // @Part에 String 타입을 넣을 땐 RequestBody에 담기
            @Part("contents") contents: RequestBody,
            @Part photo: MultipartBody.Part?    // 파일은 소괄호 X
    ) : Call<PostWriteBoardResponse>

    // 좋아요/취소
    @POST("/contents/{contentIdx}/likes")
    fun postLikeResponse(
            @Header("Content-Type") content_type: String,
            @Header("Authorization") token: String,
            @Path("contentIdx") contentIdx: Int
    ) : Call<PostLikeResponse>

    // 게시글 수정
    @Multipart
    @PUT("/contents/{contentIdx}")
    fun putModifyBoardResponse(
            @Header("Authorization") token: String,
            @Path("contentIdx") contentIdx: Int,
            @Part("title") title: RequestBody,  // @Part에 String 타입을 넣을 땐 RequestBody에 담기
            @Part("contents") contents: RequestBody,
            @Part photo: MultipartBody.Part?
    ) : Call<PutModifyBoardResponse>

    // 게시글 삭제
    @DELETE("/contents/{contentIdx}")
    fun deleteBoardResponse(
            @Header("Content-Type") content_type: String,
            @Header("Authorization") token: String,
            @Path("contentIdx") contentIdx: Int
    ) : Call<DeleteBoardResponse>

    //**************************댓글**************************
    // 댓글 조회

    // 댓글 작성

    // 댓글 좋아요

    // 댓글 수정

    // 댓글 삭제
}

/*
사진과 같은 File을 전송하는 통신은 @Multipart 방식 사용
이전 요청 바디와 형식이 같지만, body에 json 형식의 데이터를 보내는 것이 아니라,
@Part를 통해 데이터를 담는다.
@Part에 String 타입을 넣을 땐 String이 아닌 RequestBody에 담아줘야한다.
(Int나 Double 등 다른 타입의 경우는 그대로)
 */

/*
파라미터 형식일 경우
@POST 앞에 @FormUrlEncoded
@Body 대신 @Field로 변수 하나하나 담는다.
*/