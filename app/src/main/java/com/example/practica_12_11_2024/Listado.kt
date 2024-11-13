package com.example.practica_12_11_2024

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class Listado : AppCompatActivity() {

    private lateinit var etListado: EditText
    private lateinit var btnRegresar: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        etListado = findViewById(R.id.editDetalle)
        btnRegresar = findViewById(R.id.btnRegresar)

        FetchDataTask().execute()

        btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private inner class FetchDataTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            return try {
                val url = URL("http://192.168.137.188/empleado.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = "action=select_all"
                connection.outputStream.use { it.write(postData.toByteArray()) }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Failed to fetch data: ${connection.responseCode}"
                }
            } catch (e: Exception) {
                e.message ?: "Unknown error"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null && result != "Failed to fetch data") {
                etListado.setText(result)
            } else {
                Toast.makeText(this@Listado, result ?: "Sin registro de empleados", Toast.LENGTH_LONG).show()
            }
        }
    }
}