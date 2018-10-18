package com.example.kajiwaratakuya.employeeapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EmployeeDb extends SQLiteOpenHelper {
    private static String DB_NAME = "employee.db.zip";
    private static String DB_NAME_ASSETS = "employee.db";
    private static int DB_VERSION = 1;
    static String SQLstring = null;
    private static Context mContext = null;

    private final File mDatabasePath;


    //データベースを作成またはオープン
    public EmployeeDb(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        mContext = context;
        mDatabasePath = mContext.getDatabasePath(DB_NAME);
    }


    /**
     * asset に格納したデータベースをコピーするための空のデータベースを作成する
     */

    public void createEmptyDataBase() throws IOException {

        boolean dbExist = checkDataBaseExists();

        if (dbExist) {
            // 既にDBが存在

        } else {

            // 空のデータベースをデフォルトシステムパスに作成
            SQLiteDatabase db_Read = this.getReadableDatabase();
            db_Read.close();

            try {
                unzipCopyDataBaseFromAsset();
                String dbPath = mDatabasePath.getAbsolutePath();
                SQLiteDatabase checkDb = null;
                try {
                    checkDb = SQLiteDatabase.openDatabase(dbPath, null,SQLiteDatabase.OPEN_READWRITE);
                } catch (SQLiteException e) {
                }

                if (checkDb != null) {
                    checkDb.setVersion(DB_VERSION);
                    checkDb.close();
                }

            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }


    /**
     * 再コピーを防止するために、すでにデータベースがあるかどうか判定する
     *
     * @return 存在している場合 {@code true}
     */
    private boolean checkDataBaseExists() {


        String dbPath = mDatabasePath.getAbsolutePath();

        SQLiteDatabase checkDb = null;
        try {
            checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

            // データベースはまだ存在していない
        }

        if (checkDb == null) {
            // データベースはまだ存在していない
            return false;
        }

        int oldVersion = checkDb.getVersion();
        int newVersion = DB_VERSION;
        if (oldVersion == newVersion) {
            // データベースは存在していて最新
            checkDb.close();
            return true;
        }
        // データベースが存在していて最新ではないので削除
        File f = new File(dbPath);
        f.delete();
        return false;
    }


    private void unzipCopyDataBaseFromAsset() throws IOException{
        try {
            //ZIPから解凍して結合;
            AssetManager assetManager = mContext.getResources().getAssets();
            InputStream is = assetManager.open(DB_NAME,AssetManager.ACCESS_STREAMING);
            ZipInputStream zipInputStream = new ZipInputStream(is);
            ZipEntry zipEntry        = zipInputStream.getNextEntry();
            if (zipEntry != null) {
                OutputStream mOutput = new FileOutputStream(mDatabasePath);
                byte[] buffer = new byte[1024];
                int size;
                while ((size = zipInputStream.read(buffer,0,buffer.length)) > -1) {
                    mOutput.write(buffer, 0, size);
                }
                mOutput.flush();
                mOutput.close();
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
