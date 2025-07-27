<?php

namespace app\model;

use JsonSerializable;

abstract class Base implements JsonSerializable
{
    abstract public function check($object);

    abstract public function jsonSerialize();

    abstract public function toJson();
}


