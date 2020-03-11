package com.rvtechnologies.grigora.model

import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.view.ui.search.SearchItem

data class DashboardSearchModel(
    @SerializedName("main_info")
    var mainInfo: MainInfo

) {

    inner class MainInfo(
        @SerializedName("cuisines")
        var cuisines: ArrayList<SearchCuisineModel>,
        @SerializedName("restaurants")
        var restaurants: ArrayList<NewDashboardModel.AllRestautants>
    )
}

