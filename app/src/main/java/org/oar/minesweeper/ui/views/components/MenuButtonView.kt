package org.oar.minesweeper.ui.views.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import org.oar.minesweeper.R
import org.oar.minesweeper.control.ScreenProperties.toDpi
import org.oar.minesweeper.utils.ContextUtils.findColor

class MenuButtonView(
    context: Context,
    private val attrs: AttributeSet,
) : LinearLayout(context, attrs) {


    init {
        orientation = VERTICAL

        val text = getAttrString("text")
        val subText = getAttrString("subText")

        TextView(context).apply {
            id = generateViewId()
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                .apply {
                    if (subText != null) {
                        val padding = 3.toDpi().toInt()
                        setPadding(0, 0, 0, padding)
                    }
                }
            gravity = Gravity.CENTER
            this.text = text
            setTextColor(context.findColor(android.R.color.white))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.menu_text)
            );

            this@MenuButtonView.addView(this)
        }

        if (subText != null) {
            TextView(context).apply {
                id = generateViewId()
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                gravity = Gravity.CENTER
                this.text = subText
                setTextColor(context.findColor(R.color.white_splash))
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.menu_sub_text)
                );

                this@MenuButtonView.addView(this)
            }
        }
    }


    private fun getAttrString(name: String): String? {
        val value = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", name)
            ?: null

        return if (value != null && value.startsWith("@")) {
            context.getString(value.substring(1).toInt())
        } else {
            value
        }
    }
}