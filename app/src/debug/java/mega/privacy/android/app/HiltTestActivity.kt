@file:Suppress("DEPRECATION")
package mega.privacy.android.app

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import mega.privacy.android.app.lollipop.managerSections.settings.SettingsActivity
import nz.mega.sdk.MegaApiJava
import nz.mega.sdk.MegaError
import nz.mega.sdk.MegaRequest
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class HiltTestActivity : AppCompatActivity(), SettingsActivity {

    @Inject
    lateinit var settingsProvider: Provider<SettingsActivity>

    override fun changeAppBarElevation(canScrollVertically: Boolean) =
        settingsProvider.get().changeAppBarElevation(canScrollVertically)

    override fun askConfirmationDeleteAccount() =
        settingsProvider.get().askConfirmationDeleteAccount()

    override fun showConfirmationEnableLogsKarere() =
        settingsProvider.get().showConfirmationEnableLogsKarere()

    override fun showConfirmationEnableLogsSDK() =
        settingsProvider.get().showConfirmationEnableLogsSDK()

    override fun showSnackbar(snackbarType: Int, string: String, megachatInvalidHandle: Long) =
        settingsProvider.get().showSnackbar(snackbarType, string, megachatInvalidHandle)

    override val is2FAEnabled: Boolean
        get() = settingsProvider.get().is2FAEnabled
    override var openSettingsStartScreen: Boolean
        get() = settingsProvider.get().openSettingsStartScreen
        set(value) {
            settingsProvider.get().openSettingsStartScreen = value
        }
    override val openSettingsQR: Boolean
        get() = settingsProvider.get().openSettingsQR
    override val openSettingsStorage: Boolean
        get() = settingsProvider.get().openSettingsStorage

    override fun onRequestStart(api: MegaApiJava?, request: MegaRequest?) =
        settingsProvider.get().onRequestStart(api, request)

    override fun onRequestUpdate(api: MegaApiJava?, request: MegaRequest?) =
        settingsProvider.get().onRequestUpdate(api, request)

    override fun onRequestFinish(api: MegaApiJava?, request: MegaRequest?, e: MegaError?) =
        settingsProvider.get().onRequestFinish(api, request, e)

    override fun onRequestTemporaryError(api: MegaApiJava?, request: MegaRequest?, e: MegaError?) =
        settingsProvider.get().onRequestTemporaryError(api, request, e)
}

@Module
@InstallIn(ActivityComponent::class)
class  TestActivityModule{
    @Provides
    fun provideSettingsActivity(): SettingsActivity = object : SettingsActivity{
        override fun changeAppBarElevation(canScrollVertically: Boolean) {}

        override fun askConfirmationDeleteAccount() {}

        override fun showConfirmationEnableLogsKarere() {}

        override fun showConfirmationEnableLogsSDK() {}

        override fun showSnackbar(snackbarType: Int, string: String, megachatInvalidHandle: Long) {}

        override val is2FAEnabled: Boolean
            get() = false
        override var openSettingsStartScreen: Boolean
            get() = false
            set(_) {}
        override val openSettingsQR: Boolean
            get() = false
        override val openSettingsStorage: Boolean
            get() = false

        override fun onRequestStart(api: MegaApiJava?, request: MegaRequest?) {}

        override fun onRequestUpdate(api: MegaApiJava?, request: MegaRequest?) {}

        override fun onRequestFinish(api: MegaApiJava?, request: MegaRequest?, e: MegaError?) {}

        override fun onRequestTemporaryError(
            api: MegaApiJava?,
            request: MegaRequest?,
            e: MegaError?
        ) {}
    }
}