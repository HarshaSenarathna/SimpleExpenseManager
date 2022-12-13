package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentTransactionDAO implements TransactionDAO {

    private final PersistentDB sqlDB;

    public PersistentTransactionDAO(PersistentDB sqlDB) {
        this.sqlDB = sqlDB;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        try (SQLiteDatabase db = sqlDB.getWritableDatabase()) {
            ContentValues cv = new ContentValues();

            String expenseTypeText = (expenseType == ExpenseType.EXPENSE) ? "Expense" : "Income";

            @SuppressLint("SimpleDateFormat")
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);

            cv.put(PersistentDB.transaction_date, strDate);
            cv.put(PersistentDB.transaction_acc_no, accountNo);
            cv.put(PersistentDB.expense_type, expenseTypeText);
            cv.put(PersistentDB.transaction_amount, amount);

            db.insert(PersistentDB.transaction_table, null, cv);
        } catch (Exception e) {}
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        List<Transaction> transactions = new ArrayList<>();

        try (SQLiteDatabase db = sqlDB.getReadableDatabase()) {
            @SuppressLint("SimpleDateFormat")
            DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            String query = String.format("SELECT * FROM %s", PersistentDB.transaction_table);

            try (Cursor cursor = db.rawQuery(query, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        ExpenseType expenseType = (cursor.getString(3).equals("Expense")) ? ExpenseType.EXPENSE : ExpenseType.INCOME;
                        Date date;
                        try {
                            date = iso8601Format.parse(cursor.getString(1));
                        } catch (ParseException e) {
                            System.out.println(cursor.getString(1));
                            date = new Date();
                        }

                        transactions.add(
                                new Transaction(date, cursor.getString(2), expenseType, cursor.getDouble(4)));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {}
        } catch (Exception e) {}
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        List<Transaction> transactions = new ArrayList<>();

        @SuppressLint("SimpleDateFormat")
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String query = String.format("SELECT * FROM %s LIMIT ?", PersistentDB.transaction_table);

        try (SQLiteDatabase db = sqlDB.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(limit)})) {
                if (cursor.moveToFirst()) {

                    do {
                        ExpenseType expenseType = (cursor.getString(3).equals("Expense")) ? ExpenseType.EXPENSE : ExpenseType.INCOME;
                        Date date;
                        try {
                            date = iso8601Format.parse(cursor.getString(1));
                        } catch (ParseException e) {
                            System.out.println(cursor.getString(1));
                            date = new Date();
                        }

                        transactions.add(
                                new Transaction( date, cursor.getString(2), expenseType,cursor.getDouble(4))
                        );
                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {}
        } catch (Exception e) {}
        return transactions;
    }
}
