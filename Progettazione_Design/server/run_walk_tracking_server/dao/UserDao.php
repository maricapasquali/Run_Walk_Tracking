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
          if(!$stmt->execute()) {
            if(isDuplicate()) throw new UserJustSignUpExeception();
            throw new Exception("User : Inserimento fallito. Errore: ". getErrorConnection());
          }
          if(!$stmt->affected_rows) return;
          $stmt->close();

          $id = getConnection()->insert_id;

          $stmt = getConnection()->prepare("INSERT INTO login(id_user, username, hash_password) VALUES (?, ?, ?)");
          if(!$stmt) throw new Exception("Login : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("iss", $id, $username, $hash);
          $username = $user[USERNAME];
          $hash = hashed_password($user[PASSWORD]);
          if(!$stmt->execute()) throw new Exception("Login : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          $stmt = getConnection()->prepare("INSERT INTO target_default(id_user, target) VALUES (?, ?)");
          if(!$stmt) throw new Exception("Target : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("is", $id, $target);
          $target = $user[TARGET];
          if(!$stmt->execute()) throw new Exception("Target : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();


          $stmt = getConnection()->prepare("INSERT INTO weight(id_user, value) VALUES (?,?)");
          if(!$stmt) throw new Exception("Weight : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("id", $id, $weight);
          $weight = $user[WEIGHT];
          if(!$stmt->execute()) throw new Exception("Weight : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();

          if(isset($user[IMG])){
            $image = $user[IMG];
            $stmt = getConnection()->prepare("INSERT INTO profile_image(id_user, name, content) VALUES (?, ?, ?)");
            if(!$stmt) throw new Exception("Image : Preparazione fallita. Errore: ". getErrorConnection());
            $stmt->bind_param("iss", $id, $image[IMG_NAME],$image[IMG_ENCODE]);
            if(!$stmt->execute()) throw new Exception("Image : Inserimento fallito. Errore: ". getErrorConnection());
            $stmt->close();
          }

          $token = getToken(10);

          $stmt = getConnection()->prepare("INSERT INTO signup(id_user, token, date) VALUES (?, ?, ?)");
          if(!$stmt) throw new Exception("Token : Preparazione fallita. Errore: ". getErrorConnection());
          $stmt->bind_param("iss", $id, $token, current_unixdatetime());
          if(!$stmt->execute()) throw new Exception("Token : Inserimento fallito. Errore: ". getErrorConnection());
          $stmt->close();
          commitTransaction();

          // Invio email con token
          $doc = docHtml(_SERVER_."/emails/sign_up.php?id=".$id);
          $findMe = $doc->getElementById("token");
          if(is_object($findMe)) $findMe->nodeValue = $token;
          sendEmail($email, "Sign Up", $doc->saveHTML());
        }
      }catch (Exception $e) {
        rollbackTransaction();

        throw new Exception($e->getMessage());
        return;
      }
      closeConnection();
      return true;
   }

   static function checkSignUp($userCredentials){
     $id_user = self::checkSignIn($userCredentials[USERNAME], $userCredentials[PASSWORD]);
     if(connect()){

         $stmt = getConnection()->prepare("SELECT count(*) FROM session WHERE id_user=?");
         if(!$stmt) throw new Exception("Check Token : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("i", $id_user);
         if(!$stmt->execute()) throw new Exception("Check Token: Selezione fallita. Errore: ". getErrorConnection());
         $stmt->bind_result($justLogged);
         $stmt->fetch();
         $stmt->close();
         if($justLogged>0) throw new UserConfirmExeception();

         $stmt = getConnection()->prepare("SELECT count(*) FROM signup WHERE id_user=? and token=?");
         if(!$stmt) throw new Exception("Check Token : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("is", $id_user, $userCredentials[TOKEN]);
         if(!$stmt->execute()) throw new Exception("Check Token: Selezione fallita. Errore: ". getErrorConnection());
         $stmt->bind_result($isCorrect);
         $stmt->fetch();
         $stmt->close();
         if($isCorrect==0) throw new SignUpTokenExeception();
     }
     closeConnection();
     return $id_user;
   }

   static function checkSignIn($username, $password){
     if(connect()){
         $stmt = getConnection()->prepare("SELECT id_user, hash_password FROM login WHERE username=?");
         if(!$stmt) throw new Exception("user id : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("s", $username);
         if(!$stmt->execute()) throw new Exception("Check : Selezione fallita. Errore: ". getErrorConnection());
         $stmt->bind_result($id_user, $hash_password);
         $stmt->fetch();
         $stmt->close();

         if($id_user==NULL && $hash_password==NULL) throw new UserExeception();

         if(!password_verify($password, $hash_password)) throw new PasswordExeception();
     }
     closeConnection();
     return $id_user;
   }

   static function getUserForId($id){
     $user = array();

     if(connect()){
        $stmt = getConnection()->prepare("SELECT u.*, birth_date, l.username FROM login l JOIN user u on(l.id_user=u.id_user) WHERE u.id_user=?");
        if(!$stmt) throw new Exception("user from id : Preparazione fallita. Errore: ". getErrorConnection());
        $stmt->bind_param("i", $id);
        if(!$stmt->execute()) throw new Exception("user from id : Selezione fallita. Errore: ". getErrorConnection());
        $result = $stmt->get_result();
        $user = $result->fetch_assoc();
        if(count($user)==0) throw new UserExeception();
        $stmt->close();
     }
     closeConnection();
     return $user;
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
        if($id_user==NULL) throw new UserExeception();
        $stmt->close();
     }
     closeConnection();
     return array(ID_USER=>$id_user);
   }

   static function allData($session_token, $_device){
     $data= array();
     if(connect()){
         $stmt = getConnection()->prepare("SELECT id_user, device FROM session WHERE token=? ");
         if(!$stmt) throw new Exception("Check Token : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("s", $session_token);
         if(!$stmt->execute()) throw new Exception("Check Token: Selezione fallita. Errore: ". getErrorConnection());
         $stmt->bind_result($id_user, $device);
         $stmt->fetch();
         $stmt->close();
         if($id_user==NULL) throw new UserExeception();
     }
     closeConnection();

     $isDifferentDevice = $device==NULL || (strcmp($device, md5($_device))!=0);
     if($isDifferentDevice){
       SessionDao::setNewDevice($_device, $id_user);
     }

     return  array( USER => self:: getUserForId($id_user) + self::getImageProfileForIdUser($id_user, $isDifferentDevice),
                    WEIGHTS => WeightDao::getAllForUser($id_user),
                    WORKOUTS => WorkoutDao::getAllForUser($id_user),
                    SETTINGS => SettingsDao::getAllForUser($id_user));
   }

   static function getImageProfileForIdUser($id, $isDifferentDevice){
        if(connect()){
           $stmt = getConnection()->prepare("SELECT name " .($isDifferentDevice? ", content ": "")  ." FROM profile_image WHERE id_user=?");
           if(!$stmt) throw new Exception("user image id : Preparazione fallita. Errore: ". getErrorConnection());
           $stmt->bind_param("i", $id);
           if(!$stmt->execute()) throw new Exception("user image : Selezione fallita. Errore: ". getErrorConnection());
           $result = $stmt->get_result();
           $image = $result->fetch_assoc();
           $stmt->close();
        }
        closeConnection();
        return array(IMG=>$image);
   }

   static function getImageProfileForIdUserAndName($id, $name){
        if(connect()){
           $stmt = getConnection()->prepare("SELECT name, content FROM profile_image WHERE id_user=? and name = ?");
           if(!$stmt) throw new Exception("user image id : Preparazione fallita. Errore: ". getErrorConnection());
           $stmt->bind_param("is", $id, $name);
           if(!$stmt->execute()) throw new Exception("user image : Selezione fallita. Errore: ". getErrorConnection());
           $result = $stmt->get_result();
           $image = $result->fetch_assoc();
           $stmt->close();
           if($image==NULL) throw new ImageException();
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

         if($stmt->affected_rows){
           // Invio email con link
           $doc = docHtml(_SERVER_."/emails/email_support_password.php");
           $findMe = $doc->getElementById("link_change_password");
           if (is_object($findMe)) $findMe->setAttribute('href',_SERVER_.'/emails/change_password.php?c_key='.$c_key);
           sendEmail($email, "Reset Password", $doc->saveHTML());
         }

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

   static function update($user, $id_user){
     $update = false;
     try {
       if(connect()) {

         startTransaction();

         $keys = array();
         $values = array();

         foreach ($user as $key => $value) {
           if($key!=IMG){
             array_push($keys, $key);
             array_push($values, $value);
           }
         }

         array_push($values, $id_user);

         if(count($values)>1){
           $stmt = getConnection()->prepare("UPDATE user SET " .join("=?,", $keys) ."=?"." WHERE id_user=?");
           if(!$stmt) throw new Exception("User update : Preparazione fallita. Errore: ". getErrorConnection());
           $stmt->bind_param(str_repeat('s', count($keys))."i", ...$values);
           if(!$stmt->execute()) throw new Exception("User : Update fallito. Errore: ". getErrorConnection());
           $update = $update || $stmt->affected_rows;
           $stmt->close();
         }

         if(isset($user[IMG])){
           $image = $user[IMG];
           $stmt = getConnection()->prepare("INSERT INTO profile_image (content, name, id_user) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE content=?, name=?;");
           if(!$stmt) throw new Exception("User Img update : Preparazione fallita. Errore: ". getErrorConnection());
           $stmt->bind_param("ssiss", $image[IMG_ENCODE],$image[NAME], $id_user, $image[IMG_ENCODE],$image[NAME]);
           if(!$stmt->execute()) throw new Exception("User Img : Update fallito. Errore: ". getErrorConnection());
           $update = $update || $stmt->affected_rows;
           $stmt->close();
         }else{
           $stmt = getConnection()->prepare("DELETE FROM profile_image WHERE id_user=?");
           if(!$stmt) throw new Exception("User Img update : Preparazione fallita. Errore: ". getErrorConnection());
           $stmt->bind_param("i",$id_user);
           if(!$stmt->execute()) throw new Exception("User Img : Update fallito. Errore: ". getErrorConnection());
           $update = $update || $stmt->affected_rows;
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
     return $update;
   }

   static function changePassword($password, $id_user){
     try {
       if(connect()) {

         startTransaction();
         $hash_crypt = hashed_password($password);
         $stmt = getConnection()->prepare("UPDATE login SET hash_password=? WHERE id_user=?");
         if(!$stmt) throw new Exception("Password update : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("si", $hash_crypt, $id_user);
         if(!$stmt->execute()) throw new Exception("Password : Update fallito. Errore: ". getErrorConnection());
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

   static function delete($id_user){
     try {
       if(connect())
       {

         startTransaction();

         $stmt = getConnection()->prepare("DELETE FROM user WHERE id_user=?");
         if(!$stmt) throw new Exception("User delete : Preparazione fallita. Errore: ". getErrorConnection());
         $stmt->bind_param("i", $id_user);
         if(!$stmt->execute()) throw new Exception("User : Delete fallito. Errore: ". getErrorConnection());
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
