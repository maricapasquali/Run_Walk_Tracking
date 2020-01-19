<?php

require_once("Base.php");

class Workout extends Base {

  private $id_workout;

  private $id_user;
  private $sport;
  private $date;
  private $duration;

  private $map_route;
  private $distance;
  private $calories;

  public function __construct($workoutObject) {
    if(self::check($workoutObject)) throw new DataExeception(WORKOUT);
    $this->id_user = $workoutObject->id_user;
    $this->sport = $workoutObject->sport;
    $this->date = $workoutObject->date;
    $this->duration = $workoutObject->duration;

    if(isset($workoutObject->id_workout))
      $this->id_workout = $workoutObject->id_workout;
    if(isset($workoutObject->map_route))
      $this->map_route = $workoutObject->map_route;
    if(isset($workoutObject->distance))
      $this->distance = $workoutObject->distance;
    if(isset($workoutObject->calories))
      $this->calories = $workoutObject->calories;
  }

  public function check($workoutObject){
    return !isset($workoutObject->id_user) ||
           !isset($workoutObject->sport) ||
           !isset($workoutObject->date) ||
           !isset($workoutObject->duration);
  }

  public function jsonSerialize() {
    return get_object_vars($this);
  }

  public function toJson(){
    return json_encode($this);
  }
}

 ?>
