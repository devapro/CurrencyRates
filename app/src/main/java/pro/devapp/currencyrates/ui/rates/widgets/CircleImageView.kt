package pro.devapp.currencyrates.ui.rates.widgets

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView

class CircleImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var mBitmapShader: Shader? = null
    private val mShaderMatrix = Matrix()

    private val mBitmapDrawBounds = RectF()
    private val mStrokeBounds = RectF()

    private var mBitmap: Bitmap? = null

    private val mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mInitialized = false


    init {
        mInitialized = true
        setupBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateCircleDrawBounds(mBitmapDrawBounds)
        mStrokeBounds.set(mBitmapDrawBounds)
        updateBitmapSize()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = CircleImageViewOutlineProvider(mStrokeBounds)
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
    }


    fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }

    fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = width - paddingLeft - paddingRight.toFloat()
        val contentHeight =
            height - paddingTop - paddingBottom.toFloat()
        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }
        val diameter = Math.min(contentWidth, contentHeight)
        bounds[left, top, left + diameter] = top + diameter
    }

    private fun setupBitmap() {
        if (!mInitialized) {
            return
        }
        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader
        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        if (mBitmap == null) return
        val dx: Float
        val dy: Float
        val scale: Float

        // scale up/down with respect to this view size and maintain aspect ratio
        // translate bitmap position with dx/dy to the center of the image
        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapDrawBounds.width() / mBitmap!!.width.toFloat()
            dx = mBitmapDrawBounds.left
            dy =
                mBitmapDrawBounds.top - mBitmap!!.height * scale / 2f + mBitmapDrawBounds.width() / 2f
        } else {
            scale = mBitmapDrawBounds.height() / mBitmap!!.height.toFloat()
            dx =
                mBitmapDrawBounds.left - mBitmap!!.width * scale / 2f + mBitmapDrawBounds.width() / 2f
            dy = mBitmapDrawBounds.top
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class CircleImageViewOutlineProvider internal constructor(rect: RectF) :
        ViewOutlineProvider() {
        private val mRect: Rect = Rect(
            rect.left.toInt(),
            rect.top.toInt(),
            rect.right.toInt(),
            rect.bottom.toInt()
        )

        override fun getOutline(view: View, outline: Outline) {
            outline.setOval(mRect)
        }

    }
}