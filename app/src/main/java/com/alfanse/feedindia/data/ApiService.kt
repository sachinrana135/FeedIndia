package com.alfanse.feedindia.data

import com.alfanse.feedindia.data.models.*
import retrofit2.http.*

interface ApiService {
    @POST("saveDonor")
    suspend fun saveDonor(@Body saveDonorRequest: SaveDonorRequest): SaveDonorResponse

    @GET("getUserByMobile")
    suspend fun getUserByMobile(@Query("mobile")  mobile:String): UserEntity

    @GET("getUserById")
    suspend fun getUserById(@Query("userId")  userId:String): UserEntity

    @PUT("updateDonor")
    suspend fun updateDonor(@Body updateDonorRequest: UpdateDonorRequest): Any

    @GET("getGroupNeedierItems")
    suspend fun getNeediers(@Query("group_id") groupId: String,@Query("status") status: String,@Query("page") page: Int, @Query("page_load") pageLoad: Int): List<NeedieritemEntity>

    @POST("saveGroup")
    suspend fun saveGroup(@Body saveGroupRequest: SaveGroupRequest): SaveGroupResponse

    @GET("getNearByUsers")
    suspend fun getNearByUsers(@Query("lat") lat: Double,
                               @Query("lng") lng: Double,
                               @Query("distance") distance: Int,
                               @Query("user_type") userType: String): List<NearByUsersEntity>

    @GET("getGroupMember")
    suspend fun getMembers(@Query("group_id") groupId: String,@Query("page") page: Int, @Query("page_load") pageLoad: Int): List<UserEntity>

    @GET("getComments")
    suspend fun getComments(@Query("needier_item_id") needierItemId: String,@Query("page") page: Int, @Query("page_load") pageLoad: Int): List<CommentEntity>

    @GET("getNeedier")
    suspend fun getNeedier(@Query("needier_item_id")  needier_item_id:String): NeedieritemEntity

    @GET("getNeedierItemStatusTypes")
    suspend fun getNeedierItemStatusTypes(): List<NeedierItemStatusEntity>

    @PUT("updateNeedierItemStatus")
    suspend fun updateNeedierItemStatus(@Body request: UpdateNeedierItemStatusRequest): Any

    @POST("saveComment")
    suspend fun saveComment(@Body saveCommentRequest: SaveCommentRequest): Any

    @POST("saveNeedier")
    suspend fun saveNeedier(@Body saveNeedierRequest: SaveNeedierRequest) : SaveNeedierResponse

    @POST("saveMember")
    suspend fun saveMember(@Body saveNeedierRequest: SaveNeedierRequest) : SaveNeedierResponse
}