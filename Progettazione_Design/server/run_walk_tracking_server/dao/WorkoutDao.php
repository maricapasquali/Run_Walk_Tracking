<?php
require_once("DaoFactory.php");

class WorkoutDao {

   static function create($workout){
      try {
        if(connect())
        {

          startTransaction();
      
          $keys = array();
          $values = array();
          $typeParam ="";

          foreach ($workout as $key => $value) {
            array_push($keys, $key);
            array_push($values, $value);
            if($key==ID_USER || $key==DURATION) $typeParam.="i";
            else if($key==DISTANCE || $key==CALORIES) $typeParam.="d";
            else $typeParam.="s";

          }

          $stmt = getConnection()->prepare("INSERT INTO workout"."(" . join(",", $keys) .")"." VALUES (". join(",", str_split(str_repeat('?', count($keys)))).")");
          if(!$stmt) throw new Exception("User : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param($typeParam, ...array_values($values));

          if(!$stmt->execute()) throw new Exception("User : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          $id = getConnection()->insert_id;

          commitTransaction();
        }
      }catch (Exception $e) {
        rollbackTransaction();
        throw new Exception($e->getMessage());
      }
      closeConnection();
      return array(ID_WORKOUT=>$id); // +array_combine($keys, $values);
   }

   static function getAllForUser($id_user){
     $workouts = array();

     if(connect()){
        $stmt = getConnection()->prepare("SELECT id_workout, map_route, date, duration, distance, calories, sport
                                          FROM workout  WHERE id_user =?;");
        if(!$stmt) throw new Exception("Workouts : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("i", $id_user);
        if(!$stmt->execute()) throw new Exception("Workouts : Selezione fallita. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        while($row = $result->fetch_assoc()){
          array_push($workouts, $row);
        }
        $stmt->close();
     }
     closeConnection();
     return $workouts;
   }

   static function update($workout){
     try {
       if(connect())
       {

         startTransaction();
    

         $keys = array();
         $values = array();
         $typeParam ="";
         foreach ($workout as $key => $value) {
           if($key!=ID_WORKOUT){
             array_push($keys, $key);
             array_push($values, $value);
             if($key==DURATION) $typeParam.="i";
             else if($key==DISTANCE || $key==CALORIES || $key==MIDDLE_SPEED) $typeParam.="d";
             else $typeParam.="s";
           }
         }
         array_push($values, $workout[ID_WORKOUT]);

         $stmt = getConnection()->prepare("UPDATE workout SET " . join("=?,", $keys) ."=? WHERE id_workout=?");
         if(!$stmt) throw new Exception("Workout update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param($typeParam."i", ...array_values($values));
         if(!$stmt->execute()) throw new Exception("Workout : Update fallito. Errore: ". getErrorConnection());
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

   static function delete($id_workout){
     try {
       if(connect())
       {

         startTransaction();

         $stmt = getConnection()->prepare("DELETE FROM workout WHERE id_workout=?");
         if(!$stmt) throw new Exception("Workout delete : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("i", $id_workout);
         if(!$stmt->execute()) throw new Exception("Workout : Delete fallito. Errore: ". getErrorConnection());
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
