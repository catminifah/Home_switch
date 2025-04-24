package com.example.home_switch

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

internal class GridRVAdapter(
    private val courseList: ArrayList<RoomModel>,
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var courseTV: TextView
    private lateinit var courseIV: ImageView
    private lateinit var li: LinearLayout

    override fun getCount(): Int {
        return courseList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {

            convertView = layoutInflater!!.inflate(R.layout.card_item_room, null)
        }

        courseIV = convertView!!.findViewById(R.id.idIVCourse)
        courseTV = convertView!!.findViewById(R.id.idTVCourse)
        li = convertView!!.findViewById(R.id.layoutid)

        var color = courseList.get(position).geticolor()
        val drawable = GradientDrawable().apply {
            if (color != null) {
                colors = courseList.get(position).geticolor().let {
                    it?.let { it1 ->
                        intArrayOf(
                            it1,
                            Color.parseColor("#FFFFFF")
                        )
                    }
                }
            }
            gradientType = GradientDrawable.RADIAL_GRADIENT
            shape = GradientDrawable.OVAL
            gradientRadius = 120F
        }

        if (courseList.get(position).getSwitch() == true) {
            courseIV.setImageResource(R.drawable.light_on)
            li.setBackground(drawable)

        } else {
            courseIV.setImageResource(R.drawable.light_off)
            li.setBackground(null)
        }

        courseTV.setText(courseList.get(position).getTitle())

        return convertView
    }
}