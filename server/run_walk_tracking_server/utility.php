<?php
//define("_SERVER_", "https://runwalktracking.000webhostapp.com/");
define("_SERVER_", "http://192.168.1.132/run_walk_tracking_server/");

define("APP_NAME", "Run/Walk Tracking");

/* --- Index --- */

define("CONSISTENT", "consistent");
define("NO_CONSISTENT_SEND_DATA", "no consistent : send data to server");
define("NO_CONSISTENT_RECEIVE_DATA", "no consistent : receive data from server");

define("DEVICE", "device");
define("DB_EXIST", "db_exist");
define("SUCCESS", "success");
define("USER", "user");
define("DATA", "data");
define("LAST_UPDATE", "last_update");
define("ERROR", "Error");
define("STATE", "state");
define("CODE", "code");
define("DESCRIPTION", "description");
define("SESSION", "session");
define("FIRST_LOGIN", "first_login");
define("WORKOUTS", "workouts");
define("WORKOUT", "workout");
define("FILTER", "filter");
define("SIGN_UP", "sign_up");
define("ID", "id");
define("UNIT", "unit");
define("C_KEY","c_key");
define("KEY","key");
define("EXPIRY_DATE","expiry_date");
define("MIDDLE_SPEEDS", "middle_speeds");
define("DISTANCES", "distances");
define("WEIGHTS", "weights");
define("INSERT", "insert");
define("UPDATE", "update");
define("DELETE", "delete");

define("OLD_PASSWORD", "old_password");
define("NEW_PASSWORD", "new_password");
//User
define("ID_USER", "id_user");
define("NAME", "name");
define("LAST_NAME", "last_name");
define("GENDER", "gender");
define("BIRTH_DATE", "birth_date");
define("EMAIL", "email");
define("CITY", "city");
define("PHONE", "phone");
define("HEIGHT", "height");
define("TARGET", "target");
define("WEIGHT", "weight");

define("IMG", "image");
define("IMG_ENCODE", "content");
define("IMG_NAME", "name");

define("USERNAME", "username");
define("PASSWORD", "password");
define("TOKEN", "token");
define("REQUEST_PASSWORD_FORGOT_SEND", "request_password_forgot_send");
// Workouts
define("ID_WORKOUT", "id_workout");
define("MAP_ROUTE", "map_route");
define("DATE", "date");
define("DURATION", "duration");
define("DISTANCE", "distance");
define("CALORIES", "calories");
define("MIDDLE_SPEED", "middle_speed");
define("ID_SPORT", "id_sport");
// Weight
define("ID_WEIGHT", "id_weight");
define("VALUE", "value");
// Settings
define("SETTINGS", "settings");
define("SPORT", "sport");
define("UNIT_MEASURE", "unit_measure");
define("ENERGY", "energy");
define("UNIT_DISTANCE", "unit_distance");
define("UNIT_WEIGHT", "unit_weight");
define("UNIT_HEIGHT", "unit_height");


/* UTILITY SESSION--- */
define("SEPARATOR", ".");

function encode($val){
  return base64_encode(base64_encode($val));
}

function decode($val){
  return base64_decode(base64_decode($val));
}

function cast($strNum) {
  return is_numeric($strNum) ? intval($strNum): $strNum;
}

function getEncodedSession($session){
  return encode(implode(SEPARATOR, array(encode($session[ID_USER]), encode($session[TOKEN]),
                                         encode($session[LAST_UPDATE]), encode($session[DEVICE]))));
}

function getDecodedSession($token_session){
  return array_combine(array(ID_USER, TOKEN, LAST_UPDATE, DEVICE),
                       array_map(function($value){
                                   return cast(decode($value));
                       }, explode(SEPARATOR, decode($token_session))));
}

/* FINE --- */

function hashed_password($password){
  return password_hash($password, PASSWORD_BCRYPT);
}

function current_datetime() {
  return date("Y-m-d H:i:s");
}

function current_unixdatetime() {
  return strtotime(current_datetime());
}

function formatDate($date){
  return  date("Y-m-d", strtotime($date));
}

function date_end_validity_link(){
  return date('Y-m-d H:i:s',strtotime('+24 hours',strtotime(current_datetime())));
}

function docHtml($url){
      $doc = new DOMDocument();
      $doc->validateOnParse = true;
      libxml_use_internal_errors(true);
      $success=$doc->loadHTMLFile($url);
      if(!$success)
         die("<div class='mx-5 mt-5 px-5 pt-5'><strong>Documento " . $url . " non è stato caricato.</strong></div>");

      return $doc;
 }

//define('_EMAIL_REDIRECT_', 'marica.alex097@gmail.com');
define('_EMAIL_REDIRECT_', 'email_redirect@localhost.com');
function sendEmail($to_email, $subject, $body){
     $to_email = _EMAIL_REDIRECT_; // TODO: COMMENTARE QUESTA RIGA o MOFICARE _EMAIL_REDIRECT_
     $headers = "Content-Type: text/html; charset=ISO-8859-1\r\n";
  	 error_reporting(E_ERROR | E_PARSE);
  	 $success = mail($to_email, APP_NAME ." - " . $subject, $body, $headers);
     if (!$success)  throw new Exception(error_get_last()['message']);
}

function json_errors($ex) {
    return json_encode(array(ERROR=> array(CODE => $ex->getCode(), DESCRIPTION=> $ex->getMessage())));
}

function getToken($length){
     $token = "";
     $codeAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
     $codeAlphabet.= "abcdefghijklmnopqrstuvwxyz";
     $codeAlphabet.= "0123456789";
     $max = strlen($codeAlphabet); // edited

    for ($i=0; $i < $length; $i++) {
        $token .= $codeAlphabet[random_int(0, $max-1)];
    }
    return $token;
}

require_once("Exceptions.php");
require_once("Request.php");
?>
