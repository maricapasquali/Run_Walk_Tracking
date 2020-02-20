<?php
require_once("DaoFactory.php");

class SettingsDao implements ISettingsDao{

  private $daoFactory;
  public function __construct(){
      $this->daoFactory = DaoFactory::instance();
  }

  private static $instance;
  public static function instance(){
    if(self::$instance==null) self::$instance = new self();
    return self::$instance;
  }
  /**
  * Settings
  */
  public function getAllForUser($id_user){

    if($this->daoFactory->connect()){

      $sport = self::getDefault(SPORT, $id_user);
      $target = self::getDefault(TARGET, $id_user);

      $unit_measure = self::getUnitMeaureDefaultForUser($id_user);
    }
    $this->daoFactory->closeConnection();
    return array(SPORT=>$sport[SPORT],
                 TARGET=>$target[TARGET],
                 UNIT_MEASURE => $unit_measure);
  }

  private function getUnitMeaureDefaultForUser($id_user){

    if($this->daoFactory->selection(function() use ($id_user, &$unit){
      $stmt = $this->daoFactory->getConnection()->prepare("SELECT energy, distance, weight, height FROM unit_measure_default WHERE id_user=?;");
      if(!$stmt) throw new Exception("unit measure : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
      $stmt->bind_param("i", $id_user);
      if(!$stmt->execute()) throw new Exception("unit measure : Inserimento fallito. Errore: ". $this->daoFactory->getErrorConnection());
      $result = $stmt->get_result();
      $unit = $result->fetch_assoc();
      $stmt->close();
    }))
    {
      return $unit;
    }
    /*
    $stmt = $this->daoFactory->getConnection()->prepare("SELECT energy, distance, weight, height FROM unit_measure_default WHERE id_user=?;");
    if(!$stmt) throw new Exception("unit measure : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
    $stmt->bind_param("i", $id_user);
    if(!$stmt->execute()) throw new Exception("unit measure : Inserimento fallito. Errore: ". $this->daoFactory->getErrorConnection());
    $result = $stmt->get_result();
    $unit = $result->fetch_assoc();
    $stmt->close();
    return $unit;
    */
  }

  private function getDefault($type, $id_user){

    if($this->daoFactory->selection(function() use ($type, $id_user, &$def){
      $stmt = $this->daoFactory->getConnection()->prepare("SELECT $type FROM ".$type."_default WHERE id_user=?");
      if(!$stmt) throw new Exception("default $type : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
      $stmt->bind_param("i", $id_user);
      if(!$stmt->execute()) throw new Exception("default $type: Inserimento fallito. Errore: ". $this->daoFactory->getErrorConnection());
      $result = $stmt->get_result();
      $def = $result->fetch_assoc();
      $stmt->close();
    }))
    {
      return $def;
    }
    /*
    $sql =  "SELECT $type FROM ".$type."_default WHERE id_user=?;";
    $stmt = $this->daoFactory->getConnection()->prepare($sql);
    if(!$stmt) throw new Exception("default $type : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
    $stmt->bind_param("i", $id_user);
    if(!$stmt->execute()) throw new Exception("default $type: Inserimento fallito. Errore: ". $this->daoFactory->getErrorConnection());
    $result = $stmt->get_result();
    $def = $result->fetch_assoc();
    $stmt->close();
    return $def;
    */
  }

  /**
  * Update Settings
  */
  public function updateSportFor($sport, $id_user){
    return self::updateDeafult(SPORT, $sport, $id_user);
  }

  public function updateTargetFor($target, $id_user){
    return self::updateDeafult(TARGET, $target, $id_user);
  }

  public function updateUnitHeightFor($height, $id_user){

    return self::updateUnitMeasure(HEIGHT, $height, $id_user);
  }

  public function updateUnitWeightFor($weight, $id_user){
    return self::updateUnitMeasure(WEIGHT, $weight, $id_user);
  }

  public function updateUnitDistanceFor($distance, $id_user){
    return self::updateUnitMeasure(DISTANCE, $distance, $id_user);
  }

  public function updateUnits($unit_measure, $id_user){

    return $this->daoFactory->transaction(function() use ($unit_measure, $id_user){
          $stmt = $this->daoFactory->getConnection()->prepare("UPDATE unit_measure_default SET distance=? , weight=? , height=? WHERE id_user=?");
          if(!$stmt) throw new Exception("Units update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("sssi" , $unit_measure[DISTANCE], $unit_measure[WEIGHT], $unit_measure[HEIGHT], $id_user);
          if(!$stmt->execute()) throw new Exception("Units : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->close();
    });

    /*
    try {
      if($this->daoFactory->connect())
      {
        $this->daoFactory->startTransaction();

        $stmt = $this->daoFactory->getConnection()->prepare("UPDATE unit_measure_default SET distance=? , weight=? , height=? WHERE id_user=?");
        if(!$stmt) throw new Exception("Units update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
        $stmt->bind_param("sssi" , $unit_measure[DISTANCE], $unit_measure[WEIGHT], $unit_measure[HEIGHT], $id_user);
        if(!$stmt->execute()) throw new Exception("Units : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
        if(!$stmt->affected_rows) return;
        $stmt->close();

        $this->daoFactory->commitTransaction();
      }
    }catch (Exception $e) {
      $this->daoFactory->rollbackTransaction();
      throw $e;
    }
    $this->daoFactory->closeConnection();
    return true;
    */
  }

  private function updateDeafult($type, $val, $id_user){

    return $this->daoFactory->transaction(function() use ($type, $val, $id_user){
          $stmt = $this->daoFactory->getConnection()->prepare("UPDATE ".$type."_default SET $type=? WHERE id_user=?");
          if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("si" , $val, $id_user);
          if(!$stmt->execute()) throw new Exception("$type : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->close();
    });

    /*
    try {
      if($this->daoFactory->connect())
      {
        $this->daoFactory->startTransaction();
        $sql = "UPDATE ".$type."_default SET $type=? WHERE id_user=?";
        $stmt = $this->daoFactory->getConnection()->prepare($sql);
        if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
        $stmt->bind_param("si" , $val, $id_user);
        if(!$stmt->execute()) throw new Exception("$type : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
        if(!$stmt->affected_rows) return;
        $stmt->close();

        $this->daoFactory->commitTransaction();

      }
    }catch (Exception $e) {
      $this->daoFactory->rollbackTransaction();
      throw $e;
    }
    $this->daoFactory->closeConnection();
    return true;
    */
  }

  private function updateUnitMeasure($type, $id_unit, $id_user){
    
    return $this->daoFactory->transaction(function() use ($type, $id_unit, $id_user){
          $stmt = $this->daoFactory->getConnection()->prepare("UPDATE unit_measure_default SET $type=? WHERE id_user=?");
          if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("si" , $id_unit, $id_user);
          if(!$stmt->execute()) throw new Exception("$type : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->close();
    });

    /*
    try {
      if($this->daoFactory->connect())
      {
        $this->daoFactory->startTransaction();

        $stmt = $this->daoFactory->getConnection()->prepare("UPDATE unit_measure_default SET $type=? WHERE id_user=?");
        if(!$stmt) throw new Exception("$type update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
        $stmt->bind_param("si" , $id_unit, $id_user);
        if(!$stmt->execute()) throw new Exception("$type : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
        if(!$stmt->affected_rows) return;
        $stmt->close();

        $this->daoFactory->commitTransaction();
      }
    }catch (Exception $e) {
      $this->daoFactory->rollbackTransaction();
      throw $e;
    }
    $this->daoFactory->closeConnection();
    return true;
    */
  }

}
?>
