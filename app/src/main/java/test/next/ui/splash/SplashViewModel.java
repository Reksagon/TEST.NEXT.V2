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


        Task<DataSnapshot> dataSnapshotTask =  FirebaseDatabase
                .getInstance(new String(Base64.decode(activity.getResources().getString(R.string.firebase), Base64.DEFAULT)))
                .getReference()
                .child("Users/").get();
        dataSnapshotMutableLiveData.setValue(dataSnapshotTask);

        onCompleteListenerMutableLiveData.setValue(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                boolean exist = false;
                for(DataSnapshot child : task.getResult().getChildren())
                {
                    if(child.getKey().toString().equals(AccountConst.account.getId()))
                        exist = true;
                }
                if(!exist)
                {
                    DatabaseReference databaseReference = FirebaseDatabase
                            .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
                            .getReference()
                            .child("Users/" + AccountConst.account.getId() + "/Shifts");
                    databaseReference.push()
                            .setValue(new Shifts(1,"День", "00:00", "00:00", "#fcba03", false));
                    databaseReference.push()
                            .setValue(new Shifts(2,"Ночь", "00:00", "00:00", "#0339fc", false));
                    databaseReference.push()
                            .setValue(new Shifts(3,"Выходной", "00:00", "00:00", "#00d4d0", true));
                }
            }
        });
    }

    public SplashViewModel() {


    }
}