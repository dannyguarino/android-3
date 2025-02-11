package mega.privacy.android.app.activities.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import mega.privacy.android.app.lollipop.FileExplorerActivityLollipop
import mega.privacy.android.app.utils.Constants.INTENT_EXTRA_KEY_COPY_FROM
import mega.privacy.android.app.utils.Constants.INTENT_EXTRA_KEY_COPY_HANDLES
import mega.privacy.android.app.utils.Constants.INTENT_EXTRA_KEY_COPY_TO
import nz.mega.sdk.MegaApiJava.INVALID_HANDLE

class SelectFolderToCopyActivityContract : ActivityResultContract<LongArray, Pair<LongArray, Long>>() {

    override fun createIntent(context: Context, nodeHandles: LongArray): Intent =
        Intent(context, FileExplorerActivityLollipop::class.java).apply {
            action = FileExplorerActivityLollipop.ACTION_PICK_COPY_FOLDER
            putExtra(INTENT_EXTRA_KEY_COPY_FROM, nodeHandles)
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<LongArray, Long>? =
        when {
            resultCode == Activity.RESULT_OK && intent?.extras != null -> {
                Pair(
                    intent.getLongArrayExtra(INTENT_EXTRA_KEY_COPY_HANDLES) ?: longArrayOf(),
                    intent.getLongExtra(INTENT_EXTRA_KEY_COPY_TO, INVALID_HANDLE)
                )
            }
            else -> null
        }
}
