package mega.privacy.android.app.image

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import mega.privacy.android.app.BaseActivity
import mega.privacy.android.app.databinding.ActivityImageViewerBinding
import mega.privacy.android.app.image.adapter.ImageViewerAdapter
import mega.privacy.android.app.utils.Constants.*
import nz.mega.documentscanner.utils.IntentUtils.extra
import nz.mega.documentscanner.utils.IntentUtils.extraNotNull
import nz.mega.sdk.MegaApiJava.INVALID_HANDLE

@AndroidEntryPoint
class ImageViewerActivity : BaseActivity() {

    companion object {
        private const val OFFSCREEN_PAGE_LIMIT = 3
    }

    private lateinit var binding: ActivityImageViewerBinding

    private val viewModel by viewModels<ImageViewerViewModel>()
    private val pagerAdapter by lazy { ImageViewerAdapter(this) }
    private val pageChangeCallback by lazy{
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val itemCount = binding.viewPager.adapter!!.itemCount
                if (!positionSet && itemCount <= nodePosition) {
                    positionSet = true
                    binding.viewPager.currentItem = nodePosition
                }
            }
        }
    }

    private val nodePosition: Int by extraNotNull(INTENT_EXTRA_KEY_POSITION, 0)
    private val nodeHandle: Long? by extra(INTENT_EXTRA_KEY_HANDLE, INVALID_HANDLE)
    private val parentNodeHandle: Long? by extra(INTENT_EXTRA_KEY_PARENT_NODE_HANDLE, INVALID_HANDLE)
    private val childrenHandles: LongArray? by extra(INTENT_EXTRA_KEY_HANDLES_NODES_SEARCH)

    private var positionSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupObservers()

        when {
            parentNodeHandle != null && parentNodeHandle != INVALID_HANDLE ->
                viewModel.retrieveImagesFromParent(parentNodeHandle!!)
            childrenHandles != null && childrenHandles!!.isNotEmpty() ->
                viewModel.retrieveImages(childrenHandles!!.toList())
            nodeHandle != null && nodeHandle != INVALID_HANDLE ->
                viewModel.retrieveSingleImage(nodeHandle!!)
            else ->
                error("No params were sent")
        }
    }

    override fun onDestroy() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }

    @SuppressLint("WrongConstant")
    private fun setupView() {
        binding.viewPager.apply {
            adapter = pagerAdapter
            offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
            registerOnPageChangeCallback(pageChangeCallback)
        }
    }

    private fun setupObservers() {
        viewModel.defaultPosition = nodePosition
        viewModel.getImages().observe(this) { images ->
            pagerAdapter.submitList(images)
        }
    }

    private fun onImageClick(nodeHandle: Long) {
        //do something
    }
}
