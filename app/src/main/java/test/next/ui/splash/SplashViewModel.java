package test.next.ui.splash;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import test.next.constant.AccountConst;
import test.next.constant.Shifts;

public class SplashViewModel extends ViewModel {
    private MutableLiveData<Task<DataSnapshot>> dataSnapshotMutableLiveData;
    private MutableLiveData<OnCompleteListener<DataSnapshot>> onCompleteListenerMutableLiveData;

    public MutableLiveData<Task<DataSnapshot>> getDataSnapshotMutableLiveData() {
        return dataSnapshotMutableLiveData;
    }

    public MutableLiveData<OnCompleteListener<DataSnapshot>> getOnCompleteListenerMutableLiveData() {
        return onCompleteListenerMutableLiveData;
    }

    public SplashViewModel() throws InterruptedException {
        dataSnapshotMutableLiveData = new MutableLiveData<>();
        onCompleteListenerMutableLiveData = new MutableLiveData<>();


        Task<DataSnapshot> dataSnapshotTask =  FirebaseDatabase
                .getInstance("https://test-next-7ea45-default-rtdb.firebaseio.com/")
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
                            .setValue(new Shifts("TestName1", "TestStart1", "TestEnd1", "#eb3434"));
                    databaseReference.push()
                            .setValue(new Shifts("TestName2", "TestStart2", "TestEnd2", "#ebc634"));
                    databaseReference.push()
                            .setValue(new Shifts("TestName3", "TestStart3", "TestEnd3", "#96eb34"));
                    databaseReference.push()
                            .setValue(new Shifts("TestName4", "TestStart4", "TestEnd4", "#34ebba"));
                }
            }
        });

    }
}