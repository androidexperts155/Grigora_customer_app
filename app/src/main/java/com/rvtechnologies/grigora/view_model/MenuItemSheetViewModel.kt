package com.rvtechnologies.grigora.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.ItemChoicesModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import java.lang.Exception

class MenuItemSheetViewModel : ViewModel() {
    var menuItem: MutableLiveData<RestaurantDetailNewModel.MealItem> = MutableLiveData()
    var itemCount: MutableLiveData<String> = MutableLiveData()
    var cartId: MutableLiveData<String> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var price: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var itemCategories: MutableLiveData<ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory?>> =
        MutableLiveData()
    var selectedChoices: MutableLiveData<ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory?>> =
        MutableLiveData()
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

    fun addItemToCart() {
        if (cartId.value.isNullOrEmpty()) {

            val choices = ArrayList<ItemChoicesModel>()
            try {
                for (item in selectedChoices.value as Collection<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>) {
                    if (item.checked) {
                        var sel = ""
                        if (!item.item_sub_sub_category.isNullOrEmpty()) {
                            for (i in item.item_sub_sub_category) {
                                if (i.checked) {
                                    sel = if (sel.isNullOrEmpty())
                                        i.id.toString()
                                    else
                                        sel + "," + i.id.toString()
                                }
                            }
                        }

                        var itemChoicesModel = ItemChoicesModel()
                        itemChoicesModel.id = item.item_cat_id
                        var data = ItemChoicesModel.Data()
                        data.id = item.id
                        if (!sel.isEmpty())
                            data.itemSubSubCategory = sel

                        if (itemChoicesModel.itemSubCategory.isNullOrEmpty())
                            itemChoicesModel.itemSubCategory = ArrayList()

                        itemChoicesModel.itemSubCategory!!.add(data)

                        /*  {
                              "id":"2",
                              "item_sub_category":[
                              {
                                  "id":"3",
                                  "item_sub_sub_category":"3,4"
                              },
                              {
                                  "id":"4",
                                  "item_sub_sub_category":"3,4"
                              }
                              ]
                          }*/
//                        above is ready

                        if (choices.isEmpty()) {
                            choices.add(itemChoicesModel)

                        } else {
                            var already = false
                            for (pos in 0 until choices.size) {
                                val parentId = item.item_cat_id
                                if (choices[pos].id == parentId) {
                                    already = true
                                    choices[pos].itemSubCategory?.add(data)

                                }
                            }
                            if (!already) {
                                choices.add(itemChoicesModel)

                            }

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
                        restaurantId = menuItem.value?.restaurant_id.toString(),
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
        } else
            addItemToGroupCart()
    }

    private fun addItemToGroupCart() {

        if (token.value.toString().isNotBlank()) {
            val choices = ArrayList<ItemChoicesModel>()
            try {
                for (item in selectedChoices.value as Collection<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>) {
                    if (item.checked) {
                        var sel = ""
                        if (!item.item_sub_sub_category.isNullOrEmpty()) {
                            for (i in item.item_sub_sub_category) {
                                if (i.checked) {
                                    sel = if (sel.isNullOrEmpty())
                                        i.id.toString()
                                    else
                                        sel + "," + i.id.toString()
                                }
                            }
                        }

                        var itemChoicesModel = ItemChoicesModel()
                        itemChoicesModel.id = item.item_cat_id
                        var data = ItemChoicesModel.Data()
                        data.id = item.id
                        if (!sel.isEmpty())
                            data.itemSubSubCategory = sel

                        if (itemChoicesModel.itemSubCategory.isNullOrEmpty())
                            itemChoicesModel.itemSubCategory = ArrayList()

                        itemChoicesModel.itemSubCategory!!.add(data)

                        /*  {
                              "id":"2",
                              "item_sub_category":[
                              {
                                  "id":"3",
                                  "item_sub_sub_category":"3,4"
                              },
                              {
                                  "id":"4",
                                  "item_sub_sub_category":"3,4"
                              }
                              ]
                          }*/
//                        above is ready

                        if (choices.isEmpty()) {
                            choices.add(itemChoicesModel)

                        } else {
                            var already = false
                            for (pos in 0 until choices.size) {
                                val parentId = item.item_cat_id
                                if (choices[pos].id == parentId) {
                                    already = true
                                    choices[pos].itemSubCategory?.add(data)

                                }
                            }
                            if (!already) {
                                choices.add(itemChoicesModel)

                            }

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            var selected = ""
            if (choices.isNotEmpty()) {
                selected = Gson().toJson(choices)
            }
            Log.e("Choices", selected)
            isLoading.value = true
            if (itemCount.value?.toInt()!! > 0) {
                ApiRepo.getInstance()
                    .addItemToGroupCart(
                        token = token.value.toString(),
                        restaurantId = menuItem.value?.restaurant_id.toString(),
                        itemId = menuItem.value?.id.toString(),
                        price = price.value.toString(),
//                        price = menuItem.value?.price.toString(),
                        quantity = itemCount.value!!,
                        itemChoices = selected, cartId = cartId.value.toString()

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
            for (item in selectedChoices.value as ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory?>) {
                if (!item!!.item_sub_sub_category.isNullOrEmpty()) {
                    for (i in item!!.item_sub_sub_category) {
                        if (i.checked)
                            price += i!!.add_on_price.toDouble()
                    }
                }
                price += item!!.add_on_price
            }
        }
        price *= itemCount.value!!.toDouble()
        this.price.value = CommonUtils.getRoundedOff(price)
    }


    fun destroy(activity: MainActivity) {
        menuItem.value = null
        itemCount.value = null
        cartId.value = null
        token.value = null
        price.value = null
        isLoading.value = null
        itemCategories.value = null
        selectedChoices.value = null
        response.value = null

        menuItem.removeObservers(activity)
        itemCount.removeObservers(activity)
        cartId.removeObservers(activity)
        token.removeObservers(activity)
        price.removeObservers(activity)
        isLoading.removeObservers(activity)
        itemCategories.removeObservers(activity)
        selectedChoices.removeObservers(activity)
        response.removeObservers(activity)

    }
}
