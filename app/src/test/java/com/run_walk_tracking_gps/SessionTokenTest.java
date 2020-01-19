package com.run_walk_tracking_gps;

import java.util.Base64;
import com.run_walk_tracking_gps.utilities.NumberUtilities;
import com.run_walk_tracking_gps.utilities.SessionUtilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SessionTokenTest {

    private final static String ID_USER = "id_user";
    private final static int ID_USER_VALUE = 22;
    private final static String LAST_UPDATE = "last_update";
    private final static int LAST_UPDATE_VALUE = 1578167181;
    private final static String DEVICE = "device";
    private final static String DEVICE_VALUE = "ef62c1f173d2f81dc16779189c2dfdb6";
    private static final String TOKEN = "token";
    private static final String TOKEN_VALUE = "kq2sOan0e1umJO8azHDJb3LMwKXNLhxaxCYzhcqP7jRXHdYVm6J9fZ3x77HaP2fjV6mD4yXTSKk6kYS1lcrEwUqZLOv0VcYTxwY3";

    private static JSONObject session_decoded;

    private static String session_encoded;

    @Before
    public void setUp() throws JSONException {
        session_decoded = new JSONObject().put(ID_USER, ID_USER_VALUE)
                                         .put(TOKEN, TOKEN_VALUE)
                                         .put(LAST_UPDATE, LAST_UPDATE_VALUE)
                                         .put(DEVICE,DEVICE_VALUE);

        session_encoded = "VkZkd1NsQlJQVDB1V1ZST1JtVlhUWGRQVjJocFlXdEtjMVJXYUZka1JrNXlUMFJTV2xkSVFrcFZhMVozWVZVd2QyVkZOV3ROU0ZKYVZrZDBOR0l5VmtoU2FsSlNUVmQzTWxsVlpFOWxSbFpGV2toR1ZtSkhhRXBYYTFwelZqSktWVmRyZEZCV01YQm9WRlJPYmswd05IZGhSMmhXVWtWd2RGbFhlRnBOYlVwV1ZWUkNiRlp0YUZaV1ZFSXdZMnMxZEdSR2NGWmxhMXA2VjFST1MxSnRVWGhXYm1oWVlUTm9VVnBIY0VOV01XdDRZa1pXYkZOSFVtRlVXR001VUZFOVBTNVVWbEpXVFRBNVJWSlVTazlsYTFVd1ZGWkZPVkJSUFQwdVYyeGtXazFyTVhSVVdHaGhZV3RWZWxSVVNsSmxWbkJ4V2pOb1lWSXdNVFJVYlhCcVRUQTVWVkpVVWxCV01ERTFWMnRrWVdFeGJIRlhWREE5";
    }

    private String encodeSession;

    @Test
    public void encode() {
        encodeSession = SessionUtilities.getEncodedSession(ID_USER_VALUE, TOKEN_VALUE, LAST_UPDATE_VALUE, DEVICE_VALUE);

        Assert.assertEquals(encodeSession, session_encoded);
    }


    @After
    public void decode() {
        JSONObject decodedSession = SessionUtilities.getDecodedSession(encodeSession);

        Assert.assertEquals(decodedSession.toString(), session_decoded.toString());

    }
}
