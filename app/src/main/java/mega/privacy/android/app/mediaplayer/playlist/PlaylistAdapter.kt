package mega.privacy.android.app.mediaplayer.playlist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import mega.privacy.android.app.R
import mega.privacy.android.app.databinding.ItemAudioPlaylistBinding
import mega.privacy.android.app.databinding.ItemVideoPlaylistBinding

/**
 * RecyclerView adapter for playlist screen.
 * @param context Context
 * @param itemOperation PlaylistItemOperation
 * @param isAudioPlayer Whether is the audio player
 * @param paused Whether is paused
 * @param dragStartListener DragStartListener
 */
class PlaylistAdapter(
    private val context: Context,
    private val itemOperation: PlaylistItemOperation,
    private val isAudioPlayer: Boolean,
    var paused: Boolean = false,
    private val dragStartListener: DragStartListener
) : ListAdapter<PlaylistItem, PlaylistViewHolder>(PlaylistItemDiffCallback()) {
    companion object {
        const val ANIMATION_DURATION = 250L
    }

    private var playingPosition: Int = 0

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return if (isAudioPlayer) {
            AudioPlaylistItemHolder(
                ItemAudioPlaylistBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            VideoPlaylistItemHolder(
                ItemVideoPlaylistBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        //Added the touch listener for reorder icon to implement drag feature
        holder.itemView.findViewById<ImageView>(R.id.transfers_list_option_reorder)
            .setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onDragStarted(holder)
                }
                false
            }
        val playlistItem = getItem(holder.absoluteAdapterPosition)
        val currentPosition = holder.absoluteAdapterPosition

        if (playlistItem.type == PlaylistItem.TYPE_PLAYING) {
            playingPosition = holder.absoluteAdapterPosition
        }

        holder.itemView.findViewById<FrameLayout>(R.id.header_layout).isVisible =
            playlistItem.headerIsVisible
        holder.itemView.findViewById<FrameLayout>(R.id.next_layout).isVisible =
            currentPosition != itemCount - 1 && playlistItem.type == PlaylistItem.TYPE_PLAYING

        holder.bind(paused, playlistItem, itemOperation, holder, currentPosition)
    }

    /**
     * Start the animation
     * @param holder the holder that adds the animation
     * @param position the position of item that adds the animation
     */
    fun startAnimation(holder: PlaylistViewHolder, position: Int) {
        if (holder is AudioPlaylistItemHolder || holder is VideoPlaylistItemHolder) {
            val flipAnimation = AnimationUtils.loadAnimation(context, R.anim.multiselect_flip)
            flipAnimation.duration = ANIMATION_DURATION
            flipAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    notifyItemChanged(position)
                }

                override fun onAnimationEnd(animation: Animation) {
                    notifyItemChanged(position)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            holder.itemView.findViewById<ImageView>(R.id.image_selected)
                .startAnimation(flipAnimation)
        }
    }

    /**
     * Get the position of playing item
     * @return the position of playing item
     */
    fun getPlayingPosition() = playingPosition
}
