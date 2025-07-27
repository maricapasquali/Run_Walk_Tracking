<?php

namespace app\database\interfaces;
interface ISessionDao
{
    public function create($id_user);

    public function checkForIdUser($id_user);

    public function checkForToken($session_token);

    public function update($id_user);

    public function setLastUpdate($last_update, $id_user);

    public function setNewDevice($device, $id_user);
}

