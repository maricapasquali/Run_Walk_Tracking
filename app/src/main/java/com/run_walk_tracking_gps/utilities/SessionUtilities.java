package com.run_walk_tracking_gps.utilities;

import com.run_walk_tracking_gps.connectionserver.NetworkHelper;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SessionUtilities {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    private static String encode(Object i){
        return encoder.encodeToString(encoder.encode(String.valueOf(i).getBytes()));
    }

    private static String decode(Object i){
        return new String(decoder.decode(decoder.decode(String.valueOf(i).getBytes())));
    }

    public static String getEncodedSession(int id_user_value, String token_value, long last_update_value, String device_value ){

        String id_user = encode(id_user_value);
        String token = encode(token_value);
        String last_update = encode(last_update_value);
        String device = encode(device_value); // DEVICE_VALUE =  md5(md5(device)) in primo md5 applicato dal client

        List<String> session  = Arrays.asList(id_user, token, last_update, device);
        String session_string = String.join(".", session);
        return encode(session_string);
    }

    public static JSONObject getDecodedSession(String encodeSession) {
        String session_string = decode(encodeSession);
        String[] tmp_session_value = session_string.split("\\.");
        String[] tmp_session_key = Stream.of(NetworkHelper.Constant.ID_USER, NetworkHelper.Constant.TOKEN,
                NetworkHelper.Constant.LAST_UPDATE, NetworkHelper.Constant.DEVICE).toArray(String[]::new);
        Map<String, Object> session =
                IntStream.range(0, tmp_session_key.length).boxed().collect(
                        Collectors.toMap(i -> tmp_session_key[i], i -> tmp_session_value[i]));

        session.entrySet().forEach(e -> e.setValue(NumberUtilities.cast(decode(e.getValue()))));
        return new JSONObject(session);

    }
}
