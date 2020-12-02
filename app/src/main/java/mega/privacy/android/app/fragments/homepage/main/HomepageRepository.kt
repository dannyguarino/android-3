package mega.privacy.android.app.fragments.homepage.main

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.Pair
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mega.privacy.android.app.MegaApplication
import mega.privacy.android.app.R
import mega.privacy.android.app.listeners.BaseListener
import mega.privacy.android.app.utils.AvatarUtil
import mega.privacy.android.app.utils.AvatarUtil.getCircleAvatar
import mega.privacy.android.app.utils.AvatarUtil.getColorAvatar
import mega.privacy.android.app.utils.CacheFolderManager
import mega.privacy.android.app.utils.Constants
import nz.mega.sdk.*
import javax.inject.Inject

class HomepageRepository @Inject constructor(
    private val megaApi: MegaApiAndroid,
    private val megaChatApi: MegaChatApiAndroid,
    @ApplicationContext private val context: Context
) {

    private val bannerList = MutableLiveData<MutableList<MegaBanner>?>()

    fun getBannerListLiveData(): MutableLiveData<MutableList<MegaBanner>?> {
        return bannerList
    }

    fun getChatStatusDrawableId(status: Int) = when (status) {
        MegaChatApi.STATUS_ONLINE -> R.drawable.ic_online
        MegaChatApi.STATUS_AWAY -> R.drawable.ic_away
        MegaChatApi.STATUS_BUSY -> R.drawable.ic_busy
        MegaChatApi.STATUS_OFFLINE -> R.drawable.ic_offline
        else -> 0
    }

    suspend fun getDefaultAvatar(): Bitmap = withContext(Dispatchers.IO) {
        AvatarUtil.getDefaultAvatar(
            getColorAvatar(megaApi.myUser), megaChatApi.myFullname, Constants.AVATAR_SIZE, true
        )
    }

    suspend fun loadAvatar(): Pair<Boolean, Bitmap>? = withContext(Dispatchers.IO) {
        getCircleAvatar(context, megaApi.myEmail)
    }

    suspend fun createAvatar(listener: BaseListener) = withContext(Dispatchers.IO) {
        megaApi.getUserAvatar(
            megaApi.myUser,
            CacheFolderManager.buildAvatarFile(context, megaApi.myEmail + ".jpg").absolutePath,
            listener
        )
    }

    suspend fun loadBannerList() = withContext(Dispatchers.IO) {
        megaApi.getBanners(object : BaseListener(MegaApplication.getInstance()) {
            override fun onRequestFinish(
                api: MegaApiJava,
                request: MegaRequest,
                e: MegaError
            ) {
                Log.i("Alex", "error:${e.errorString}")
                if (e.errorCode == MegaError.API_OK) {
                    Log.i("Alex", "bannerlistsize:${request.megaBannerList.size()}")
                    bannerList.value = MegaUtilsAndroid.bannersToArray(request.megaBannerList)
                } else if (e.errorCode == MegaError.API_ENOENT) {
                    bannerList.value = null
                }
            }
        })
    }

    fun isRootNodeNull() = (megaApi.rootNode == null)

    fun dismissBanner(id: Int) {
        megaApi.dismissBanner(id)
    }
}