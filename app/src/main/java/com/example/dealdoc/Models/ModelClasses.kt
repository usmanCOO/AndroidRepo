package com.medpicc.dealdoc

data class ModelClassForDeal(
    val id: String,
    val name: String,
    val price: String,
    val date: String,
    val color: String,
    var isDraft: Boolean,
    var status: String,
    var closedDate: String,
)

data class ModelClassForStuffMeetingLink(
    val id: String,
    val StuffMeetingDate: String,
    val StuffMeetingTime: String,
    val StuffMeetingLink: String,
)

data class ModelClassForSharedWith(
    val id: Int,
    val SharedName: String,
    val DealColor: String,
    val investmentSize: Int,
    val DealDescription: String,
    val DealClosedDate: String,
    val DealUpdateDate: String,
    val DealCreator: String,
    val DealShared: String,
    val UnreadMessages: Int,
    val sharedDealStatus: String,
    val SharedProfile: String,
    val CreatorProfile: String,
)

data class ModelClassForCoaching(
    val CoachingVideoName: String,
    val CoachingVideoUrl: String,
    val image: String
)

data class ModelClassForQuestionNames(
    val id: String,
    val QuestionTitle: String,
    val Questions: List<Question>?,
    val deal_id: Int,
    val deal_Status: String,
)

data class questionModel(
    val `data`: List<DataXX>,
    val status: Boolean
)

data class Question(
    val QuestionResponses: List<Any>,
    val category_id: Int,
    val createdAt: String,
    val id: Int,
    val metadata: String,
    val sequence: Int,
    val statement: String,
    val updatedAt: String
)

data class CategoryLabel(
    val category_id: Int,
    val color: String,
    val condition: String,
    val createdAt: String,
    val id: Int,
    val updatedAt: String,
    val value: String
)

data class DataXX(
    val CategoryLabels: List<CategoryLabel>,
    val Questions: List<Question>,
    val createdAt: String,
    val id: Int,
    val is_delete: Boolean,
    val metadata: Any,
    val name: String,
    val order: Int,
    val updatedAt: String
)

data class ModelClassForCoachingVideos(
    val message: String,
    val success: Boolean,
    val video_data: List<VideoData>
)

data class VideoData(
    val createdAt: String,
    val id: Int,
    val isArchive: Boolean,
    val metadata: Any,
    val name: String,
    val thumbnail: String,
    val updatedAt: String,
    val url: String,
    val video_createed_by: Any
)

data class ModelClassForStatements(
    val statement: String,
    val id: Int,
)
data class ModelClassForStatementsAndResponse(
    val statement: String,
    val id: Int,
    val response: String,
    val status: String
)
data class ModelClassForStatementsAndResponseOff(
    val statement: String,
    val id: Int,
    val response: String,
    val status: String
)

data class draftResponse(
    val `data`: List<ResponseData>,
    val status: Boolean
)

data class ResponseData(
    val questionId: Int,
    val status: Boolean
)

data class Deal_Data(
    val dealId: Int,
    val data: ArrayList<ModelClassForDraftDeal>
)
data class ModelClassForDraftDeal(
    val questionId: Int,
    val response: Boolean
)
enum class MyEnum(val value: Boolean) {
    TRUE(true),
    FALSE(false)
}
data class statusFinish(
    var isFinished: Boolean
)
data class ApiResponse(
    val success: Boolean,
    val message: String
)
