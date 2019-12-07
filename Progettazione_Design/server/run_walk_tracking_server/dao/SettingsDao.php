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
  static function updateSportFor($val, $id_user){
    return self::updateDeafult(SPORT, $val, $id_user);
  }

  static function updateTargetFor($val, $id_user){
    return self::updateDeafult(TARGET, $val, $id_user);
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
        $sql = "UPDATE ".$type."_default SET $type=? WHERE id_user=?";
        $stmt = getConnection()->prepare($sql);
        if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("si" , $val, $id_user);
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
    return array(UPDATE => true) + array($type => $value_update[$type]);
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
        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
    }
    closeConnection();
    return array(UPDATE => true);
  }

}
?>
