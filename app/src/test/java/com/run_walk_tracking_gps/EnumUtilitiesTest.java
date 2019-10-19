package com.run_walk_tracking_gps;

import com.run_walk_tracking_gps.model.enumerations.Gender;
import com.run_walk_tracking_gps.model.enumerations.Sport;
import com.run_walk_tracking_gps.model.enumerations.Target;
import com.run_walk_tracking_gps.utilities.EnumUtilities;

import org.junit.Assert;
import org.junit.Test;

public class EnumUtilitiesTest {

    @Test
    public void testMethods(){

        Integer[] values = EnumUtilities.valuesStrId(Target.class);
        Assert.assertArrayEquals(new Integer[]{Target.MARATHON.getStrId(),Target.LOSE_WEIGHT.getStrId()}, values);

        values = EnumUtilities.valuesStrId(Sport.class);
        Assert.assertArrayEquals(new Integer[]{Sport.RUN.getStrId(),Sport.WALK.getStrId()}, values);

        values = EnumUtilities.valuesStrId(Gender.class);
        Assert.assertArrayEquals(new Integer[]{Gender.FEMALE.getStrId(), Gender.MALE.getStrId()
                , Gender.OTHER.getStrId()}, values);

        values = EnumUtilities.valuesWithDescription(Target.class);
        Assert.assertArrayEquals(new Integer[]{R.string.target, Target.MARATHON.getStrId(),Target.LOSE_WEIGHT.getStrId()}, values);

        values = EnumUtilities.valuesWithDescription(Sport.class);
        Assert.assertArrayEquals(new Integer[]{R.string.sport,Sport.RUN.getStrId(),Sport.WALK.getStrId()}, values);

        values = EnumUtilities.valuesWithDescription(Gender.class);
        Assert.assertArrayEquals(new Integer[]{R.string.gender,Gender.FEMALE.getStrId(), Gender.MALE.getStrId()
                , Gender.OTHER.getStrId()}, values);


        boolean isNotDes = EnumUtilities.isNotDescription(Gender.class, Gender.MALE.getStrId());
        Assert.assertEquals(true, isNotDes);

        isNotDes = EnumUtilities.isNotDescription(Target.class, R.string.target);
        Assert.assertEquals(false, isNotDes);


        int icon = EnumUtilities.iconOfStrId(Sport.class, Sport.RUN.getStrId());
        Assert.assertEquals(R.drawable.ic_run, icon);

        icon = EnumUtilities.iconOfStrId(Gender.class, Gender.OTHER.getStrId());
        Assert.assertEquals(R.drawable.ic_other, icon);

    }
}
