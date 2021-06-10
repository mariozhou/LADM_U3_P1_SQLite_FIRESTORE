package mx.tecnm.tepic.ladm_u3_p1_sqlite_firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity4 : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        val btnactualizar = findViewById<Button>(R.id.button33)
        val btnregresar = findViewById<Button>(R.id.button44)
        val nombre = findViewById<EditText>(R.id.nombreactfire)
        val producto = findViewById<EditText>(R.id.productoactfire)
        val precio = findViewById<EditText>(R.id.precioactfire)

        var extra = intent.extras
        id = extra!!.getString("idElegido")!!

        baseRemota.collection("Apartado")
            .document(id)
            .get()
            .addOnSuccessListener {
                nombre.setText(it.getString("nombreCliente"))
                producto.setText(it.getString("Producto"))
                precio.setText(it.getDouble("Precio").toString())
            }
            .addOnFailureListener {
                alerta("Error! No existe ID ${id}")
            }

        btnactualizar.setOnClickListener {
            actualizar()
        }

        btnregresar.setOnClickListener {
            finish()
        }
    }

    private fun actualizar() {
        val nombre = findViewById<EditText>(R.id.nombreactfire)
        val producto = findViewById<EditText>(R.id.productoactfire)
        val precio = findViewById<EditText>(R.id.precioactfire)

        baseRemota.collection("Apartado")
            .document(id)
            .update("nombreCliente",nombre.text.toString(),
                "Producto",producto.text.toString(),
                "Precio",precio.text.toString().toFloat())
            .addOnSuccessListener { alerta("Exito se actualizo")
            }
            .addOnFailureListener { mensaje("Error no se pudo actualizar") }
    }

    fun mensaje(m: String){
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(m)
            .setPositiveButton("OK"){ d, i->}
            .show()
    }

    private fun alerta(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }
}