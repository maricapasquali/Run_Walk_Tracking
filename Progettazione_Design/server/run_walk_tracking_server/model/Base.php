
<?php

abstract class Base implements JsonSerializable {
  abstract function check($object);
  abstract function jsonSerialize();
  abstract function toJson();
}

 ?>
