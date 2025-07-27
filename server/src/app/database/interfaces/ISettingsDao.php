<?php

namespace app\database\interfaces;
interface ISettingsDao
{
    public function getAllForUser($id_user);

    public function updateSportFor($sport, $id_user);

    public function updateTargetFor($target, $id_user);

    public function updateUnitHeightFor($height, $id_user);

    public function updateUnitWeightFor($weight, $id_user);

    public function updateUnitDistanceFor($distance, $id_user);

    public function updateUnits($unit_measure, $id_user);
}

