package ru.dolbak.roomhomework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "results.db"
        ).build()

        val allData = db.resultsDao().getAll("RESULT DESC")

        allData.observe(this, Observer {
            companies ->
            GlobalScope.launch {
                displayStatics(companies)
            }
        })
    }

    private suspend fun displayStatics(companies: List<ResultEntity>){
        if(companies.isNotEmpty()) {
            withContext(Dispatchers.Main) {
                val total = companies.sumOf { it.result ?: 0 }
                findViewById<TextView>(R.id.money).text = total.toString()

                val avg = (total ?: 0) / companies.size

                findViewById<TextView>(R.id.good).text =
                    companies.count { (it.result ?: 0) > avg }.toString()


                val countEngName = companies.count { it.name?.any { c -> c.isLetter() && c !in 'а'..'я' } ?: false}
                findViewById<TextView>(R.id.english).text = countEngName.toString()

                findViewById<TextView>(R.id.best).text = companies.maxByOrNull { it.result ?: 0 }?.name ?: "-"

                findViewById<TextView>(R.id.longest).text = companies.maxByOrNull { it.name?.length ?: 0 }?.name ?: "-"

            }
        }
    }

}