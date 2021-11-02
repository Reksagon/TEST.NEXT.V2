package test.next.ui.changes

import android.app.Activity
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.awesomedialog.*
import test.next.R
import test.next.constant.AccountConst
import test.next.ui.home.HomeFragment

class DialogDelete {
    companion object {

        fun getDialog(activity: Activity, name: String, id: Int, adapter: ChangeAdapter) {
            AwesomeDialog.build(activity)
                .title(
                    activity.getString(R.string.delete) + " " + name + "?"
                )
                .body("", color = ContextCompat.getColor(activity, android.R.color.white))
                .icon(R.drawable.delete)
                .onPositive(activity.getString(R.string.delete_usually)) {
                    AccountConst.deleteSchedule(id, adapter)
                }
                .onNegative(activity.getString(R.string.cancel)) {
                }
                .create()
        }
    }
}


