<?php
define("_SERVER_", "https://runwalktracking.000webhostapp.com/");
define("APP_NAME", "Run/Walk Tracking");
define("APP", "app");
define("USER", "user");
define("FIRST_LOGIN", "first_login");
define("URL_NOT_VALID", "Url non valido");
define("FILTER_NOT_VALID", "Filtro non valido");
define("WORKOUTS", "workouts");
define("WORKOUT", "workout");
define("FILTER", "filter");

define("ID", "id");
define("ID_NOT_SET", "id non settato");
define("UNIT", "unit");
define("C_KEY","c_key");


define("MIDDLE_SPEEDS", "middle_speeds");
define("DISTANCES", "distances");
define("WEIGHTS", "weights");
define("UPDATE", "update");
define("DELETE", "delete");

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
define("IMG", "img_encode");
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



function bodyRequest(){
  return json_decode(file_get_contents('php://input'), true);
}

function hashed_password($password){
  return password_hash($password, PASSWORD_BCRYPT);
}

function current_date() {
  return date("Y-m-d H:i:s");
}

function formatDate($date){
  return  date("Y-m-d", strtotime($date));
}

function formatDateWithTime($dateTime){
  return  date("Y-m-d H:i:s", strtotime($dateTime));
}

function date_end_validity_link(){
  return date('Y-m-d H:i:s',strtotime('+30 minutes',strtotime(current_date())));
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


define('_EMAIL_REDIRECT_', 'marica.alex097@gmail.com');
function sendEmail($to_email, $subject, $body){
     $to_email = _EMAIL_REDIRECT_; // TODO: COMMENTARE QUESTA RIGA o MOFICARE _EMAIL_REDIRECT_
     $headers = "Content-Type: text/html; charset=ISO-8859-1\r\n";
  	 error_reporting(E_ERROR | E_PARSE);
  	 $success = mail($to_email, APP_NAME ." - " . $subject, $body, $headers);
     if (!$success)  throw new Exception(error_get_last()['message']);
}


function json_errors($err) {
    return json_encode(array('Error'=> $err));
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

?>
