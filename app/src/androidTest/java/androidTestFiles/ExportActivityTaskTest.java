package androidTestFiles;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.digitalcampus.oppia.model.QuizAttempt;
import org.digitalcampus.oppia.model.TrackerLog;
import org.digitalcampus.oppia.task.ExportActivityTask;
import org.digitalcampus.oppia.utils.storage.FileUtils;
import org.digitalcampus.oppia.utils.storage.Storage;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidTestFiles.database.BaseTestDB;

@RunWith(AndroidJUnit4.class)
public class ExportActivityTaskTest extends BaseTestDB {

    private static final int NUM_QUIZ_ATTEMPTS_TEST = 2;
    private static final int NUM_TRACKER_TEST = 3;

    @After
    public void cleanActivityFiles() throws Exception {

        File activityPath = new File(Storage.getActivityPath(getContext()));
        for (String filename : activityPath.list()) {
            File file = new File(activityPath, filename);
            file.delete();
        }
    }


    @Test
    public void exportUnexportedActivity() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        getTestDataManager().addQuizAttempts(NUM_QUIZ_ATTEMPTS_TEST);
        getTestDataManager().addTrackers(NUM_TRACKER_TEST);

        List<QuizAttempt> quizAttemptsBefore = getDbHelper().getUnexportedQuizAttempts(1);
        assertEquals(NUM_QUIZ_ATTEMPTS_TEST, quizAttemptsBefore.size());

        List<TrackerLog> trackersBefore = getDbHelper().getUnexportedTrackers(1);
        assertEquals(NUM_TRACKER_TEST, trackersBefore.size());

        final String[] exportedFilename = new String[1];

        ExportActivityTask task = new ExportActivityTask(getContext());
        task.setListener(filename -> {
            exportedFilename[0] = filename;
            signal.countDown();
        });
        task.execute(ExportActivityTask.UNEXPORTED_ACTIVITY);

        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkJsonFile(exportedFilename[0]);

        List<QuizAttempt> quizAttemptsAfter = getDbHelper().getUnexportedQuizAttempts(1);
        assertEquals(0, quizAttemptsAfter.size());

        List<TrackerLog> trackersAfter = getDbHelper().getUnexportedTrackers(1);
        assertEquals(0, trackersAfter.size());

    }

    @Test
    public void exportFullActivityUnexported() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        getTestDataManager().addQuizAttempts(NUM_QUIZ_ATTEMPTS_TEST);
        getTestDataManager().addTrackers(NUM_TRACKER_TEST);

        List<QuizAttempt> quizAttemptsBefore = getDbHelper().getUnexportedQuizAttempts(1);
        assertEquals(NUM_QUIZ_ATTEMPTS_TEST, quizAttemptsBefore.size());

        List<TrackerLog> trackersBefore = getDbHelper().getUnexportedTrackers(1);
        assertEquals(NUM_TRACKER_TEST, trackersBefore.size());

        final String[] exportedFilename = new String[1];

        ExportActivityTask task = new ExportActivityTask(getContext());
        task.setListener(filename -> {
            exportedFilename[0] = filename;
            signal.countDown();
        });
        task.execute(ExportActivityTask.FULL_EXPORT_ACTIVTY);

        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkJsonFile(exportedFilename[0]);

        List<QuizAttempt> quizAttemptsAfter = getDbHelper().getUnexportedQuizAttempts(1);
        assertEquals(NUM_QUIZ_ATTEMPTS_TEST, quizAttemptsAfter.size());

        List<TrackerLog> trackersAfter = getDbHelper().getUnexportedTrackers(1);
        assertEquals(NUM_TRACKER_TEST, trackersAfter.size());

    }

    @Test
    public void exportFullActivityAlreadyExported() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        getTestDataManager().addQuizAttempts(NUM_QUIZ_ATTEMPTS_TEST);
        getTestDataManager().addTrackers(NUM_TRACKER_TEST);

        List<QuizAttempt> quizAttemptsBefore = getDbHelper().getUnexportedQuizAttempts(1);
        assertEquals(NUM_QUIZ_ATTEMPTS_TEST, quizAttemptsBefore.size());

        List<TrackerLog> trackersBefore = getDbHelper().getUnexportedTrackers(1);
        assertEquals(NUM_TRACKER_TEST, trackersBefore.size());

        getDbHelper().markLogsAndQuizzesExported();
        getDbHelper().markLogsAndQuizzesSubmitted();

        List<QuizAttempt> quizAttemptsBefore2 = getDbHelper().getUnexportedQuizAttempts(1);
        assertEquals(0, quizAttemptsBefore2.size());

        List<TrackerLog> trackersBefore2 = getDbHelper().getUnexportedTrackers(1);
        assertEquals(0, trackersBefore2.size());

        final String[] exportedFilename = new String[1];

        ExportActivityTask task = new ExportActivityTask(getContext());
        task.setListener(filename -> {
            exportedFilename[0] = filename;
            signal.countDown();
        });
        task.execute(ExportActivityTask.FULL_EXPORT_ACTIVTY);

        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkJsonFile(exportedFilename[0]);

        List<QuizAttempt> quizAttemptsAfter = getDbHelper().getUnexportedQuizAttempts(1);
        assertEquals(0, quizAttemptsAfter.size());

        List<TrackerLog> trackersAfter = getDbHelper().getUnexportedTrackers(1);
        assertEquals(0, trackersAfter.size());

    }

    private void checkJsonFile(String exportedFilename) throws Exception {

        File activityPath = new File(Storage.getActivityPath(getContext()));
        File exportedActivityFile = new File(activityPath, exportedFilename);
        assertTrue(exportedActivityFile.exists());
        String contentJson = FileUtils.readFile(exportedActivityFile);
        JSONObject jsonObject = new JSONObject(contentJson);
        JSONObject jsonUser1 = jsonObject.getJSONArray("users").getJSONObject(0);
        assertEquals(NUM_QUIZ_ATTEMPTS_TEST, jsonUser1.getJSONArray("quizresponses").length());
        assertEquals(NUM_TRACKER_TEST, jsonUser1.getJSONArray("trackers").length());
    }

}
