<?php
require_once("DaoFactory.php");


class WeightDao {

   static function create($weight){
      try {
        if(connect())
        {

          startTransaction();
          $date = formatDate($weight[DATE]);

          $stmt = getConnection()->prepare("INSERT INTO weight(id_weight, id_user, date, value) VALUES (?, ?, ?, ?)");
          if(!$stmt) throw new Exception("Weight : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("iisd",$weight[ID_WEIGHT], $weight[ID_USER], $date, $weight[VALUE]);
          if(!$stmt->execute()) throw new Exception("Weight : Inserimento fallito. Errore: ". getErrorConnection());
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

   static function getAllForUser($id_user){
     $weights = array();

     if(connect()){
        $stmt = getConnection()->prepare("SELECT id_weight, date, value FROM weight WHERE id_user=? ORDER BY UNIX_TIMESTAMP(date)  DESC;");
        if(!$stmt) throw new Exception("Weights : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("i", $id_user);
        if(!$stmt->execute()) throw new Exception("Weights : Selezione fallita. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        while($row = $result->fetch_assoc()){
          array_push($weights, $row);
        }
        $stmt->close();
     }
     closeConnection();
     return $weights;
   }

   static function update($weight, $id_user){
     try {
       if(connect())
       {
         startTransaction();

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

         $stmt = getConnection()->prepare("UPDATE weight SET " . join("=?,", $keys) ."=? WHERE id_weight=? and id_user=?");
         if(!$stmt) throw new Exception("Weight update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param($typeParam."ii" , ...array_values($values));
         if(!$stmt->execute()) throw new Exception("Weight : Update fallito. Errore: ". getErrorConnection());

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

   static function updateAll($weights, $id_user){
        try {
          if(connect())
          {
            startTransaction();

            $stmt = getConnection()->prepare("DELETE FROM weight WHERE id_user =?;");
            if(!$stmt) throw new Exception("Delete All Weights : Preparazione fallita. Errore: ". getErrorConnection());
            $stmt->bind_param("i", $id_user);
            if(!$stmt->execute()) throw new Exception("All Weights : delete fallita. Errore: ". getErrorConnection());
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


              $stmt = getConnection()->prepare("INSERT INTO weight (".join(",", $keys).") VALUES(". join(",", str_split(str_repeat('?', count($keys)))).")");
              if(!$stmt) throw new Exception("Weight update : Preparazione fallita. Errore: ". getErrorConnection());
              $stmt->bind_param($typeParam , ...array_values($values));
              if(!$stmt->execute()) throw new Exception("Weight : Update fallito. Errore: ". getErrorConnection());

              if(!$stmt->affected_rows) return;

              $stmt->close();

            }
            commitTransaction();
          }
        }catch (Exception $e) {
          rollbackTransaction();
          throw new Exception($e->getMessage());
        }
        closeConnection();

        return true;
   }

   static function delete($id_weight){
     try {
       if(connect())
       {

         startTransaction();

         $stmt = getConnection()->prepare("DELETE FROM weight WHERE id_weight=?");
         if(!$stmt) throw new Exception("Workout delete : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("i", $id_weight);
         if(!$stmt->execute()) throw new Exception("Workout : Delete fallito. Errore: ". getErrorConnection());
         if(!$stmt->affected_rows) return;
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
