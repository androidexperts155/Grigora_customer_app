package com.rvtechnologies.grigora.model

import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.model.models.NewDashboardModel

data class PickupRestaurantsModel(
    @SerializedName("main_info")
    var mainInfo: ArrayList<NewDashboardModel.AllRestautants>
)

