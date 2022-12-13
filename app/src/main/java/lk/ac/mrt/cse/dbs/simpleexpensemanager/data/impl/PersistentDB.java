package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class PersistentDB extends SQLiteOpenHelper {

    // account table
    public static String account_table = "account";
    public static String acc_name = "account_no";
    public static String bank_name = "bank_name";
    public static String acc_holder = "account_holder_name";
    public static String acc_balance = "balance";

    // transaction table
    public static String transaction_table = "transaction_log";
    public static String transaction_id = "id";
    public static String transaction_date = "date";
    public static String transaction_acc_no = "account_no";
    public static String expense_type = "expense_type";
    public static String transaction_amount = "amount";


    public PersistentDB(@Nullable Context context) {
        super(context, "200595E_DB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "".concat(String.format("CREATE TABLE %s (", account_table))
                        .concat(String.format(" %s VARCHAR(20) PRIMARY KEY, ", acc_name))
                        .concat(String.format(" %s VARCHAR(50) NOT NULL,", bank_name))
                        .concat(String.format(" %s VARCHAR(50) NOT NULL,", acc_holder))
                        .concat(String.format(" %s DECIMAL(15, 2) DEFAULT 0 ", acc_balance))
                        .concat(" )")
        );

        sqLiteDatabase.execSQL(
                "".concat(String.format("CREATE TABLE %s (", transaction_table))
                        .concat(String.format(" %s INTEGER PRIMARY KEY AUTOINCREMENT,", transaction_id))
                        .concat(String.format(" %s DATE NOT NULL,", transaction_date))
                        .concat(String.format(" %s VARCHAR(20) NOT NULL,", transaction_acc_no))
                        .concat(String.format(" %s VARCHAR(20) NOT NULL,", expense_type))
                        .concat(String.format(" %s DECIMAL(15, 2) DEFAULT 0", transaction_amount))
                        .concat(" )")
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
