package bredesh.medico;

import android.content.Context;

import java.util.Calendar;
import java.util.GregorianCalendar;

import bredesh.medico.DAL.Exercise;
import bredesh.medico.DAL.MedicoDB;

/**
 * Created by ophir on 6/9/2017.
 */

public class PointsCalculator {
    private MedicoDB db;
    public final int PointsPerExercise = 10;

    public PointsCalculator(Context context)
    {
         db = new MedicoDB(context);
    }


    private int dateDiff (Calendar startCal, Calendar endCal)
    {
        long endTime = endCal.getTimeInMillis();
        long startTime = startCal.getTimeInMillis();
        return (int) Math.floor( (endTime - startTime) / (1000*60*60*24));

    }
    private int getPossiblePointsForDateRange(Calendar startDate, Calendar endDate)
    {
        int result = 0;
        Exercise[] exercises = db.getAlerts();
        if (exercises != null) {
            for (int i = 0; i < exercises.length; i++)
            {
                for (int j=0; j <= dateDiff(startDate, endDate); j++)
                {
                    Calendar current = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                    current.add(Calendar.DAY_OF_MONTH, j);
                    int dayOfWeek = current.get(Calendar.DAY_OF_WEEK) - 1;
                    if (exercises[i].daysToPerform[dayOfWeek])
                        result += exercises[i].timesToPerform.length * PointsPerExercise;
                }
            }
        }
        return result;

    }

    public CalculatedPoints CalculatePoints(Calendar startDate, Calendar endDate)
    {
        CalculatedPoints result = new CalculatedPoints();
        result.gainedPoints = db.getTotalPointsByDates(startDate, endDate);
        result.possiblePoints = getPossiblePointsForDateRange(startDate, endDate);
        return result;
    }
}
