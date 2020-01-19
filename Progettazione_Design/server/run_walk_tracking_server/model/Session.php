<?php

require_once("Base.php");

class Session extends Base {

  private $id_user;
  private $device;
  private $token;
  private $last_update;

  public function __construct() {
     $get_arguments       = func_get_args();
     $number_of_arguments = func_num_args();

     if (method_exists($this, $method_name = '__construct'.$number_of_arguments)) {
         call_user_func_array(array($this, $method_name), $get_arguments);
     }
 }

  public function __construct1($sessionObject){
    if(self::check($sessionObject)) throw new SessionTokenExeception();
    $this->id_user = $sessionObject->id_user;
    $this->device = $sessionObject->device;
    $this->token = $sessionObject->token;
    $this->last_update = $sessionObject->last_update;
  }

  public function __construct2($id_user, $device){
    $this->id_user = $id_user;
    $this->device = $device;
    $this->token = self::createToken();
    $this->last_update = current_unixdatetime();
  }

  public static function createToken(){
      return getToken(100);
  }

  public function setLastUpdate($last_update){
      $this->last_update = $last_update;
  }

  public function setDevice($device){
      $this->device = $device;
  }

  public function check($sessionObject){
    return !isset($sessionObject->id_user) ||
           !isset($sessionObject->device) ||
           !isset($sessionObject->token) ||
           !isset($sessionObject->last_update);
  }

  public function jsonSerialize() {
    return get_object_vars($this);
  }

  public function toJson(){
    return json_encode($this);
  }
}

?>
