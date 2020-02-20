<?php
require_once("DaoFactory.php");


class WeightDao implements IWeightDao{

  private $daoFactory;
  public function __construct(){
      $this->daoFactory = DaoFactory::instance();
  }

  private static $instance;
  public static function instance(){
    if(self::$instance==null) self::$instance = new self();
    return self::$instance;
  }

   public function create($weight){
     return $this->daoFactory->transaction(function() use ($weight){
             $date = formatDate($weight[DATE]);
             $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO weight(id_weight, id_user, date, value) VALUES (?, ?, ?, ?)");
             if(!$stmt) throw new Exception("Weight : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
             $stmt->bind_param("iisd",$weight[ID_WEIGHT], $weight[ID_USER], $date, $weight[VALUE]);
             if(!$stmt->execute()) throw new Exception("Weight : Inserimento fallito. Errore: ". $this->daoFactory->getErrorConnection());
             $stmt->close();
     });

      /*
      try {
        if($this->daoFactory->connect())
        {

          $this->daoFactory->startTransaction();

          $date = formatDate($weight[DATE]);

          $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO weight(id_weight, id_user, date, value) VALUES (?, ?, ?, ?)");
          if(!$stmt) throw new Exception("Weight : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("iisd",$weight[ID_WEIGHT], $weight[ID_USER], $date, $weight[VALUE]);
          if(!$stmt->execute()) throw new Exception("Weight : Inserimento fallito. Errore: ". $this->daoFactory->getErrorConnection());
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

   public function getAllForUser($id_user){
     $weights = array();
     if($this->daoFactory->selection(function() use ($id_user, &$weights){
       $stmt = $this->daoFactory->getConnection()->prepare("SELECT id_weight, date, value FROM weight WHERE id_user=? ORDER BY UNIX_TIMESTAMP(date)  DESC;");
       if(!$stmt) throw new Exception("Weights : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
       $stmt->bind_param("i", $id_user);
       if(!$stmt->execute()) throw new Exception("Weights : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
       $result = $stmt->get_result();
       while($row = $result->fetch_assoc()){
         array_push($weights, $row);
       }
       $stmt->close();
     }))
     {
       return $weights;
     }
     /*
     $weights = array();
     if($this->daoFactory->connect()){
        $stmt = $this->daoFactory->getConnection()->prepare("SELECT id_weight, date, value FROM weight WHERE id_user=? ORDER BY UNIX_TIMESTAMP(date)  DESC;");
        if(!$stmt) throw new Exception("Weights : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
        $stmt->bind_param("i", $id_user);
        if(!$stmt->execute()) throw new Exception("Weights : Selezione fallita. Errore: ". $this->daoFactory->getErrorConnection());
        $result = $stmt->get_result();
        while($row = $result->fetch_assoc()){
          array_push($weights, $row);
        }
        $stmt->close();
     }
     $this->daoFactory->closeConnection();
     return $weights;
     */
   }

   public function update($weight, $id_user){
     return $this->daoFactory->transaction(function() use ($weight, $id_user){
         $keys = array();
         $values = array();
         $typeParam ="";
         foreach ($weight as $key => $value) {
           if($key!=ID_WEIGHT){
             array_push($keys, $key);
             array_push($values, $key==DATE ? formatDate($value) : $value);
           }
           if($key==DATE) $typeParam.="s";
           else if($key==VALUE) $typeParam.="d";
         }
         array_push($values, $weight[ID_WEIGHT]);
         array_push($values, $id_user);

         if(count($values) > 2){
           $stmt = $this->daoFactory->getConnection()->prepare("UPDATE weight SET " . join("=?,", $keys) ."=? WHERE id_weight=? and id_user=?");
           if(!$stmt) throw new Exception("Weight update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
           $stmt->bind_param($typeParam."ii" , ...array_values($values));
           if(!$stmt->execute()) throw new Exception("Weight : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());
           $stmt->close();
         }
     });
     /*
     try {
       if($this->daoFactory->connect())
       {
         $this->daoFactory->startTransaction();

         $keys = array();
         $values = array();
         $typeParam ="";
         foreach ($weight as $key => $value) {
           if($key!=ID_WEIGHT){
             array_push($keys, $key);
             array_push($values, $key==DATE ? formatDate($value) : $value);
           }
           if($key==DATE) $typeParam.="s";
           else if($key==VALUE) $typeParam.="d";
         }
         array_push($values, $weight[ID_WEIGHT]);
         array_push($values, $id_user);

         if(count($values) <= 2) return;

         $stmt = $this->daoFactory->getConnection()->prepare("UPDATE weight SET " . join("=?,", $keys) ."=? WHERE id_weight=? and id_user=?");
         if(!$stmt) throw new Exception("Weight update : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->bind_param($typeParam."ii" , ...array_values($values));
         if(!$stmt->execute()) throw new Exception("Weight : Update fallito. Errore: ". $this->daoFactory->getErrorConnection());

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

   public function updateAll($weights, $id_user){
      $updateAll = false;
      $this->daoFactory->transaction(function() use ($weights, $id_user){

          $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM weight WHERE id_user =?;");
          if(!$stmt) throw new Exception("Delete All Weights : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->bind_param("i", $id_user);
          if(!$stmt->execute()) throw new Exception("All Weights : delete fallita. Errore: ". $this->daoFactory->getErrorConnection());
          $stmt->close();

          foreach ($weights as $weight) {
            $keys = array();
            $values = array();
            $typeParam ="";
            foreach ($weight as $key => $value) {

              array_push($keys, $key);
              array_push($values, $key==DATE ? formatDate($value) : $value);

              if($key==DATE) $typeParam.="s";
              else if($key==VALUE) $typeParam.="d";
              else $typeParam.="i";
            }

            array_push($keys, ID_USER);
            array_push($values, $id_user);
            $typeParam.="i";

            if(count($values) > 2) {
                $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO weight (".join(",", $keys).") VALUES(". join(",", str_split(str_repeat('?', count($keys)))).")");
                if(!$stmt) throw new Exception("Weight update : Preparazione fallita. Errore: ". getErrorConnection());
                $stmt->bind_param($typeParam , ...array_values($values));
                if(!$stmt->execute()) throw new Exception("Weight : Update fallito. Errore: ". getErrorConnection());
                $updateAll = $stmt->affected_rows;
            }
            $stmt->close();
          }
      });
      return $updateAll;
      /*
        try {
          if($this->daoFactory->connect())
          {
            $this->daoFactory->startTransaction();

            $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM weight WHERE id_user =?;");
            if(!$stmt) throw new Exception("Delete All Weights : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
            $stmt->bind_param("i", $id_user);
            if(!$stmt->execute()) throw new Exception("All Weights : delete fallita. Errore: ". $this->daoFactory->getErrorConnection());
            $stmt->close();

            foreach ($weights as $weight) {
              $keys = array();
              $values = array();
              $typeParam ="";
              foreach ($weight as $key => $value) {

                array_push($keys, $key);
                array_push($values, $key==DATE ? formatDate($value) : $value);

                if($key==DATE) $typeParam.="s";
                else if($key==VALUE) $typeParam.="d";
                else $typeParam.="i";
              }

              array_push($keys, ID_USER);
              array_push($values, $id_user);
              $typeParam.="i";

              if(count($values) <= 2) return;


              $stmt = $this->daoFactory->getConnection()->prepare("INSERT INTO weight (".join(",", $keys).") VALUES(". join(",", str_split(str_repeat('?', count($keys)))).")");
              if(!$stmt) throw new Exception("Weight update : Preparazione fallita. Errore: ". getErrorConnection());
              $stmt->bind_param($typeParam , ...array_values($values));
              if(!$stmt->execute()) throw new Exception("Weight : Update fallito. Errore: ". getErrorConnection());

              if(!$stmt->affected_rows) return;

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

   public function delete($id_weight){
     return $this->daoFactory->transaction(function() use ($id_weight){
       $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM weight WHERE id_weight=?");
       if(!$stmt) throw new Exception("Workout delete : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
       $stmt->bind_param("i", $id_weight);
       if(!$stmt->execute()) throw new Exception("Workout : Delete fallito. Errore: ". $this->daoFactory->getErrorConnection());
       $stmt->close();
     });
     /*
     try {
       if($this->daoFactory->connect())
       {
         $this->daoFactory->startTransaction();

         $stmt = $this->daoFactory->getConnection()->prepare("DELETE FROM weight WHERE id_weight=?");
         if(!$stmt) throw new Exception("Workout delete : Preparazione fallita. Errore: ". $this->daoFactory->getErrorConnection());
         $stmt->bind_param("i", $id_weight);
         if(!$stmt->execute()) throw new Exception("Workout : Delete fallito. Errore: ". $this->daoFactory->getErrorConnection());
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
