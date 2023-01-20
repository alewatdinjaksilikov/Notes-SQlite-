package com.example.lessonsqlite.Activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lessonsqlite.R
import com.example.lessonsqlite.RecyclerView.MyAdapter
import com.example.lessonsqlite.db.MyDbManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val myDbManager = MyDbManager(this)
    val adapter = MyAdapter(ArrayList(),this)
    var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initSearchView()
    }

    fun init(){
        val swapHelper = getSwapManager()
        swapHelper.attachToRecyclerView(recycleView)
        recycleView.adapter = adapter
    }

    //Инициализация SearchView
    fun initSearchView(){
        searchView.setOnQueryTextListener(object:androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fillAdapter(newText!!)
                return true
            }

        })
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter("") // в onResume потому что когда перейдем в другой Activity onCreate не сработает и данные не обновляются

    }
    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickNew(view: View){
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }

    //fill-наполнять
    fun fillAdapter(text:String){
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val list = myDbManager.readDbData(text)
            adapter.updateAdapter(list)
            if (list.size > 0) {
                tvNoElements.visibility = View.GONE
            } else {
                tvNoElements.visibility = View.VISIBLE
            }
        }
    }

    //функция swipe-а
    private fun getSwapManager():ItemTouchHelper{
        return ItemTouchHelper(object : ItemTouchHelper
        .SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setMessage("Вы действительно хотите удалить?")
                    .setPositiveButton("Да"){dialogInterface,which ->
                        adapter.removeItem(viewHolder.adapterPosition, myDbManager)
                    }
                    .setNegativeButton("No"){dialogInterface, which ->
                    finish()
                    }
                val alertDialog : AlertDialog = dialog.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        })
    }

}