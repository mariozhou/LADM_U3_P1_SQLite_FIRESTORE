package mx.tecnm.tepic.ladm_u3_p1_sqlite_firestore

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity3 : AppCompatActivity() { 
    var baseRemota = FirebaseFirestore.getInstance()
    var datalista = ArrayList<String>()
    var listaId = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val btnregresar = findViewById<Button>(R.id.regresar)
        val lista = findViewById<ListView>(R.id.listanombre)

        var timer = object : CountDownTimer(2000,280){
            override fun onTick(p0: Long) {
                cargalista()
            }
            override fun onFinish() {
                start()
            }
        }.start()

        lista.setOnItemClickListener{  adapterView, view, posicion,l ->
            dialogoEliminaActualiza(posicion)
        }

        btnregresar.setOnClickListener {
            finish()
        }

    }

    private fun cargalista() {
        val lista = findViewById<ListView>(R.id.listanombre)

        baseRemota.collection("Apartado")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null){
                    mensaje(error.message!!)
                    return@addSnapshotListener
                }
                datalista.clear()
                listaId.clear()
                for (document in querySnapshot!!){
                    var cadena = "Id:${document.getId()}- Nombre:${document.getString("nombreCliente")}- Prodcto:${document.getString("Producto")}- Precio:${document.get("Precio")}"
                    datalista.add(cadena)
                    listaId.add(document.id.toString())
                }

            }
        lista.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalista)

        lista.setOnItemClickListener{
                adapterView, view, posicion,l ->
            dialogoEliminaActualiza(posicion)

     }

    }

    private fun dialogoEliminaActualiza(posicion: Int) {
        var idElegido = listaId.get(posicion)

        AlertDialog.Builder(this)
            .setTitle("Atencio")
            .setMessage("Que desea hacer con\n${datalista.get(posicion)}?")
            .setPositiveButton("Eliminar"){d,i->
                eliminar(idElegido)
            }
            .setNeutralButton("Actualizar"){d,i->
                var intent = Intent(this, MainActivity4::class.java)
                intent.putExtra("idElegido", idElegido)
                startActivity((intent))
            }
            .setNegativeButton("Cancelar"){d,i->
                d.dismiss()
            }
            .show()
    }

    fun eliminar(idElegido:String){
        baseRemota.collection("Apartado")
            .document(idElegido)
            .delete()
            .addOnSuccessListener {
                alerta("Se elimino con exito")
            }
            .addOnFailureListener { mensaje("Error: ${it.message!!}") }
        cargalista()
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


