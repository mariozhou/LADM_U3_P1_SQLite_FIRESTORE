package mx.tecnm.tepic.ladm_u3_p1_sqlite_firestore

import android.content.ContentValues
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import mx.tecnm.tepic.ladm_u3_p1_sqlite_firestore.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
    var baseSQLite = BaseDatos(this,"prueba2",null,1)
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val nombreactualizar = findViewById<EditText>(R.id.nombreactualizar)
        val producto = findViewById<EditText>(R.id.productoactualizar)
        val precio = findViewById<EditText>(R.id.precioactualizar)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)

        var extra = intent.extras
        id = extra!!.getString("idactualizar")!!

        try {
            var transaccion = baseSQLite.readableDatabase

            var cursor = transaccion.query("Apartado", arrayOf("nombreCliente",
                "Producto","Precio"),"IdApartado=?",
                arrayOf(id),null,null,null)

            if (cursor.moveToFirst()){
                nombreactualizar.setText(cursor.getString(0))
                producto.setText(cursor.getString(1))
                precio.setText(cursor.getString(2))
            }else{
                mensaje("ERROR! no se pudo recuperar la DATA de ID ${id}")
            }
            transaccion.close()

        }catch (err: SQLiteException){
            mensaje(err.message!!)
        }
        button3.setOnClickListener {
            actualizar(id)
        }

        button4.setOnClickListener {
            finish() }

    }

    private fun actualizar(id:String) {
        val nombreactualizar = findViewById<EditText>(R.id.nombreactualizar)
        val productoactualizar = findViewById<EditText>(R.id.productoactualizar)
        val precioactualizar = findViewById<EditText>(R.id.precioactualizar)

        try {
            var transaccion = baseSQLite.writableDatabase
            var valores = ContentValues()

            valores.put("nombreCliente",nombreactualizar.text.toString())
            valores.put("Producto",productoactualizar.text.toString())
            valores.put("Precio",precioactualizar.text.toString().toFloat())
            var resultado = transaccion.update("Apartado",valores,"IdApartado=?",
                arrayOf(id))

            if (resultado>0){
                mensaje("se actualizo correctamente ID")
                finish()
            } else{
                mensaje("ERROR! no se actualizo")
            }
            transaccion.close()
        } catch (err: SQLiteException) {
            mensaje(err.message!!)
        }
    }

    fun mensaje(m:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(m)
            .setPositiveButton("OK"){d,i->}
            .show()
    }
}