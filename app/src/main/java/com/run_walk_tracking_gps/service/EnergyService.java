package com.run_walk_tracking_gps.service;

import android.content.Context;

import com.run_walk_tracking_gps.controller.DefaultPreferencesUser;
import com.run_walk_tracking_gps.model.enumerations.Sport;

public class EnergyService implements WorkoutServiceHandler.OnEnergyListener {

    /**
     * Corsa:     1,0 kcal x peso corporeo in kg x distanza percorsa in km
     * Camminata: 0,5 kcal x peso corporeo in kg x distanza percorsa in km
     */
    private final double ENERGY_RUN  = 1.0;
    private final double ENERGY_WALK = 0.5;

    private Sport sport;
    private double weight;

    public EnergyService(Context context, double weightInKg){
        super();
        this.weight = weightInKg;
        this.sport = DefaultPreferencesUser.getSportDefault(context);
    }

    @Override
    public double getEnergyInKcal(double km) {
        switch (sport){
            case RUN:
                return ENERGY_RUN*weight*km;
            case WALK:
                return ENERGY_WALK*weight*km;
        }
        return 0;
    }
}
