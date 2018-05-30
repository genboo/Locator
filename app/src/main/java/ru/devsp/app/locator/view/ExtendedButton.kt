package ru.devsp.app.locator.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import ru.devsp.app.locator.R

class ExtendedButton : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private lateinit var button: Button
    private lateinit var image: ImageView

    private fun init() {
        button = Button(context, null, 0, R.style.Button_Rounded_Main)
        image = ImageView(context, null, 0, R.style.Button_Rounded_Image)

        val paramsImage = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        paramsImage.gravity = Gravity.END
        image.layoutParams = paramsImage
        image.setImageDrawable(resources.getDrawable(R.drawable.ic_map_marker, context.theme))

        val paramsButton = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        paramsButton.setMargins(0, 0, resources.getDimensionPixelSize(R.dimen.extended_button_right), 0)
        button.layoutParams = paramsButton
        button.text = "Test"

        addView(image)
        addView(button)
    }

    fun setOnExtendedButtonClickListener(listener: OnClickListener) {
        image.setOnClickListener(listener)
    }

    override fun setOnClickListener(listener: OnClickListener) {
        button.setOnClickListener(listener)
    }

    fun setText(text: String) {
        button.text = text
    }

}