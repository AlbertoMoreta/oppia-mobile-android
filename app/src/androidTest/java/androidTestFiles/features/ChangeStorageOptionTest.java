package androidTestFiles.features;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import org.digitalcampus.mobile.learning.R;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.listener.MoveStorageListener;
import org.digitalcampus.oppia.task.ChangeStorageOptionTask;
import org.digitalcampus.oppia.task.result.BasicResult;
import org.digitalcampus.oppia.utils.storage.ExternalStorageState;
import org.digitalcampus.oppia.utils.storage.Storage;
import org.digitalcampus.oppia.utils.storage.StorageAccessStrategyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

import androidx.preference.PreferenceManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ChangeStorageOptionTest {
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private Context context;
    private SharedPreferences prefs;
    private CountDownLatch signal;
    private BasicResult resultResponse;

    @Mock ExternalStorageState externalStorageState;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        signal = new CountDownLatch(1);
        resultResponse = null;
    }

    @After
    public void tearDown() throws Exception {
        signal.countDown();
    }

    /*
    @Test
    public void fromInternalToExternal_success() throws Exception {

        Storage.setStorageStrategy(StorageAccessStrategyFactory.createStrategy(PrefsActivity.STORAGE_OPTION_INTERNAL));
        Storage.createFolderStructure(context);

        ChangeStorageOptionTask task = new ChangeStorageOptionTask(context);

        String storageOption = PrefsActivity.STORAGE_OPTION_EXTERNAL;
        task.setMoveStorageListener(new MoveStorageListener() {
            @Override
            public void moveStorageComplete(BasicResult result) {
                resultResponse = result;
                signal.countDown();
            }

            @Override
            public void moveStorageProgressUpdate(String s) {

            }
        });
        task.execute(storageOption);

        signal.await();

        assertTrue(resultResponse.isSuccess());
        assertEquals(PrefsActivity.STORAGE_OPTION_EXTERNAL, prefs.getString(PrefsActivity.PREF_STORAGE_OPTION, ""));
    }
    */



    //Is Storage Available Tests
    @Test
    public void fromInternalToExternal_storageNotAvailable_mediaRemoved() throws Exception {

        ExternalStorageState state = Mockito.mock(ExternalStorageState.class);  //Mock ExternalStorageState object
        ExternalStorageState.setExternalStorageState(state);    //Inject mocked object in ExternalStorageState class
        when(state.getExternalStorageState(any())).thenReturn(Environment.MEDIA_REMOVED);    //Provide mocked behaviour

        Storage.setStorageStrategy(StorageAccessStrategyFactory.createStrategy(PrefsActivity.STORAGE_OPTION_INTERNAL));
        Storage.createFolderStructure(context);

        ChangeStorageOptionTask task = new ChangeStorageOptionTask(context);

        String storageOption = PrefsActivity.STORAGE_OPTION_EXTERNAL;
        task.setMoveStorageListener(new MoveStorageListener() {
            @Override
            public void moveStorageComplete(BasicResult result) {
                resultResponse = result;
                signal.countDown();
            }

            @Override
            public void moveStorageProgressUpdate(String s) {

            }
        });
        task.execute(storageOption);

        signal.await();

        assertFalse(resultResponse.isSuccess()); 
        assertEquals(context.getString(R.string.error_sdcard), resultResponse.getResultMessage());

        ExternalStorageState.setExternalStorageState(new ExternalStorageState());   //Replace mocked object
    }
    @Test
    public void fromInternalToExternal_storageNotAvailable_mediaUnmountable() throws Exception {

        ExternalStorageState state = Mockito.mock(ExternalStorageState.class);  //Mock ExternalStorageState object
        ExternalStorageState.setExternalStorageState(state);    //Inject mocked object in ExternalStorageState class
        when(state.getExternalStorageState(any())).thenReturn(Environment.MEDIA_UNMOUNTABLE);    //Provide mocked behaviour

        Storage.setStorageStrategy(StorageAccessStrategyFactory.createStrategy(PrefsActivity.STORAGE_OPTION_INTERNAL));
        Storage.createFolderStructure(context);

        ChangeStorageOptionTask task = new ChangeStorageOptionTask(context);

        String storageOption = PrefsActivity.STORAGE_OPTION_EXTERNAL;
        task.setMoveStorageListener(new MoveStorageListener() {
            @Override
            public void moveStorageComplete(BasicResult result) {
                resultResponse = result;
                signal.countDown();
            }

            @Override
            public void moveStorageProgressUpdate(String s) {

            }
        });
        task.execute(storageOption);

        signal.await();

        assertFalse(resultResponse.isSuccess());
        assertEquals(context.getString(R.string.error_sdcard), resultResponse.getResultMessage());

        ExternalStorageState.setExternalStorageState(new ExternalStorageState());   //Replace mocked object
    }
    @Test
    public void fromInternalToExternal_storageNotAvailable_mediaUnmounted() throws Exception {

        ExternalStorageState state = Mockito.mock(ExternalStorageState.class);  //Mock ExternalStorageState object
        ExternalStorageState.setExternalStorageState(state);    //Inject mocked object in ExternalStorageState class
        when(state.getExternalStorageState(any())).thenReturn(Environment.MEDIA_UNMOUNTED);    //Provide mocked behaviour

        Storage.setStorageStrategy(StorageAccessStrategyFactory.createStrategy(PrefsActivity.STORAGE_OPTION_INTERNAL));
        Storage.createFolderStructure(context);

        ChangeStorageOptionTask task = new ChangeStorageOptionTask(context);

        String storageOption = PrefsActivity.STORAGE_OPTION_EXTERNAL;
        task.setMoveStorageListener(new MoveStorageListener() {
            @Override
            public void moveStorageComplete(BasicResult result) {
                resultResponse = result;
                signal.countDown();
            }

            @Override
            public void moveStorageProgressUpdate(String s) {

            }
        });
        task.execute(storageOption);

        signal.await();

        assertFalse(resultResponse.isSuccess());
        assertEquals(context.getString(R.string.error_sdcard), resultResponse.getResultMessage());

        ExternalStorageState.setExternalStorageState(new ExternalStorageState());   //Replace mocked object
    }


    @Test
    public void fromInternalToExternal_storageNotAvailable_mediaMountedReadOnly() throws Exception {

        ExternalStorageState state = Mockito.mock(ExternalStorageState.class);  //Mock ExternalStorageState object
        ExternalStorageState.setExternalStorageState(state);    //Inject mocked object in ExternalStorageState class
        when(state.getExternalStorageState(any())).thenReturn(Environment.MEDIA_MOUNTED_READ_ONLY);    //Provide mocked behaviour

        Storage.setStorageStrategy(StorageAccessStrategyFactory.createStrategy(PrefsActivity.STORAGE_OPTION_INTERNAL));
        Storage.createFolderStructure(context);

        ChangeStorageOptionTask task = new ChangeStorageOptionTask(context);

        String storageOption = PrefsActivity.STORAGE_OPTION_EXTERNAL;
        task.setMoveStorageListener(new MoveStorageListener() {
            @Override
            public void moveStorageComplete(BasicResult result) {
                resultResponse = result;
                signal.countDown();
            }

            @Override
            public void moveStorageProgressUpdate(String s) {

            }
        });
        task.execute(storageOption);

        signal.await();

        assertFalse(resultResponse.isSuccess());
        assertEquals(context.getString(R.string.error_sdcard), resultResponse.getResultMessage());

        ExternalStorageState.setExternalStorageState(new ExternalStorageState());   //Replace mocked object
    }
    @Test
    public void fromInternalToExternal_storageNotAvailable_mediaShared() throws Exception {

        ExternalStorageState state = Mockito.mock(ExternalStorageState.class);  //Mock ExternalStorageState object
        ExternalStorageState.setExternalStorageState(state);    //Inject mocked object in ExternalStorageState class
        when(state.getExternalStorageState(any())).thenReturn(Environment.MEDIA_SHARED);    //Provide mocked behaviour

        Storage.setStorageStrategy(StorageAccessStrategyFactory.createStrategy(PrefsActivity.STORAGE_OPTION_INTERNAL));
        Storage.createFolderStructure(context);

        ChangeStorageOptionTask task = new ChangeStorageOptionTask(context);

        String storageOption = PrefsActivity.STORAGE_OPTION_EXTERNAL;
        task.setMoveStorageListener(new MoveStorageListener() {
            @Override
            public void moveStorageComplete(BasicResult result) {
                resultResponse = result;
                signal.countDown();
            }

            @Override
            public void moveStorageProgressUpdate(String s) {

            }
        });
        task.execute(storageOption);

        signal.await();

        assertFalse(resultResponse.isSuccess());
        assertEquals(context.getString(R.string.error_sdcard), resultResponse.getResultMessage());

        ExternalStorageState.setExternalStorageState(new ExternalStorageState());   //Replace mocked object
    }


    @Test
    public void fromExternalToExternal_moveFilesToSameLocationFailure() throws Exception {

        Storage.setStorageStrategy(StorageAccessStrategyFactory.createStrategy(PrefsActivity.STORAGE_OPTION_EXTERNAL));
        Storage.createFolderStructure(context);

        ChangeStorageOptionTask task = new ChangeStorageOptionTask(context);

        String storageOption = PrefsActivity.STORAGE_OPTION_EXTERNAL;
        task.setMoveStorageListener(new MoveStorageListener() {
            @Override
            public void moveStorageComplete(BasicResult result) {
                resultResponse = result;
                signal.countDown();
            }

            @Override
            public void moveStorageProgressUpdate(String s) {

            }
        });
        task.execute(storageOption);

        signal.await();

        //Expected to fail when moving to the same location
        assertFalse(resultResponse.isSuccess());
        assertEquals(PrefsActivity.STORAGE_OPTION_EXTERNAL, prefs.getString(PrefsActivity.PREF_STORAGE_OPTION, ""));
    }

}
