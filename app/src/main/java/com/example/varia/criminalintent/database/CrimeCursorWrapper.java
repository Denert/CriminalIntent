package com.example.varia.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.varia.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeDbSchema.Cols.UUID));
        String title = getString(getColumnIndex(CrimeDbSchema.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDbSchema.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDbSchema.Cols.SOLVED));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);

        return crime;
    }
}
