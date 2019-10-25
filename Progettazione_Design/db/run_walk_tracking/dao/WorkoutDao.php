<?php
require_once("DaoFactory.php");

class WorkoutDao {

   static function create($workout){
      try {
        if(connect())
        {

          startTransaction();


          $keys = array();
          $typeParam ="";

          foreach ($workout as $key => $value) {
            array_push($keys, $key);
            if($key==ID_USER || $key==DURATION || $key==ID_SPORT) $typeParam.="i";
            else if($key==DISTANCE || $key==CALORIES || $key==MIDDLE_SPEED) $typeParam.="d";
            else $typeParam.="s";

          }

          $stmt = getConnection()->prepare("INSERT INTO workout"."(" . join(",", $keys) .")"." VALUES (". join(",", str_split(str_repeat('?', count($keys)))).")");
          if(!$stmt) throw new Exception("User : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param($typeParam, ...array_values($workout));

          if(!$stmt->execute()) throw new Exception("User : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          $stmt = getConnection()->prepare("SELECT id_workout FROM workout WHERE id_user=? and date=?");
          if(!$stmt) throw new Exception("workout id : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("is", $workout[ID_USER], $workout[DATE]);
          if(!$stmt->execute()) throw new Exception("workout id : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->bind_result($id);
          $stmt->fetch();
          $stmt->close();

          commitTransaction();
        }
      }catch (Exception $e) {
        rollbackTransaction();
        throw new Exception($e->getMessage());
      }
      closeConnection();
      return array(ID_WORKOUT => $id) + $workout;
   }

   static function getAllForUser($id_user){
     $workouts = array();

     if(connect()){
        $stmt = getConnection()->prepare("SELECT w.*, s.name as sport FROM workout w join sport s on(w.id_sport=s.id_sport) WHERE id_user =? ORDER BY date DESC;");
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

   /**
   * ID ULTIMO PARAMETRO {id_workout}
   */
   static function update($workout){
     try {
       if(connect())
       {

         startTransaction();

         $keys = array();
         $typeParam ="";
         foreach ($workout as $key => $value) {
           if($key!=ID_WORKOUT) array_push($keys, $key);

           if($key==ID_WORKOUT || $key==DURATION || $key==ID_SPORT) $typeParam.="i";
           else if($key==DISTANCE || $key==CALORIES || $key==MIDDLE_SPEED) $typeParam.="d";
           else $typeParam.="s";
         }

         $stmt = getConnection()->prepare("UPDATE workout SET " . join("=?,", $keys) ."=? WHERE id_workout=?");
         if(!$stmt) throw new Exception("Workout update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param($typeParam, ...array_values($workout));

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
