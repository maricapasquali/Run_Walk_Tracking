<?php
//require_once("model/User.php");

define("SIGN_IN", "sign_in");
define("DOWNLOAD_IMAGE", "download_image");
define("CHANGE_PASSWORD", "change_password");
define("REQUEST_CHANGE_PASSWORD", "request_change_password");
define("SYNC", "sync");
define("_CONTINUE_", "continue");

class Request {

  public static function get(){
    return json_decode(file_get_contents('php://input'), true);
  }

  public static function getBody($of){

    switch ($of) {

      case SIGN_UP:
        $required = array(NAME, LAST_NAME, GENDER, BIRTH_DATE, EMAIL, CITY,
                          PHONE, HEIGHT, TARGET, WEIGHT, USERNAME, PASSWORD);
        break;

      case SIGN_IN:
        $required = array(USERNAME, PASSWORD);
        break;

      case FIRST_LOGIN:
        $required = array(USERNAME, PASSWORD, TOKEN, DEVICE);
        break;

      case DOWNLOAD_IMAGE:
        $required = array(TOKEN, IMG);
        break;

      case CHANGE_PASSWORD:
        $required = array(USERNAME, OLD_PASSWORD, TOKEN, NEW_PASSWORD);
        break;

      case REQUEST_CHANGE_PASSWORD:
        $required = array(EMAIL);
        break;

      case SYNC:
        $required = array(LAST_UPDATE, DB_EXIST, TOKEN, DEVICE);
        break;

      case _CONTINUE_:
        $required = array(SESSION);
        break;

      default:
          break;
    }
    if($of==INSERT || $of==UPDATE || $of==DELETE)
    {
      $required = array(TOKEN, LAST_UPDATE, FILTER, DATA);
    }
    $body = self::get();
    $keys = array_keys($body);
    foreach ($required as $key) {
      if(!in_array($key, $keys) || is_null($body[$key]))
        throw new UrlExeception();
    }
    return $body;
  }

  /**
  * return object
  */
  /*
  public static function getBody($of){
    $body = (object) self::get();
    switch ($of) {
      case SIGN_UP:
        return new User($body);

      case SIGN_IN:
        if(!isset($body->username) || !isset($body->password))
          throw new UrlExeception();
      break;

      case FIRST_LOGIN:
        if(!isset($body->username) || !isset($body->password) ||
           !isset($body->token) || !isset($body->device))
            throw new UrlExeception();
      break;

      case DOWNLOAD_IMAGE:
        if(!isset($body->token) || !isset($body->image))
          throw new UrlExeception();
      break;

      case CHANGE_PASSWORD:
        if(!isset($body->token) || !isset($body->username) ||
          !isset($body->old_password) || !isset($body->new_password))
            throw new UrlExeception();
      break;

      case REQUEST_CHANGE_PASSWORD:
        if(!isset($body->email))
          throw new UrlExeception();
      break;

      case SYNC:
        if(!isset($body->token)||
           !isset($body->last_update)||
           !isset($body->db_exist) ||
           !isset($body->device))
            throw new UrlExeception();
      break;
      case UPDATE_ALL:
      // TODO:
        return;
      default:
        return false;
    }

    if($of==INSERT || $of==UPDATE || $of==DELETE)
    {
      if(!isset($body->token) || !isset($body->last_update) || !isset($body->filter) || !isset($body->data))
        throw new UrlExeception();
    }

    return $body;

  }
*/
}

?>
