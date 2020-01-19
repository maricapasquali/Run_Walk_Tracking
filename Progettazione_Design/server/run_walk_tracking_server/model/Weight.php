<?php
require_once("Base.php");


class Weight  extends Base {

  private $id_weight;

  private $id_user;
  private $date;
  private $value;

  public function __construct($weightObject) {
    if(self::check($weightObject)) throw new DataExeception(WEIGHT);
    $this->id_user = $weightObject->id_user;
    $this->value = $weightObject->value;
    $this->date = $weightObject->date;

    if(isset($weightObject->id_weight))
      $this->id_weight = $weightObject->id_weight;

  }

  public function check($weightObject){
    return !isset($weightObject->id_user) ||
           !isset($weightObject->value) ||
           !isset($weightObject->date);
  }

  public function jsonSerialize() {
    return get_object_vars($this);
  }

  public function toJson(){
    return json_encode($this);
  }

}


 ?>
