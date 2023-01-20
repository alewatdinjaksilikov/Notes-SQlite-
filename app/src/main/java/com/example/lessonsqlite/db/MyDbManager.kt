package com.example.lessonsqlite.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.widget.SearchView
import com.example.lessonsqlite.entity.ListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManager(context: Context) {
    var myDBHelper = MyDBHelper(context)
    var db : SQLiteDatabase? = null

    //Функция открытые БД
    fun openDb(){
        db = myDBHelper.writableDatabase
    }

    // insert - Добавить в БД
    suspend fun insertToDb(title:String,content:String,uri:String,time:String) = withContext(Dispatchers.IO){
        var values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE,title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT,content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI,uri)
            put(MyDbNameClass.COLUMN_NAME_TIME,time)
        }
        db?.insert(MyDbNameClass.TABLE_NAME,null,values)
    }

    //Чтитать БД
    suspend fun readDbData(searchViewText:String): ArrayList<ListItem> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<ListItem>()
        var selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME, null, selection, arrayOf("%$searchViewText%"),
            null, null, null
        )
        while (cursor?.moveToNext()!!) {
            val dataID =
                cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val dataTitle =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TITLE))
            val dataContent =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_CONTENT))
            val dataUri =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
            val dataTime =
                cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TIME))
            val item = ListItem()
            item.id = dataID
            item.title = dataTitle
            item.content = dataContent
            item.uri = dataUri
            item.time = dataTime
            dataList.add(item)
        }
        cursor.close()
        return@withContext dataList
    }

    //Обновление данных
    suspend fun updateItem(title:String,content:String,uri:String,id:Int,time:String) = withContext(Dispatchers.IO){
        val selection = BaseColumns._ID + "=$id"
        var values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE,title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT,content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI,uri)
            put(MyDbNameClass.COLUMN_NAME_TIME,time)
        }
        db?.update(MyDbNameClass.TABLE_NAME,values,selection,null)
    }

    //Функция удаление данных из БД
    fun removeItemFromDb(id:String){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME,selection,null)
    }

    //Функ. закрытие БД
    fun closeDb(){
        myDBHelper.close()
    }
}