package com.example.myhealthlife.data.local.ble.sleep;

import com.yucheng.ycbtsdk.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SleepHistoryMapper {

    public static List<SleepSession> map(HashMap<String, Object> response) {
        List<SleepSession> sessions = new ArrayList<>();

        ArrayList<HashMap<String, Object>> data =
                (ArrayList<HashMap<String, Object>>) response.get("data");

        if (data == null) return sessions;

        for (HashMap<String, Object> item : data) {

            List<SleepSegment> segments = mapSegments(
                    (ArrayList<HashMap<String, Object>>) item.get("sleepData")
            );

            SleepSession session = new SleepSession(
                    (Long) item.get("startTime"),
                    (Long) item.get("endTime"),
                    (Integer) item.get("wakeCount"),
                    (Integer) item.get("wakeDuration"),
                    (Integer) item.get("deepSleepTotal"),
                    (Integer) item.get("lightSleepTotal"),
                    (Integer) item.get("rapidEyeMovementTotal"),
                    segments
            );

            sessions.add(session);
        }

        return sessions;
    }

    private static List<SleepSegment> mapSegments(
            ArrayList<HashMap<String, Object>> sleepData
    ) {
        List<SleepSegment> segments = new ArrayList<>();
        if (sleepData == null) return segments;

        for (HashMap<String, Object> segment : sleepData) {
            segments.add(
                    new SleepSegment(
                            (Long) segment.get("sleepStartTime"),
                            (Integer) segment.get("sleepLen"),
                            (Constants.SleepType) segment.get("sleepType")
                    )
            );
        }
        return segments;
    }


}

