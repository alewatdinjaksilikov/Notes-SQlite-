package com.example.lessonsqlite.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(context: Context):SQLiteOpenHelper(
    context,
    MyDbNameClass.DATABASE_NAME,
    null,
    MyDbNameClass.DATABASE_VERSION,){

    override fun onCreate(p0: SQLiteDatabase?) {
        //Создание БД
        p0?.execSQL(MyDbNameClass.CREATE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        //Обновление БД
        p0?.execSQL(MyDbNameClass.SQL_DELETE_TABLE)  // Удалить Таблицу
        onCreate(p0)  // Создаст новую БД
    }

}