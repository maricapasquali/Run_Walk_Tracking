<?php
require_once("DaoFactory.php");


class WeightDao {

   static function create($weight){
      try {
        if(connect())
        {

          startTransaction();

          $stmt = getConnection()->prepare("INSERT INTO weight(id_user, date, value) VALUES (?, ?, ?)");
          if(!$stmt) throw new Exception("Weight : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("isd",$weight[ID_USER], $weight[DATE], $weight[VALUE]);
          if(!$stmt->execute()) throw new Exception("Weight : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          $stmt = getConnection()->prepare("SELECT id_weight FROM weight WHERE id_user=? and date=? and value=?");
          if(!$stmt) throw new Exception("Weight id : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("isd",$weight[ID_USER], $weight[DATE], $weight[VALUE]);
          if(!$stmt->execute()) throw new Exception("Weight id : Inserimento fallito. Errore: ". getErrorConnection());
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
      return array(ID_WEIGHT => $id) + $weight;
   }

  /**
   * ID ULTIMO PARAMETRO {id_weight}
   */
   static function update($weight){
     try {
       if(connect())
       {

         startTransaction();

         $keys = array();
         $typeParam ="";
         foreach ($weight as $key => $value) {
           if($key!=ID_WEIGHT) array_push($keys, $key);

           if($key==DATE) $typeParam.="s";
           else if($key==VALUE) $typeParam.="d";
         }

         $stmt = getConnection()->prepare("UPDATE weight SET " . join("=?,", $keys) ."=? WHERE id_weight=?");
         if(!$stmt) throw new Exception("Weight update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param($typeParam."i" , ...array_values($weight));
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
