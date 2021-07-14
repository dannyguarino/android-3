package mega.privacy.android.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import mega.privacy.android.app.R
import org.jetbrains.anko.contentView

class HighLightHintHelper(private val activity: Activity) {

    private var popupWindow: PopupWindow? = null

    private lateinit var targetLocation: Rect

    private var statusBarHeight = 0
    private var navigationBarHeight = 0

    private val hintLayoutWidth: Int
    private val hintLayoutHeight: Int
    private val textCoverHeight: Int
    private val screenWidth: Int
    private val screenHeight: Int
    private val arrowSize: Int

    init {
        statusBarHeight = getStatusBarHeight(activity)
        navigationBarHeight =
            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && navigationBarOnLeft()) {
                getNavigationBarHeight(activity)
            } else {
                0
            }

        hintLayoutWidth = Util.dp2px(HINT_LAYOUT_WIDTH_DP)
        hintLayoutHeight = Util.dp2px(HINT_LAYOUT_HEIGHT_DP)
        textCoverHeight = Util.dp2px(TEXT_COVER_HEIGHT_DP)
        arrowSize = Util.dp2px(ARROW_SIZE_DP)

        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
    }

    private fun navigationBarOnLeft(): Boolean {
        val windowManager = activity.windowManager

        if (!hasNavigationBar()) return false

        // If it's a tablet.
        if(Util.isTablet(activity)) return false

        // If it's a HUAWEI device.
        if(MANUFACTURE_HUAWEI.equals(Build.MANUFACTURER, true)) return false

        // Rotate to right, navigation bar will stay at the left of the screen.
        return windowManager.defaultDisplay.rotation == Surface.ROTATION_270
    }

    private fun getMetrics(): Pair<DisplayMetrics, DisplayMetrics> {
        val display = activity.windowManager.defaultDisplay

        val realDisplayMetrics = DisplayMetrics()
        display.getRealMetrics(realDisplayMetrics)

        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)

        return Pair(realDisplayMetrics, displayMetrics)
    }

    private fun hasNavigationBar(): Boolean {
        val realWidth = getMetrics().first.widthPixels
        val width = getMetrics().second.widthPixels

        return realWidth != width + statusBarHeight
    }

    fun showHintForMeetingIcon(targetViewId: Int, onDismissCallback: () -> Unit) {
        val target = activity.findViewById<View>(targetViewId)
        if (target == null) {
            LogUtil.logError("TargetView is null. Can't view with id $targetViewId in $activity")
            return
        }

        targetLocation = getLocationInParent(
            activity.contentView!!,
            target
        )

        val contentView = getContentView()

        // Add cover, it's the highlight view covers on the target view.
        contentView.addView(getIconCover(), getIconCoverLayoutParams())
        contentView.addView(getArrow(), getIconArrowLayoutParams())
        contentView.addView(
            getHintView(onDismissCallback, R.string.tip_create_meeting),
            getIconHintLayoutParams()
        )

        popupWindow = PopupWindow(
            contentView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        ).apply {
            isClippingEnabled = false
            showAtLocation(activity.contentView!!, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    fun dismissPopupWindow() = popupWindow?.dismiss()

    private fun getContentView() = FrameLayout(activity).apply {
        // Mask color
        setBackgroundColor(getMaskColor())
    }

    @SuppressLint("InflateParams")
    private fun getHintView(onDismissCallback: () -> Unit, hintTextId: Int) =
        LayoutInflater.from(activity).inflate(R.layout.highlight_hint_meeting, null).apply {
            findViewById<TextView>(R.id.tv_tip).text =
                StringResourcesUtils.getString(hintTextId)
            findViewById<View>(R.id.bt_ok).setOnClickListener {
                onDismissCallback.invoke()
            }
        }

    @SuppressLint("InflateParams")
    private fun getIconCover() = LayoutInflater.from(activity)
        .inflate(R.layout.highlight_cover_icon, null)

    private fun getIconCoverLayoutParams() = FrameLayout.LayoutParams(
        targetLocation.right - targetLocation.left,
        targetLocation.bottom - targetLocation.top
    ).apply {
        leftMargin = targetLocation.left
        topMargin = targetLocation.top + statusBarHeight
    }

    private fun getTextCoverLayoutParams() = FrameLayout.LayoutParams(
        targetLocation.right - targetLocation.left + arrowSize,
        textCoverHeight
    ).apply {
        leftMargin = targetLocation.left
        topMargin =
            targetLocation.top + statusBarHeight - (textCoverHeight - (targetLocation.bottom - targetLocation.top)) / 2
    }

    fun getArrow() = View(activity).apply {
        background =
            ContextCompat.getDrawable(activity, R.drawable.background_meeting_hint_up_arrow)
    }

    private fun getIconArrowLayoutParams() = FrameLayout.LayoutParams(
        arrowSize,
        arrowSize
    ).apply {
        // Point to the middle of the cover.
        leftMargin =
            targetLocation.left + (targetLocation.right - targetLocation.left) / 2 - arrowSize / 2
        topMargin = statusBarHeight + targetLocation.bottom
    }

    private fun getTextArrowLayoutParams() = FrameLayout.LayoutParams(
        arrowSize,
        arrowSize
    ).apply {
        // Point to the middle of the cover.
        leftMargin =
            targetLocation.left + (targetLocation.right - targetLocation.left) / 2 - arrowSize / 2
        topMargin =
            targetLocation.bottom + statusBarHeight + (textCoverHeight - (targetLocation.bottom - targetLocation.top)) / 2
    }

    private fun getTextHintLayoutParams() = FrameLayout.LayoutParams(
        hintLayoutWidth,
        hintLayoutHeight
    ).apply {
        val arrowLeft =
            targetLocation.left + (targetLocation.right - targetLocation.left) / 2 - arrowSize / 2

        // arrowLeft / 6 is a bit of offset.
        leftMargin = arrowLeft / 6 + navigationBarHeight

        topMargin =
            targetLocation.bottom + statusBarHeight + arrowSize + (textCoverHeight - (targetLocation.bottom - targetLocation.top)) / 2
    }

    private fun getIconHintLayoutParams() = FrameLayout.LayoutParams(
        hintLayoutWidth,
        hintLayoutHeight
    ).apply {
        val arrowLeft =
            targetLocation.left + (targetLocation.right - targetLocation.left) / 2 - arrowSize / 2
        val exceedsPart = hintLayoutWidth + arrowLeft - screenWidth

        leftMargin = if (exceedsPart >= 0) {
            // Exceeds screen end. (screenWidth - arrowLeft - arrowSize) / 4 is a bit of offset.
            arrowLeft - exceedsPart - (screenWidth - arrowLeft - arrowSize) / 4
        } else {
            // arrowLeft / 4 is a bit of offset.
            arrowLeft / 4
        }

        topMargin = targetLocation.bottom + statusBarHeight + arrowSize
    }

    fun showHintForMeetingText(targetViewId: Int, onDismissCallback: () -> Unit) {
        val target = activity.findViewById<View>(targetViewId)
        if (target == null) {
            LogUtil.logError("TargetView is null. Can't view with id $targetViewId in $activity")
            return
        }

        targetLocation = getLocationInParent(
            activity.contentView!!,
            target
        )

        targetLocation.left = targetLocation.left + navigationBarHeight
        targetLocation.right = targetLocation.right + navigationBarHeight

        val contentView = getContentView()
        contentView.addView(getTextCover(), getTextCoverLayoutParams())
        contentView.addView(getArrow(), getTextArrowLayoutParams())
        contentView.addView(
            getHintView(onDismissCallback, R.string.tip_setup_meeting),
            getTextHintLayoutParams()
        )

        popupWindow = PopupWindow(
            contentView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        ).apply {
            isClippingEnabled = false
            showAtLocation(activity.contentView, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    @SuppressLint("InflateParams")
    private fun getTextCover() = LayoutInflater.from(activity)
        .inflate(R.layout.highlight_cover_text, null)

    private fun getStatusBarHeight(activity: Activity) =
        getSystemBarHeight(activity, "status_bar_height")

    private fun getNavigationBarHeight(activity: Activity) =
        getSystemBarHeight(activity, "navigation_bar_height")

    private fun getSystemBarHeight(activity: Activity, name: String): Int {
        val resource = activity.applicationContext.resources
        val resId = resource.getIdentifier(name, "dimen", "android")
        return resource.getDimensionPixelSize(resId)
    }

    private fun getLocationInParent(parent: View, child: View): Rect {
        var decorView: View? = null

        val activity = child.context
        if (activity is Activity) {
            decorView = activity.window.decorView
        }

        val result = Rect()
        val tmpRect = Rect()
        var tmp: View = child
        if (child === parent) {
            child.getHitRect(result)
            return result
        }

        while (tmp !== decorView && tmp !== parent) {
            tmp.getHitRect(tmpRect)
            if (tmp.javaClass.simpleName != "NoSaveStateFrameLayout") {
                result.left += tmpRect.left
                result.top += tmpRect.top
            }

            tmp = tmp.parent as View
        }

        result.right = result.left + child.measuredWidth
        result.bottom = result.top + child.measuredHeight
        return result
    }

    private fun getMaskColor() = ContextCompat.getColor(activity, R.color.grey_alpha_032)

    companion object {

        const val MANUFACTURE_HUAWEI = "HUAWEI"
        const val ARROW_SIZE_DP = 16f
        const val HINT_LAYOUT_WIDTH_DP = 290f
        const val HINT_LAYOUT_HEIGHT_DP = 75f
        const val TEXT_COVER_HEIGHT_DP = 56f
    }
}