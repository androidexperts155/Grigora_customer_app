package com.rvtechnologies.grigora.model

data class RoutesModel(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)