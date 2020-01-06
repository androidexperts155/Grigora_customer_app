package com.rvtechnologies.grigora.model.models

class PaymentModeModel(var id:String, var type:String) {
    override fun toString(): String {
        return type
    }
}