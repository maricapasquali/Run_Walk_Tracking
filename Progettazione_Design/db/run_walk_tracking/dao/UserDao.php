<?php
require_once("DaoFactory.php");

class UserDao {

   static function create($user){
      try {
        if(connect()){

          startTransaction();

          $stmt = getConnection()->prepare("INSERT INTO user (name, last_name, gender, birth_date, email, phone, city, height)
                             VALUES (?, ?, ?, ?, ?, ?, ?, ? )");
          if(!$stmt) throw new Exception("User : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("sssssssd", $name, $lastname, $gender, $birth_date, $email, $phone, $city, $height);
          $name = $user[NAME];
          $lastname = $user[LAST_NAME];
          $gender = $user[GENDER];
          $birth_date = formatDate($user[BIRTH_DATE]);
          $email = $user[EMAIL];
          $phone = $user[PHONE];
          $city = $user[CITY];
          $height = $user[HEIGHT];
          if(!$stmt->execute()) throw new Exception("User : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          $id = getConnection()->insert_id;

          $stmt = getConnection()->prepare("INSERT INTO login(id_user, username, hash_password) VALUES (?, ?, ?)");
          if(!$stmt) throw new Exception("Login : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("iss", $id, $username, $hash);
          $username = $user[USERNAME];
          $hash = hashed_password($user[PASSWORD]);
          if(!$stmt->execute()) throw new Exception("Login : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          $stmt = getConnection()->prepare("SELECT id_target FROM target WHERE name=?");
          if(!$stmt) throw new Exception("Target find id: Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("s", $name_target);
          $name_target = $user[TARGET];
          if(!$stmt->execute()) throw new Exception("Target find id: Selezione fallito. Errore: ". getErrorConnection());
          $stmt->bind_result($id_target);
          $stmt->fetch();
          if($id_target==NULL) throw new Exception("Target ($name_target) NON ESISTENTE");
          $stmt->close();

          $stmt = getConnection()->prepare("INSERT INTO target_default(id_user, target) VALUES (?, ?)");
          if(!$stmt) throw new Exception("Target : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("ii", $id, $id_target);
          if(!$stmt->execute()) throw new Exception("Target : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          $stmt = getConnection()->prepare("INSERT INTO language_default(id_user, language) VALUES (?, ?)");
          if(!$stmt) throw new Exception("Language : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("is", $id,$language);
          $language = $user[LANGUAGE];
          if(!$stmt->execute()) throw new Exception("Language : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();


          $stmt = getConnection()->prepare("INSERT INTO weight(id_user, date, value) VALUES (?, CURRENT_DATE, ?)");
          if(!$stmt) throw new Exception("Weight : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("id", $id, $weight);
          $weight = $user[WEIGHT];
          if(!$stmt->execute()) throw new Exception("Weight : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          if(isset($user[IMG])){

            $stmt = getConnection()->prepare("INSERT INTO profile_image(id_user, img_encode) VALUES (?, ?)");
            if(!$stmt) throw new Exception("Image : Preparazione fallita. Errore: ". getErrorConnection());
            $stmt->bind_param("is", $id, $image);
            $image = $user[IMG];
            if(!$stmt->execute()) throw new Exception("Image : Inserimento fallito. Errore: ". getErrorConnection());
            $stmt->close();
          }
          $user[ID_USER]= $id;

          $token = getToken(10);

          $stmt = getConnection()->prepare("INSERT INTO verify_registration(id_user, token) VALUES (?,?)");
          if(!$stmt) throw new Exception("Token : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("is", $id, $token);
          if(!$stmt->execute()) throw new Exception("Token : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          // Invio email con token
          $body = "Token: ".$token;
          sendEmail($email, "Registrazione", $body);

          commitTransaction();
        }
      }catch (Exception $e) {
        rollbackTransaction();
        throw new Exception($e->getMessage());
        return;
      }
      closeConnection();
      return array(ID_USER => $id, TOKEN=>$token);
   }

   static function checkCredential($username, $password){
     if(connect()){
         $stmt = getConnection()->prepare("SELECT id_user, hash_password FROM login WHERE username=?");
         if(!$stmt) throw new Exception("user id : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("s", $username);
         if(!$stmt->execute()) throw new Exception("Check : Selezione fallita. Errore: ". getErrorConnection());
         $stmt->bind_result($id_user, $hash_password);
         $stmt->fetch();
         $stmt->close();

         if($id_user==NULL && $hash_password==NULL) throw new Exception("USER NON REGISTRATO!");

         if(!password_verify($password, $hash_password)) throw new Exception("PASSWORD NON CORRETTA!");
     }
     closeConnection();
     return array(ID_USER => $id_user);
   }

   static function checkToken($id_user, $token){
     if(connect()){
         $stmt = getConnection()->prepare("SELECT count(*) FROM verify_registration WHERE id_user=? and token=?");
         if(!$stmt) throw new Exception("Check Token : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("is", $id_user, $token);
         if(!$stmt->execute()) throw new Exception("Check Token: Selezione fallita. Errore: ". getErrorConnection());
         $stmt->bind_result($isCorrect);
         $stmt->fetch();
         $stmt->close();

         if($isCorrect==0) throw new Exception("Token non corretto");

         $stmt = getConnection()->prepare("UPDATE login SET date = CURRENT_TIMESTAMP WHERE id_user=?");
         if(!$stmt) throw new Exception("Date login update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("i",$id_user );
         if(!$stmt->execute()) throw new Exception("Date login : Update fallito. Errore: ". getErrorConnection());
         $stmt->close();
     }
     closeConnection();
     return true;
   }

   static function checkFirstLogin($user){
     if(connect()){
         $stmt = getConnection()->prepare("SELECT count(*) FROM login WHERE id_user=? and date is null");
         if(!$stmt) throw new Exception("Check first login : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("i", $user[ID_USER]);
         if(!$stmt->execute()) throw new Exception("Check first login: Selezione fallita. Errore: ". getErrorConnection());
         $stmt->bind_result($isFirstlogin);
         $stmt->fetch();
         $stmt->close();
     }
     closeConnection();
     return $isFirstlogin;
   }

   static function getUserForId($id){
     $user = array();

     if(connect()){
        $stmt = getConnection()->prepare("SELECT u.*, l.username FROM login l JOIN user u on(l.id_user=u.id_user) WHERE u.id_user=?");
        if(!$stmt) throw new Exception("user from id : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("s", $id);
        if(!$stmt->execute()) throw new Exception("user from id : Selezione fallita. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        while($row = $result->fetch_assoc()){
          array_push($user, $row);
        }
        if(count($user)==0) throw new Exception("USER (id=$id) NON ESISTENTE");
        $stmt->close();

     }
     closeConnection();
     return $user[0];
   }

   static function getUserForUsername($username){
     $user = array();

     if(connect()){
        $stmt = getConnection()->prepare("SELECT id_user FROM login WHERE username=?");
        if(!$stmt) throw new Exception("user id : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("s", $username);
        if(!$stmt->execute()) throw new Exception("Check : Selezione fallita. Errore: ". getErrorConnection());
        $stmt->bind_result($id_user);
        $stmt->fetch();
        if($id_user==NULL) throw new Exception("USER (username = $username) NON ESISTENTE");
        $stmt->close();
     }
     closeConnection();
     return array(ID_USER=>$id_user);
   }


  static function getImageProfileForIdUser($id){
        if(connect()){
           $stmt = getConnection()->prepare("SELECT img_encode FROM profile_image WHERE id_user=?");
           if(!$stmt) throw new Exception("user image id : Preparazione fallita. Errore: ". getErrorConnection());
           $stmt->bind_param("i", $id);
           if(!$stmt->execute()) throw new Exception("user image : Selezione fallita. Errore: ". getErrorConnection());
           $stmt->bind_result($image);
           $stmt->fetch();
           $stmt->close();
        }
        closeConnection();
        return array(IMG=>$image);
   }

   static function requestForgotPassword($email, $c_key, $end_validity){
     try {
       if(connect()){

         $stmt = getConnection()->prepare("SELECT count(*) FROM request_forgot_password WHERE email=?");
         if(!$stmt) throw new Exception("Check email : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("s", $email);
         if(!$stmt->execute()) throw new Exception("Check email : Selezione fallito. Errore: ". getErrorConnection());
         $stmt->bind_result($row);
         $stmt->fetch();
         $stmt->close();
         $sql = $row==0?"INSERT INTO request_forgot_password (c_key, end_validity,email) VALUES (?, ?, ? )" :
                        "UPDATE request_forgot_password set c_key=?, end_validity=? WHERE email=?";

         startTransaction();
         $stmt = getConnection()->prepare($sql);
         if(!$stmt) throw new Exception("Forgot Password : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("sss", $c_key, $end_validity, $email);
         if(!$stmt->execute()) throw new Exception("Forgot Password : Inserimento fallito. Errore: ". getErrorConnection());
         $stmt->close();

         // Invio email con link
         $body = "<a style='text-decoration: underline;' href='"._SERVER_."/change_password.php?c_key=$c_key'> Reimposta Password </a>";
         sendEmail($email, "Reimposta Password", $body );

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

   static function update($user){
     try {
       if(connect()) {

         startTransaction();

         $keys = array();
         $values = array();

         foreach ($user as $key => $value) {
           if($key!=ID_USER && $key!=IMG){
             array_push($keys, $key);
             array_push($values, $value);
           }
         }
         array_push($values, $user[ID_USER]);


         $stmt = getConnection()->prepare("UPDATE user SET " .join("=?,", $keys) ."=?"." WHERE id_user=?");
         if(!$stmt) throw new Exception("User update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param(str_repeat('s', count($keys))."i", ...$values);

         if(!$stmt->execute()) throw new Exception("User : Update fallito. Errore: ". getErrorConnection());
         $stmt->close();

         if(isset($user[IMG])){
           $stmt = getConnection()->prepare("UPDATE profile_image SET img_encode=? WHERE id_user=?");
           if(!$stmt) throw new Exception("User Img update : Preparazione fallita. Errore: ". getErrorConnection());
           $stmt->bind_param("si", $user[IMG], $user[ID_USER]);

           if(!$stmt->execute()) throw new Exception("User Img : Update fallito. Errore: ". getErrorConnection());
           $stmt->close();

         }
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

   static function changePassword($id_user, $password){
     try {
       if(connect()) {

         startTransaction();
         $hash_crypt = hashed_password($password);
         $stmt = getConnection()->prepare("UPDATE login SET hash_password=? WHERE id_user=?");
         if(!$stmt) throw new Exception("Password update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("si", $hash_crypt, $id_user);

         if(!$stmt->execute()) throw new Exception("Password : Update fallito. Errore: ". getErrorConnection());
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

   static function delete($id_user){
     try {
       if(connect())
       {

         startTransaction();

         $stmt = getConnection()->prepare("DELETE FROM user WHERE id_user=?");
         if(!$stmt) throw new Exception("User delete : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("i", $id_user);
         if(!$stmt->execute()) throw new Exception("User : Delete fallito. Errore: ". getErrorConnection());
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
