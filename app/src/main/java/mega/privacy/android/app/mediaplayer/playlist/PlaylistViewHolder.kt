package mega.privacy.android.app.mediaplayer.playlist

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Base class for ViewHolder for PlaylistItem.
 * @param binding ViewDataBinding
 */
abstract class PlaylistViewHolder(binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(
        paused: Boolean,
        item: PlaylistItem,
        itemOperation: PlaylistItemOperation,
        holder: PlaylistViewHolder,
        position: Int
    )
}
