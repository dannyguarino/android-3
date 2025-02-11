package mega.privacy.android.app.lollipop.listeners;

import static mega.privacy.android.app.modalbottomsheet.UploadBottomSheetDialogFragment.GENERAL_UPLOAD;
import static mega.privacy.android.app.utils.Constants.SNACKBAR_TYPE;
import static mega.privacy.android.app.utils.LogUtil.logDebug;
import static mega.privacy.android.app.utils.MegaNodeDialogUtil.ACTION_BACKUP_FAB;
import static mega.privacy.android.app.utils.Util.isOnline;

import android.content.Context;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import mega.privacy.android.app.R;
import mega.privacy.android.app.lollipop.ContactFileListActivityLollipop;
import mega.privacy.android.app.lollipop.ManagerActivityLollipop;
import mega.privacy.android.app.utils.Util;

public class FabButtonListener implements FloatingActionButton.OnClickListener{

    Context context;
    ManagerActivityLollipop.DrawerItem drawerItem;

    public FabButtonListener(Context context){
        logDebug("FabButtonListener created");
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        logDebug("FabButtonListener");
        switch(v.getId()) {
            case R.id.floating_button: {
                logDebug("Floating Button click!");
                if(context instanceof ManagerActivityLollipop){
                    drawerItem = ((ManagerActivityLollipop)context).getDrawerItem();
                    switch (drawerItem){
                        case CLOUD_DRIVE:
                            logDebug("Cloud Drive SECTION");
                            if(!isOnline(context)){
                                if(context instanceof ManagerActivityLollipop){
                                    ((ManagerActivityLollipop) context).showSnackbar(SNACKBAR_TYPE, context.getString(R.string.error_server_connection_problem), -1);
                                }
                                return;
                            }
                            ((ManagerActivityLollipop)context).showUploadPanelForBackup(GENERAL_UPLOAD, ACTION_BACKUP_FAB);
                            break;
                        case SEARCH:
                        case SHARED_ITEMS:{
                            logDebug("Cloud Drive SECTION");
                            if(!isOnline(context)){
                                if(context instanceof ManagerActivityLollipop){
                                    ((ManagerActivityLollipop) context).showSnackbar(SNACKBAR_TYPE, context.getString(R.string.error_server_connection_problem), -1);
                                }
                                return;
                            }
                            ((ManagerActivityLollipop)context).showUploadPanel();
                            break;
                        }
                        case CHAT:{
                            logDebug("Create new chat");
                            if (!Util.isFastDoubleClick()) {
                                ((ManagerActivityLollipop) context).fabMainClickCallback();
                            }
                            break;
                        }
                    }
                }
                break;
            }
            case R.id.floating_button_contact_file_list:{
                if(!isOnline(context)){
                    if(context instanceof ContactFileListActivityLollipop){
                        ((ContactFileListActivityLollipop) context).showSnackbar(SNACKBAR_TYPE, context.getString(R.string.error_server_connection_problem));
                    }
                    return;
                }
                if(context instanceof ContactFileListActivityLollipop){
                    ((ContactFileListActivityLollipop)context).showUploadPanel();
                }
                break;
            }
        }
    }
}
