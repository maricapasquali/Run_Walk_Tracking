<?php
require_once("DaoFactory.php");

class SettingsDao{
  /**
  * Settings
  */
  static function getSettingsFor($id_user){

    if(connect()){

      $sport = self::getDefault(SPORT, $id_user);
      $target = self::getDefault(TARGET, $id_user);
      $language = self::getDefault(LANGUAGE, $id_user);
      $location = self::getDefault(LOCATION, $id_user);

      $energy = self::getUnitMeaure(ENERGY, $id_user);
      $distance = self::getUnitMeaure(DISTANCE,$id_user);
      $weight = self::getUnitMeaure(WEIGHT,$id_user);
      $height = self::getUnitMeaure(HEIGHT,$id_user);

    }
    closeConnection();
    return array(SPORT=>$sport,
                 TARGET=>$target,
                 LANGUAGE=> $language[LANGUAGE],
                 LOCATION=> $location["active"]==1,
                 UNIT_MEASURE=> array(
                                  ENERGY =>$energy,
                                  DISTANCE=>$distance,
                                  WEIGHT=>$weight,
                                  HEIGHT=>$height
                                ));
  }

  private function getUnitMeaure($type, $id_user){

    $stmt = getConnection()->prepare("SELECT t.id_unit_".$type." as id_unit, t.unit FROM unit_measure_default u JOIN unit_measure_".$type." t on (u.".$type."=t.id_unit_".$type.") WHERE id_user=?;");
    if(!$stmt) throw new Exception("unit measure : Preparazione fallita. Errore: ". getErrorConnection());
    $stmt->bind_param("i", $id_user);
    if(!$stmt->execute()) throw new Exception("unit measure : Inserimento fallito. Errore: ". getErrorConnection());
    $result = $stmt->get_result();
    $unit = $result->fetch_assoc();
    $stmt->close();
    return $unit;
  }

  private function getDefault($type, $id_user){

    if($type==LANGUAGE){
      $sql = "SELECT language FROM language_default WHERE id_user=?";
    }else if($type==LOCATION){
      $sql = "SELECT active FROM location WHERE id_user=?";
    }else{
      $sql = "SELECT t.* FROM ".$type."_default td JOIN ".$type." t on(td.".$type."=t.id_".$type.") WHERE id_user=?;";
    }

    $stmt = getConnection()->prepare($sql);
    if(!$stmt) throw new Exception("default $type : Preparazione fallita. Errore: ". getErrorConnection());
    $stmt->bind_param("i", $id_user);
    if(!$stmt->execute()) throw new Exception("default $type: Inserimento fallito. Errore: ". getErrorConnection());
    $result = $stmt->get_result();
    $def = $result->fetch_assoc();
    $stmt->close();
    return $def;
  }

  /**
  * Update Settings
  */
  static function updateSportFor($val, $id_user){
    return self::updateDeafult(SPORT, $val, $id_user);
  }

  static function updateTargetFor($val, $id_user){
    return self::updateDeafult(TARGET, $val, $id_user);
  }

  static function updateLanguageFor($val, $id_user){
    return self::updateDeafult(LANGUAGE, $val, $id_user);
  }

  static function updateLocationFor($val, $id_user){
    return self::updateDeafult(LOCATION, $val, $id_user);
  }

  static function updateUnitHeightFor($val, $id_user){
    return self::updateUnitMeasure(HEIGHT, $val, $id_user);
  }

  static function updateUnitWeightFor($val, $id_user){
    return self::updateUnitMeasure(WEIGHT, $val, $id_user);
  }

  static function updateUnitDistanceFor($val, $id_user){
    return self::updateUnitMeasure(DISTANCE, $val, $id_user);
  }

  private function updateDeafult($type, $val, $id_user){
    try {
      if(connect())
      {

        startTransaction();
        $sql = ($type==LOCATION ? "UPDATE location SET active=? WHERE id_user=?;"
          :"UPDATE ".$type."_default SET $type=? WHERE id_user=?");
        $param = ($type==LANGUAGE? "si": "ii");
        $stmt = getConnection()->prepare($sql);
        if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param($param , $val, $id_user);
        if(!$stmt->execute()) throw new Exception("$type : Update fallito. Errore: ". getErrorConnection());
        $stmt->close();

        $value_update = self::getDefault($type, $id_user);

        commitTransaction();

      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    $retunArray = array(UPDATE => true);
    if($type==LOCATION) return $retunArray  + array($type => $value_update["active"]==1);
    if($type==LANGUAGE) return $retunArray  + array($type => $value_update["language"]);
    return $retunArray + array($type => $value_update);
  }

  private function updateUnitMeasure($type, $id_unit, $id_user){
    try {
      if(connect())
      {

        startTransaction();

        $stmt = getConnection()->prepare("UPDATE unit_measure_default SET $type=? WHERE id_user=?");
        if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("ii" , $id_unit, $id_user);
        if(!$stmt->execute()) throw new Exception("$type : Update fallito. Errore: ". getErrorConnection());
        $stmt->close();


        $stmt = getConnection()->prepare("SELECT id_unit_".$type." as id_unit, unit FROM unit_measure_".$type."  WHERE id_unit_".$type."=?");
        if(!$stmt) throw new Exception("$type select : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("i", $id_unit);
        if(!$stmt->execute()) throw new Exception("$type : Select fallito. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        $value_update = $result->fetch_assoc();
        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return array(UPDATE => true) + array($type => $value_update);
  }

}
?>
