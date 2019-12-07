<?php
require_once("DaoFactory.php");

class StatisticsDao{

  private function getAll($type, $id_user){
    $statistics = array();

    if(connect()){

      $sql = ($type==WEIGHT ? "SELECT id_weight, date, value as weight FROM weight WHERE id_user=? ORDER BY UNIX_TIMESTAMP(date)  DESC;" :
      "SELECT date, $type FROM workout WHERE id_user=? and $type is not null ORDER BY UNIX_TIMESTAMP(date) DESC;");

       $stmt = getConnection()->prepare($sql);
       if(!$stmt) throw new Exception("Statistics : Preparazione fallita. Errore: ". getErrorConnection());
       $stmt->bind_param("i", $id_user);
       if(!$stmt->execute()) throw new Exception("Statistics : Selezione fallita. Errore: ". getErrorConnection());
       $result = $stmt->get_result();
       while($row = $result->fetch_assoc()){
         array_push($statistics, $row);
       }
       $stmt->close();
    }
    closeConnection();
    return $statistics;
  }

  static function getAllMiddleSpeedFor($id_user){
    return self::getAll(MIDDLE_SPEED,$id_user);
  }
  static function getAllCaloriesFor($id_user){
    return self::getAll(CALORIES, $id_user);
  }
  static function getAllDistanceFor($id_user){
    return self::getAll(DISTANCE, $id_user);
  }
  static function getAllWeightFor($id_user){
    return self::getAll(WEIGHT, $id_user);
  }

  static function deleteWeight($id_weight){
    try {
      if(connect())
      {

        startTransaction();

        $stmt = getConnection()->prepare("DELETE FROM weight WHERE id_weight=?");
        if(!$stmt) throw new Exception("Weight delete : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("i", $id_weight);
        if(!$stmt->execute()) throw new Exception("Weight : Delete fallito. Errore: ". getErrorConnection());
        $stmt->close();

        commitTransaction();
      }
    }catch (Exception $e) {
      rollbackTransaction();
      throw new Exception($e->getMessage());
      return;
    }
    closeConnection();
    return true;
  }
}
?>
