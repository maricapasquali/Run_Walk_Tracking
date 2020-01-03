<?php
require_once("DaoFactory.php");

class SettingsDao{
  /**
  * Settings
  */
  static function getAllForUser($id_user){

    if(connect()){

      $sport = self::getDefault(SPORT, $id_user);
      $target = self::getDefault(TARGET, $id_user);

      $unit_measure = self::getUnitMeaureDefaultForUser($id_user);
    }
    closeConnection();
    return array(SPORT=>$sport[SPORT],
                 TARGET=>$target[TARGET],
                 UNIT_MEASURE => $unit_measure);
  }

  private function getUnitMeaureDefaultForUser($id_user){

    $stmt = getConnection()->prepare("SELECT energy, distance, weight, height FROM unit_measure_default WHERE id_user=?;");
    if(!$stmt) throw new Exception("unit measure : Preparazione fallita. Errore: ". getErrorConnection());
    $stmt->bind_param("i", $id_user);
    if(!$stmt->execute()) throw new Exception("unit measure : Inserimento fallito. Errore: ". getErrorConnection());
    $result = $stmt->get_result();
    $unit = $result->fetch_assoc();
    $stmt->close();
    return $unit;
  }

  private function getDefault($type, $id_user){

    $sql =  "SELECT $type FROM ".$type."_default WHERE id_user=?;";
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
  static function updateSportFor($sport, $id_user){
    return self::updateDeafult(SPORT, $sport, $id_user);
  }

  static function updateTargetFor($target, $id_user){
    return self::updateDeafult(TARGET, $target, $id_user);
  }

  static function updateUnitHeightFor($height, $id_user){

    return self::updateUnitMeasure(HEIGHT, $height, $id_user);
  }

  static function updateUnitWeightFor($weight, $id_user){
    return self::updateUnitMeasure(WEIGHT, $weight, $id_user);
  }

  static function updateUnitDistanceFor($distance, $id_user){
    return self::updateUnitMeasure(DISTANCE, $distance, $id_user);
  }

  static function updateUnits($unit_measure, $id_user){
    try {
      if(connect())
      {
        startTransaction();

        $stmt = getConnection()->prepare("UPDATE unit_measure_default SET distance=? , weight=? , height=? WHERE id_user=?");
        if(!$stmt) throw new Exception("Units update : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("sssi" , $unit_measure[DISTANCE], $unit_measure[WEIGHT], $unit_measure[HEIGHT], $id_user);
        if(!$stmt->execute()) throw new Exception("Units : Update fallito. Errore: ". getErrorConnection());
        if(!$stmt->affected_rows) return;
        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return true;
  }

  private function updateDeafult($type, $val, $id_user){
    try {
      if(connect())
      {
        startTransaction();
        $sql = "UPDATE ".$type."_default SET $type=? WHERE id_user=?";
        $stmt = getConnection()->prepare($sql);
        if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("si" , $val, $id_user);
        if(!$stmt->execute()) throw new Exception("$type : Update fallito. Errore: ". getErrorConnection());
        if(!$stmt->affected_rows) return;
        $stmt->close();

        commitTransaction();

      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return true;
  }

  private function updateUnitMeasure($type, $id_unit, $id_user){
    try {
      if(connect())
      {
        startTransaction();

        $stmt = getConnection()->prepare("UPDATE unit_measure_default SET $type=? WHERE id_user=?");
        if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("si" , $id_unit, $id_user);
        if(!$stmt->execute()) throw new Exception("$type : Update fallito. Errore: ". getErrorConnection());
        if(!$stmt->affected_rows) return;
        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return true;
  }

}
?>
