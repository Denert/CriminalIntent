package com.example.varia.criminalintent;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.varia.criminalintent.database.CrimeBaseHelper;
import com.example.varia.criminalintent.database.CrimeCursorWrapper;
import com.example.varia.criminalintent.database.CrimeDbSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static CrimeLab get(Context context){
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context;
        mDatabase = new CrimeBaseHelper(context).getWritableDatabase();
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime (UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeDbSchema.Cols.UUID + " = ?",
                new String[] {id.toString()}
                );
        try {
            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public boolean deleteCrime(UUID id) {
        int count = mDatabase.delete(CrimeDbSchema.CrimeTable.NAME, CrimeDbSchema.Cols.UUID + " = ?",
                new String[]{id.toString()});
        return count != 0;
    }

    public void updateCrime(Crime crime) {
        String uuids = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values, CrimeDbSchema.Cols.UUID + " = ?",
                new String[] {uuids});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null, //columns - c null выбираются все столбцы
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues content = new ContentValues();
        content.put(CrimeDbSchema.Cols.UUID, crime.getId().toString());
        content.put(CrimeDbSchema.Cols.TITLE, crime.getTitle());
        content.put(CrimeDbSchema.Cols.DATE, crime.getDate().getTime());
        content.put(CrimeDbSchema.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        return content;
    }

    public void addCrime(Crime crime) {
        ContentValues contentValues = getContentValues(crime);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, contentValues);
    }
}
