package com.example.c001apk.view.ninegridimageview

/*
MIT License

Copyright (c) 2021 Plain

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.c001apk.R
import com.example.c001apk.constant.Constants
import com.example.c001apk.util.BitmapCut
import com.example.c001apk.util.DensityTool
import com.example.c001apk.util.PrefManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import net.mikaelzero.mojito.tools.Utils.dip2px


class NineGridImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var urlList: List<String>? = null
    var onImageItemClickListener: OnImageItemClickListener? = null

    var imgHeight = 0
    var imgWidth = 0

    private var singleWidth = 0
    private var singleHeight = 0

    private var totalWidth = 0

    private var columns: Int = 0
    private var rows: Int = 0


    private val itemGap = 5
    private var gap: Int = 0

    init {
        gap = dip2px(context, itemGap.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        totalWidth = sizeWidth - paddingLeft - paddingRight
        val defaultWidth = (totalWidth - gap * (3 - 1)) / 3
        if (urlList != null && urlList!!.isNotEmpty()) {
            val childrenCount = urlList!!.size
            if (childrenCount == 1) {
                if (imgHeight < imgWidth) {
                    singleHeight = defaultWidth * 2
                    singleWidth = singleHeight * imgWidth / imgHeight
                    if (singleWidth > totalWidth) {
                        singleWidth = totalWidth
                        singleHeight = singleWidth * imgHeight / imgWidth
                    }
                } else if (imgHeight > imgWidth) {
                    if (imgHeight / imgWidth < 1.5) {
                        singleWidth = defaultWidth * 2
                        singleHeight = singleWidth * imgHeight / imgWidth
                    } else if (imgHeight / imgWidth <= 1320 / 540) {
                        singleWidth = defaultWidth
                        singleHeight = singleWidth * imgHeight / imgWidth
                    } else {
                        singleWidth = defaultWidth
                        singleHeight = singleWidth * 1320 / 540
                    }
                } else {
                    singleWidth = defaultWidth * 2
                    singleHeight = defaultWidth * 2
                }
            } else {
                singleWidth = defaultWidth
                singleHeight = defaultWidth
            }
            measureChildren(
                MeasureSpec.makeMeasureSpec(singleWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(singleHeight, MeasureSpec.EXACTLY)
            )
            val measureHeight: Int = singleHeight * rows + gap * (rows - 1)
            setMeasuredDimension(sizeWidth, measureHeight)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layoutChildrenView()
    }

    private fun findPosition(childNum: Int): IntArray {
        val position = IntArray(2)
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (i * columns + j == childNum) {
                    position[0] = i //行
                    position[1] = j //列
                    break
                }
            }
        }
        return position
    }

    private fun layoutChildrenView() {
        if (urlList == null || urlList!!.isEmpty()) {
            return
        }
        val childrenCount = urlList!!.size
        for (i in 0 until childrenCount) {
            val position = findPosition(i)
            val left = (singleWidth + gap) * position[1] + paddingLeft
            val top = (singleHeight + gap) * position[0] + paddingTop
            val right = left + singleWidth
            val bottom = top + singleHeight
            val childrenView = getChildAt(i) as ImageView
            if (childrenCount == 1) {
                //只有一张图片
                childrenView.scaleType = ImageView.ScaleType.FIT_CENTER
            } else {
                childrenView.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            childrenView.setOnClickListener {
                if (onImageItemClickListener != null) {
                    onImageItemClickListener!!.onClick(
                        this,
                        childrenView,
                        urlList!!,
                        i
                    )
                }
            }
            childrenView.layout(left, top, right, bottom)
        }
    }


    private fun generateChildrenLayout(length: Int) {
        if (length <= 3) {
            rows = 1
            columns = length
        } else if (length <= 6) {
            rows = 2
            columns = 3
            if (length == 4) {
                columns = 2
            }
        } else {
            rows = 3
            columns = 3
        }
    }

    fun getImageViews(): List<ImageView> {
        val imageViews = mutableListOf<ImageView>()
        for (i in 0 until childCount) {
            val imageView = getChildAt(i)
            if (imageView is ImageView) {
                imageViews.add(imageView)
            }
        }
        return imageViews
    }

    fun getImageViewAt(position: Int) = getChildAt(position) as? ImageView

    @SuppressLint("RestrictedApi")
    fun setUrlList(urlList: List<String>?) {
        if (urlList != null) {
            this.urlList = urlList
            generateChildrenLayout(urlList.size)
            removeAllViews()

            for (i in urlList.indices) {
                val imageView = ShapeableImageView(context)
                val shapePathModel = ShapeAppearanceModel.builder()
                    .setAllCorners(RoundedCornerTreatment())
                    .setAllCornerSizes(DensityTool.dp2px(context, 12f))
                    .build()
                imageView.shapeAppearanceModel = shapePathModel
                imageView.strokeWidth = DensityTool.dp2px(context, 1f)
                imageView.strokeColor = context.getColorStateList(R.color.cover)
                addView(imageView, generateDefaultLayoutParams())
                /*val options = RequestOptions().transform(
                    RoundedCorners(
                        DensityTool.dp2px(context, 12f).toInt()
                    )
                )*/
                val backgroundDrawable = MaterialShapeDrawable(shapePathModel).apply {
                    setTint(context.getColor(R.color.cover))
                    paintStyle = Paint.Style.FILL
                }
                val newUrl =
                    GlideUrl(
                        urlList[i],
                        LazyHeaders.Builder().addHeader("User-Agent", Constants.USER_AGENT).build()
                    )
                if (urlList.size == 1 && PrefManager.isFullImageQuality && imgHeight / imgWidth > 1320 / 540) {
                    Glide.with(context)
                        .asBitmap()
                        .load(newUrl)
                        .into(object : CustomTarget<Bitmap?>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                val bitmap = BitmapCut.cutBitmap(resource)
                                if (bitmap != null)
                                    imageView.setImageBitmap(bitmap)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                } else {
                    Glide.with(context)
                        .load(newUrl)
                        .centerCrop()
                        .placeholder(backgroundDrawable)
                        .into(imageView)
                }
            }
        }
    }

}