package com.rvtechnologies.grigora.view.ui.dashboard.adapter

/*

class RestaurantPagerAdapter(
    var mContext: Context,
    var restaurantList: ArrayList<RestaurantModel>, var listener: OnItemClickListener
) : PagerAdapter() {

    var mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return restaurantList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as CardView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater = LayoutInflater.from(mContext)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.restaurant_card_view, container,false) as RestaurantCardViewBinding


        val restaurantModel=restaurantList[position]
        if(restaurantModel!=null) {
            binding.restaurantModel = restaurantModel
            binding.root.parentLayout.setOnClickListener {
                listener.onItemClick(restaurantModel)
            }

            container.addView(binding.root)
        }
        return binding.root
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if(`object` is CardView)
        container.removeView(`object`)
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
*/
