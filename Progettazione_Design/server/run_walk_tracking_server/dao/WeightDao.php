<?php
require_once("DaoFactory.php");


class WeightDao {

   static function create($weight){
      try {
        if(connect())
        {

          startTransaction();
          $date = formatDate($weight[DATE]);

          $stmt = getConnection()->prepare("INSERT INTO weight(id_user, date, value) VALUES (?, ?, ?)");
          if(!$stmt) throw new Exception("Weight : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("isd",$weight[ID_USER], $date, $weight[VALUE]);
          if(!$stmt->execute()) throw new Exception("Weight : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();
          $id = getConnection()->insert_id;
          commitTransaction();
        }
      }catch (Exception $e) {
        rollbackTransaction();
        throw new Exception($e->getMessage());
      }
      closeConnection();
      return array(ID_WEIGHT => $id);//+ $weight;
   }

   static function update($weight){
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


         $stmt = getConnection()->prepare("UPDATE weight SET " . join("=?,", $keys) ."=? WHERE id_weight=?");
         if(!$stmt) throw new Exception("Weight update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param($typeParam."i" , ...array_values($values));
         if(!$stmt->execute()) throw new Exception("Weight : Update fallito. Errore: ". getErrorConnection());
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

   static function delete($id_weight){
     try {
       if(connect())
       {

         startTransaction();

         $stmt = getConnection()->prepare("DELETE FROM weight WHERE id_weight=?");
         if(!$stmt) throw new Exception("Workout delete : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("i", $id_weight);
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
