<?php

namespace app\model;

class UserImage extends Base
{

    private $name;
    private $content;

    public function __construct($imageObject)
    {
        $this->name = $imageObject->name;
        $this->content = $imageObject->content;
    }

    public function check($userObject)
    {
        return false;
    }

    public function jsonSerialize()
    {
        return get_object_vars($this);
    }

    public function toJson()
    {
        return json_encode($this);
    }
}

