<?php
require_once("DaoFactory.php");

class WorkoutDao implements IWorkoutDao{

  private $daoFactory;
  public function __construct(){
      $this->daoFactory = DaoFactory::instance();
  }

  private static $instance;
  public static function instance(){
    if(self::$instance==null) self::$instance = new self();
    return self::$instance;
  }

   public function create($workout){
       return $this->daoFactory->transaction(function() use ($workout){
             $keys = array();
             $values = array();
             $typeParam ="";

             foreach ($workout as $key => $value) {
               array_push($keys, $key);
               array_push($values, $value);
               if($key==ID_USER || $key==DURATION || $key==ID_WORKOUT) $typeParam.="i";
               else if($key==DISTANCE || $key==CALORIES) $typeParam.="d";
               else $typeParam.="s";
             }

             $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO workout"."(" . join(",", $keys) .")"." VALUES (". join(",", str_split(str_repeat('?', count($keys)))).")");
             if(!$stmt) throw new Exception("User : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
             $stmt->bind_param($typeParam, ...array_values($values));
             if(!$stmt->execute()) throw new Exception("User : Inserimento fallito. Errore: ". $this->daoFactory->getErrorConnection());
             $stmt->close();
       });
       /*
      try {
        if($this->daoFactory->connect())
        {

          $this->daoFactory->startTransaction();

          $keys = array();
          $values = array();
          $typeParam ="";

          foreach ($workout as $key => $value) {
            array_push($keys, $key);
            array_push($values, $value);
            if($key==ID_USER || $key==DURATION || $key==ID_WORKOUT) $typeParam.="i";
            else if($key==DISTANCE || $key==CALORIES) $typeParam.="d";
            else $typeParam.="s";
          }

          $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO workout"."(" . join(",", $keys) .")"." VALUES (". join(",", str_split(str_repeat('?', count($keys)))).")");
          if(!$stmt) throw new Exception("User : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param($typeParam, ...array_values($values));
          if(!$stmt->execute()) throw new Exception("User : Inserimento fallito. Errore: ". $this->daoFactory->getErrorConnection());
          if(!$stmt->affected_rows) return false;
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

   public function getAllForUser($id_user){
     $workouts = array();
     if($this->daoFactory->selection(function() use ($id_user, &$workouts){
         $stmt = $this->daoFactory->getConnection()->prepare("SELECT id_workout, map_route, date, duration, distance, calories, sport
                                           FROM workout  WHERE id_user =?;");
         if(!$stmt) throw new Exception("Workouts : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->bind_param("i", $id_user);
         if(!$stmt->execute()) throw new Exception("Workouts : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $result = $stmt->get_result();
         while($row = $result->fetch_assoc()){
           array_push($workouts, $row);
         }
         $stmt->close();
     }))
     {
       return $workouts;
     }
     /*
     if($this->daoFactory->connect()){
        $stmt = $this->daoFactory->getConnection()->prepare("SELECT id_workout, map_route, date, duration, distance, calories, sport
                                          FROM workout  WHERE id_user =?;");
        if(!$stmt) throw new Exception("Workouts : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
        $stmt->bind_param("i", $id_user);
        if(!$stmt->execute()) throw new Exception("Workouts : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
        $result = $stmt->get_result();
        while($row = $result->fetch_assoc()){
          array_push($workouts, $row);
        }
        $stmt->close();
     }
     $this->daoFactory->closeConnection();
     return $workouts;
     */
   }

   public function update($workout, $id_user){
     return $this->daoFactory->transaction(function() use ($workout, $id_user){
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
         array_push($values, $id_user);

         if(count($values) <= 2) return;

         $stmt = $this->daoFactory->getConnection()->prepare("UPDATE workout SET " . join("=?,", $keys) ."=? WHERE id_workout=? and id_user=?");
         if(!$stmt) throw new Exception("Workout update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->bind_param($typeParam."ii", ...array_values($values));
         if(!$stmt->execute()) throw new Exception("Workout : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->close();
     });

     /*
     try {
       if($this->daoFactory->connect())
       {

         $this->daoFactory->startTransaction();

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
         array_push($values, $id_user);

         if(count($values) <= 2) return;

         $stmt = $this->daoFactory->getConnection()->prepare("UPDATE workout SET " . join("=?,", $keys) ."=? WHERE id_workout=? and id_user=?");
         if(!$stmt) throw new Exception("Workout update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->bind_param($typeParam."ii", ...array_values($values));
         if(!$stmt->execute()) throw new Exception("Workout : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
         if(!$stmt->affected_rows) return false;
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

   public function updateAll($workouts, $id_user){
     $updateAll = false;
     $this->daoFactory->transaction(function() use ($workouts, $id_user, &$updateAll){
       $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM workout WHERE id_user =?;");
       if(!$stmt) throw new Exception("Delete Workouts : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
       $stmt->bind_param("i", $id_user);
       if(!$stmt->execute()) throw new Exception("All Workouts : Delete fallita. Errore: ". $this->daoFactory->getErrorConnection());
       $stmt->close();

       foreach ($workouts as $workout){
         $keys = array();
         $values = array();
         $typeParam ="";
         foreach ($workout as $key => $value) {
             array_push($keys, $key);
             array_push($values, $value);
             if($key==DURATION || $key==ID_WORKOUT) $typeParam.="i";
             else if($key==DISTANCE || $key==CALORIES || $key==MIDDLE_SPEED) $typeParam.="d";
             else $typeParam.="s";
         }
         $typeParam.="i";
         array_push($keys, ID_USER);
         array_push($values, $id_user);

         if(count($values) > 2){
           $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO workout (".join(",", $keys).") VALUES(". join(",", str_split(str_repeat('?', count($keys)))).")");
           if(!$stmt) throw new Exception("Workout update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
           $stmt->bind_param($typeParam, ...array_values($values));
           if(!$stmt->execute()) throw new Exception("Workout : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
           $updateAll = $stmt->affected_rows;
           $stmt->close();
         }
       }
     });
     return $updateAll;
     /*
     try {
       if($this->daoFactory->connect())
       {
         $this->daoFactory->startTransaction();

         $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM workout WHERE id_user =?;");
         if(!$stmt) throw new Exception("Delete Workouts : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->bind_param("i", $id_user);
         if(!$stmt->execute()) throw new Exception("All Workouts : Delete fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->close();

         foreach ($workouts as $workout){
           $keys = array();
           $values = array();
           $typeParam ="";
           foreach ($workout as $key => $value) {
               array_push($keys, $key);
               array_push($values, $value);
               if($key==DURATION || $key==ID_WORKOUT) $typeParam.="i";
               else if($key==DISTANCE || $key==CALORIES || $key==MIDDLE_SPEED) $typeParam.="d";
               else $typeParam.="s";
           }
           $typeParam.="i";
           array_push($keys, ID_USER);
           array_push($values, $id_user);

           if(count($values) <= 2) return false;
           $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO workout (".join(",", $keys).") VALUES(". join(",", str_split(str_repeat('?', count($keys)))).")");
           if(!$stmt) throw new Exception("Workout update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
           $stmt->bind_param($typeParam, ...array_values($values));
           if(!$stmt->execute()) throw new Exception("Workout : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
           if(!$stmt->affected_rows) return false;
           $stmt->close();
         }

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

   public function delete($id_workout){
     return $this->daoFactory->transaction(function() use ($id_workout){
       $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM workout WHERE id_workout=?");
       if(!$stmt) throw new Exception("Workout delete : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
       $stmt->bind_param("i", $id_workout);
       if(!$stmt->execute()) throw new Exception("Workout : Delete fallito. Errore: ". $this->daoFactory->getErrorConnection());
       $stmt->close();
     });
     /*
     try {
       if($this->daoFactory->connect())
       {

         $this->daoFactory->startTransaction();

         $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM workout WHERE id_workout=?");
         if(!$stmt) throw new Exception("Workout delete : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->bind_param("i", $id_workout);
         if(!$stmt->execute()) throw new Exception("Workout : Delete fallito. Errore: ". $this->daoFactory->getErrorConnection());
         if(!$stmt->affected_rows) return false;
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
