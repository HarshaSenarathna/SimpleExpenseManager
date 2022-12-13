package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private final PersistentDB sqlDB;

    public PersistentAccountDAO(PersistentDB sqlDB) {
        this.sqlDB = sqlDB;
    }

    @Override
    public List<String> getAccountNumbersList() {

        List<String> acc_num = new ArrayList<>();

        try (SQLiteDatabase db = sqlDB.getReadableDatabase()) {
            String query = String.format("SELECT %s FROM %s", PersistentDB.acc_name, PersistentDB.account_table);
            try (Cursor cursor = db.rawQuery(query, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        acc_num.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {}
        } catch (Exception e) {}

        return acc_num;
    }

    @Override
    public List<Account> getAccountsList() {

        List<Account> accountsList = new ArrayList<>();

        try (SQLiteDatabase db = sqlDB.getReadableDatabase()) {
            String query = String.format("SELECT * FROM %s", PersistentDB.account_table);
            try (Cursor cursor = db.rawQuery(query, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        accountsList.add(new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3)));
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {}
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        Account account;

        try (SQLiteDatabase db = sqlDB.getReadableDatabase()) {
            String query = String.format("SELECT * FROM %s WHERE %s = ?", PersistentDB.account_table, PersistentDB.acc_name);
            try (Cursor cursor = db.rawQuery(query, new String[]{accountNo})) {
                if (cursor.moveToFirst()) {
                    account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
                } else {
                    throw new InvalidAccountException("NO such account exists.");
                }
            } catch (InvalidAccountException invalidAccountException) {
                throw invalidAccountException;
            }
        } catch (InvalidAccountException invalidAccountException) {
            throw invalidAccountException;
        } catch (Exception e) {
            throw new InvalidAccountException("Could not fetch requested data");
        }
        return account;
    }

    @Override
    public void addAccount(Account account) {

        try (SQLiteDatabase db = sqlDB.getWritableDatabase()) {

            ContentValues cv = new ContentValues();

            cv.put(PersistentDB.acc_name, account.getAccountNo());
            cv.put(PersistentDB.bank_name, account.getBankName());
            cv.put(PersistentDB.acc_holder, account.getAccountHolderName());
            cv.put(PersistentDB.acc_balance, account.getBalance());

            db.insert(PersistentDB.account_table, null, cv);
        } catch (Exception e) {}
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        try (SQLiteDatabase db = sqlDB.getWritableDatabase()) {
            db.delete(PersistentDB.account_table, PersistentDB.acc_name + " = ?", new String[]{accountNo});
        } catch (Exception e) {
            throw new InvalidAccountException("Deleting account Failed.");
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = getAccount(accountNo);
        try (SQLiteDatabase db = sqlDB.getWritableDatabase()) {
            switch (expenseType) {
                case INCOME:
                    account.setBalance(account.getBalance() + amount);
                    break;
                case EXPENSE:
                    account.setBalance(account.getBalance() - amount);
                    break;
                default:
                    throw new InvalidAccountException("Updating Failed");
            }

            if (account.getBalance() < 0) {
                throw new InvalidAccountException("Insufficient balance.");
            }

            ContentValues cv = new ContentValues();
            cv.put(PersistentDB.acc_balance, account.getBalance());
            db.update(PersistentDB.account_table, cv, String.format("%s = ?", PersistentDB.acc_name), new String[]{accountNo});
        } catch (InvalidAccountException iae) {
            throw iae;
        } catch (Exception e) {
            throw new InvalidAccountException("Updating Failed");
        }
    }
}
