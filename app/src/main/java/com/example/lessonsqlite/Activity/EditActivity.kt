package com.example.lessonsqlite.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.lessonsqlite.R
import com.example.lessonsqlite.db.MyDbManager
import com.example.lessonsqlite.db.MyIntentConstants
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    var isEditState = false
    var id = 0
    val ImageRequestCode = 10 //номер запроса из галерее
    var tempImageUri = "empty" //переменный для сохр ссылки фото
    val myDbManager = MyDbManager(this) //БД

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        getMyIntents()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()

    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode==ImageRequestCode){

            image_from_gallery.setImageURI(data?.data) // ссылка на фото из галерее
            tempImageUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(data?.data!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //дает посстояную ссылку
        }
    }

    //кнопка для добавление фото
    fun onClickAddImage(view: View) {
        image_layout.visibility = View.VISIBLE
        fltbtn_addImage.visibility = View.GONE
    }

    //кнопка для удаление фото
    fun onClickDeleteImage(view: View) {
        image_layout.visibility = View.GONE
        fltbtn_addImage.visibility = View.VISIBLE
        tempImageUri = "empty"
    }

    //кнопка добавление фото из галерее
    fun onClickImageRedagtirovaniye(view: View){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT) // Открывает(Запрос) программы которые открывает фото
        intent.type = "image/*"
        startActivityForResult(intent,ImageRequestCode) // запрос на Activity Result
    }

    //Функция для сохранение всего данного на БД
    fun onClickSave(view: View){
        val myTitle = etTitle.text.toString()
        val content = etDesc.text.toString()

        if (myTitle.isNotEmpty() && content.isNotEmpty()){

            CoroutineScope(Dispatchers.Main).launch {
                if (isEditState) {
                    myDbManager.updateItem(myTitle, content, tempImageUri, id, getCurrentTime())
                } else {
                    myDbManager.insertToDb(myTitle, content, tempImageUri, getCurrentTime())
                }

                finish()
            }
        }
    }

    //Функция даёт доступ для редактирование или изменение текста
    fun onEditEnable(view: View){
        etTitle.isEnabled = true
        etDesc.isEnabled = true
        fltbtn_isEnabled.visibility = View.GONE
        fltbtn_addImage.visibility = View.VISIBLE
        if (tempImageUri=="empty")return
        imbuttonEdit.visibility = View.VISIBLE
        imbuttonDelete.visibility = View.VISIBLE
    }

    fun getMyIntents(){
        fltbtn_isEnabled.visibility = View.GONE
        val i = intent
        if (i != null){

            if (i.getStringExtra(MyIntentConstants.I_TITLE_KEY)!= null){

                fltbtn_addImage.visibility = View.GONE

                etTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                etDesc.setText(i.getStringExtra(MyIntentConstants.I_CONTENT_KEY))
                fltbtn_isEnabled.visibility = View.VISIBLE
                isEditState = true
                etTitle.isEnabled = false
                etDesc.isEnabled = false
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY,0)

                if (i.getStringExtra(MyIntentConstants.I_URI_KEY)!= "empty"){
                    image_layout.visibility = View.VISIBLE
                    tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                    image_from_gallery.setImageURI(Uri.parse(tempImageUri))
                    imbuttonDelete.visibility = View.GONE
                    imbuttonEdit.visibility = View.GONE
                }
            }

        }
    }

    //Функция времени
    private fun getCurrentTime():String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        val Ftime = formatter.format(time)
        return Ftime
    }
}