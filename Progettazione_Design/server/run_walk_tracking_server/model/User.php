<?php
require_once("Base.php");

class Image   extends Base {

    private $name;
    private $content;

    public function __construct($imageObject){
        $this->name = $imageObject->name;
        $this->content = $imageObject->content;
    }

    public function check($userObject){
      return false;
    }

    public function jsonSerialize() {
      return get_object_vars($this);
    }

    public function toJson(){
      return json_encode($this);
    }
}

class User    extends Base {

  private $id_user;

  private $name;
  private $last_name;
  private $gender;
  private $birth_date;
  private $email;
  private $phone;
  private $city;
  private $height;

  private $target;

  private $weight;

  private $username;
  private $password;

  private $image;

  public function __construct($userObject){
    if(self::check($userObject)) throw new UrlExeception();

    $this->name = $userObject->name;
    $this->last_name = $userObject->last_name;
    $this->gender = $userObject->gender;
    $this->birth_date = formatDate($userObject->birth_date);
    $this->email = $userObject->email;
    $this->phone = $userObject->phone;
    $this->city = $userObject->city;
    $this->height = $userObject->height;

    $this->target = $userObject->target;
    $this->weight = $userObject->weight;

    $this->username = $userObject->username;
    $this->password = hashed_password($userObject->password);

    if(isset($userObject->id_user))
      $this->id_user = $userObject->id_user;

      if(isset($userObject->image))
        $this->image = new Image((object)$userObject->image);
  }

  public function setId($id){
    $this->id_user = $id;
  }

  public function hasImageProfile(){
    return !is_null($this->image);
  }

  public function check($userObject){
    return !isset($userObject->name) ||
           !isset($userObject->last_name) ||
           !isset($userObject->gender) ||
           !isset($userObject->birth_date) ||
           !isset($userObject->email) ||
           !isset($userObject->phone) ||
           !isset($userObject->city) ||
           !isset($userObject->height) ||
           !isset($userObject->target) ||
           !isset($userObject->weight) ||
           !isset($userObject->username) ||
           !isset($userObject->password);
  }

  public function jsonSerialize() {
    return get_object_vars($this);
  }

  public function toJson(){
    return json_encode($this);
  }
}


?>
