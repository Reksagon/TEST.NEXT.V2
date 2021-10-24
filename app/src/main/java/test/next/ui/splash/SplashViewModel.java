package test.next.ui.splash;

import android.app.Activity;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import test.next.R;
import test.next.constant.AccountConst;
import test.next.constant.Shifts;

public class SplashViewModel extends ViewModel {
    Activity activity;
    private MutableLiveData<Task<DataSnapshot>> dataSnapshotMutableLiveData;
    private MutableLiveData<OnCompleteListener<DataSnapshot>> onCompleteListenerMutableLiveData;

    public MutableLiveData<Task<DataSnapshot>> getDataSnapshotMutableLiveData() {
        return dataSnapshotMutableLiveData;
    }

    public MutableLiveData<OnCompleteListener<DataSnapshot>> getOnCompleteListenerMutableLiveData() {
        return onCompleteListenerMutableLiveData;
    }

    void setSetting(Activity activity)
    {
        this.activity = activity;
        dataSnapshotMutableLiveData = new MutableLiveData<>();
        onCompleteListenerMutableLiveData = new MutableLiveData<>();


    }

    public SplashViewModel() {


    }
}