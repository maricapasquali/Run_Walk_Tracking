<?php

namespace app\database\interfaces;
interface IWeightDao
{
    public function create($weight);

    public function getAllForUser($id_user);

    public function update($weight, $id_user);

    public function updateAll($weights, $id_user);

    public function delete($id_weight);
}

