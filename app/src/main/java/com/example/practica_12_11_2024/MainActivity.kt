package com.example.practica_12_11_2024

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var etNumEmp: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etSueldo: EditText
    private lateinit var btnAgregar: ImageButton
    private lateinit var btnBuscar: ImageButton
    private lateinit var btnActualizar: ImageButton
    private lateinit var btnEliminar: ImageButton
    private lateinit var btnLista: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNumEmp = findViewById(R.id.editTextNumEmp)
        etNombre = findViewById(R.id.editTextNombre)
        etApellidos = findViewById(R.id.editTextApellidos)
        etSueldo = findViewById(R.id.editTextSueldo)
        btnAgregar = findViewById(R.id.imageButtonAgregar)
        btnBuscar = findViewById(R.id.imageButtonBuscar)
        btnActualizar = findViewById(R.id.imageButtonActualizar)
        btnEliminar = findViewById(R.id.imageButtonEliminar)
        btnLista = findViewById(R.id.buttonLista)

        btnAgregar.setOnClickListener { registrarEmpleado() }
        btnBuscar.setOnClickListener { buscarEmpleado() }
        btnActualizar.setOnClickListener { actualizarEmpleado() }
        btnEliminar.setOnClickListener { eliminarEmpleado() }
        btnLista.setOnClickListener { listarRegistro() }
    }

    private fun listarRegistro() {
        val intent = Intent(this, Listado::class.java)
        startActivity(intent)
    }

    private fun eliminarEmpleado() {
        val numep = etNumEmp.text.toString()
        if (numep.isNotEmpty()) {
            SendDataTask().execute("delete", numep)
        } else {
            Toast.makeText(this, "Ingresa numero de empleado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarEmpleado() {
        val numep = etNumEmp.text.toString()
        val nombre = etNombre.text.toString()
        val apellidos = etApellidos.text.toString()
        val sueldo = etSueldo.text.toString()
        if (numep.isNotEmpty() && nombre.isNotEmpty() && apellidos.isNotEmpty() && sueldo.isNotEmpty()) {
            SendDataTask().execute("update", numep, nombre, apellidos, sueldo)
        } else {
            Toast.makeText(this, "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarEmpleado() {
        val numep = etNumEmp.text.toString()
        if (numep.isNotEmpty()) {
            SendDataTask().execute("select", numep)
        } else {
            Toast.makeText(this, "Ingresa numero de empleado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registrarEmpleado() {
        val numep = etNumEmp.text.toString()
        val nombre = etNombre.text.toString()
        val apellidos = etApellidos.text.toString()
        val sueldo = etSueldo.text.toString()
        if (numep.isNotEmpty() && nombre.isNotEmpty() && apellidos.isNotEmpty() && sueldo.isNotEmpty()) {
            SendDataTask().execute("insert", numep, nombre, apellidos, sueldo)
        } else {
            Toast.makeText(this, "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class SendDataTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            return try {
                val action = params[0]
                val url = URL("http://192.168.137.188/empleado.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                val postData = when (action) {
                    "insert" -> "action=insert&numep=${params[1]}&nombre=${params[2]}&apellidos=${params[3]}&sueldo=${params[4]}"
                    "select" -> "action=select&numep=${params[1]}"
                    "update" -> "action=update&numep=${params[1]}&nombre=${params[2]}&apellidos=${params[3]}&sueldo=${params[4]}"
                    "delete" -> "action=delete&numep=${params[1]}"
                    else -> ""
                }
                outputStreamWriter.write(postData)
                outputStreamWriter.flush()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Failed to send data: $responseCode"
                }
            } catch (e: Exception) {
                e.message ?: "Unknown error"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Toast.makeText(this@MainActivity, result, Toast.LENGTH_LONG).show()
        }
    }
}