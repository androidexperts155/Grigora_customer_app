package com.rvtechnologies.grigora.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.GrigoraApp
import java.lang.Exception

class MenuItemDetailsViewModel : ViewModel() {
    var menuItem: MutableLiveData<MenuItemModel> = MutableLiveData()
    var itemCount: MutableLiveData<String> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var price: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var itemCategories: MutableLiveData<ArrayList<ItemCategory?>> = MutableLiveData()
    var selectedChoices: MutableLiveData<ArrayList<ItemSubCategory?>> = MutableLiveData()

    var response: MutableLiveData<Any> = MutableLiveData()

    init {
        itemCount.value = "1"
    }

    fun decreaseQuantity() {
        if (itemCount.value?.toInt()!! > 1)
            itemCount.value = itemCount.value?.toInt()!!.minus(1).toString()
        else
            itemCount.value = "1"

        refresh()
    }

    fun increaseQuantity() {
        itemCount.value = itemCount.value?.toInt()!!.plus(1).toString()
        refresh()
    }

    fun isFrench(): Boolean {
        return GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH
    }

    fun getCategoryItems() {
        itemCategories.value =
            (menuItem.value as MenuItemModel).itemCategories as ArrayList<ItemCategory?>
    }

    fun addItemToCart() {

        if (token.value.toString().isNotBlank()) {
            val choices = ArrayList<ItemChoicesModel>()
            try {
                for (item in selectedChoices.value as Collection<ItemSubCategory>) {
                    if (item.checked) {
                        if (choices.isEmpty()) {
                            choices.add(ItemChoicesModel(item.itemCatId, item.id))

                            menuItem.value?.price =
                                (menuItem.value?.price?.toDouble()!! + item.addOnPrice).toString()

                        } else {
                            var already = false
                            for (pos in 0 until choices.size) {
                                val parentId = item.itemCatId
                                if (choices[pos].id == parentId) {
                                    already = true
                                    var choice = choices[pos].itemSubCategory!!
                                    if (!choices[pos].itemSubCategory?.contains(item.id!!)!!)
                                        choice =
                                            choices[pos].itemSubCategory?.plus(",")?.plus(item.id)!!

                                    choices[pos] = ItemChoicesModel(parentId, choice)
                                }
                            }
                            if (!already) {
                                choices.add(ItemChoicesModel(item.itemCatId, item.id))
                                menuItem.value?.price =
                                    (menuItem.value?.price?.toDouble()!! + item.addOnPrice).toString()
                            }

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.e("Choices model", Gson().toJson(selectedChoices.value))
            Log.e("Choices", Gson().toJson(choices))

            var selected = ""
            if (choices.isNotEmpty()) {
                selected = Gson().toJson(choices)
            }
            Log.e("Choices", selected)

            isLoading.value = true
            if (itemCount.value?.toInt()!! > 0) {
                ApiRepo.getInstance()
                    .addItemToCart(
                        token = token.value.toString(),
                        restaurantId = menuItem.value?.restaurantId.toString(),
                        itemId = menuItem.value?.id.toString(),
                        price = price.value.toString(),
//                        price = menuItem.value?.price.toString(),
                        quantity = itemCount.value!!,
                        itemChoices = selected
                    ) { success, result ->
                        isLoading.value = false
                        if (success) {
                            response.value =
                                Gson().fromJson(
                                    result as JsonElement,
                                    CommonResponseModel::class.java
                                )
                        } else {
                            response.value = result
                        }
                    }
            } else {
                response.value = "Quantity too low"

            }
        } else {
            response.value = "Unauthorized"
        }
    }

    fun refresh() {
        var price = menuItem.value!!.price!!.toDouble()
        if (selectedChoices.value != null) {
            for (item in selectedChoices.value as ArrayList<ItemSubCategory?>) {
                price += item!!.addOnPrice
            }
        }

        price *= itemCount.value!!.toDouble()
        this.price.value = price.toString()

/*


        val choices = ArrayList<ItemChoicesModel>()
        try {
            for (item in selectedChoices.value as Collection<ItemSubCategory>) {
                if (item.checked) {
                    if (choices.isEmpty()) {
                        choices.add(ItemChoicesModel(item.itemCatId, item.id))

                        menuItem.value?.price =
                            (menuItem.value?.price?.toDouble()!! + item.addOnPrice).toString()

                    } else {
                        var already = false
                        for (pos in 0 until choices.size) {
                            val parentId = item.itemCatId
                            if (choices[pos].id == parentId) {
                                already = true
                                var choice = choices[pos].itemSubCategory!!
                                if (!choices[pos].itemSubCategory?.contains(item.id!!)!!)
                                    choice =
                                        choices[pos].itemSubCategory?.plus(",")?.plus(item.id)!!

                                choices[pos] = ItemChoicesModel(parentId, choice)
                            }
                        }
                        if (!already) {
                            choices.add(ItemChoicesModel(item.itemCatId, item.id))
                            menuItem.value?.price =
                                (menuItem.value?.price?.toDouble()!! + item.addOnPrice).toString()
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e("Choices model", Gson().toJson(selectedChoices.value))
        Log.e("Choices", Gson().toJson(choices))*/

    }
}
