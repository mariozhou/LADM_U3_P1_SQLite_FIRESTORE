package mx.tecnm.tepic.ladm_u3_p1_sqlite_firestore

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    var baseSQLite = BaseDatos(this, "prueba2", null, 1)
    var baseRemota = FirebaseFirestore.getInstance()
    var listaId = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        val btnsincro = findViewById<Button>(R.id.sincro)
        val btnfire = findViewById<Button>(R.id.firestore)
        cargarContacto()

        button.setOnClickListener {
            insertar()
        }

        button2.setOnClickListener {
            consulta()
        }

        btnsincro.setOnClickListener {
            sincro()
        }

        btnfire.setOnClickListener {
            var intent = Intent(this, MainActivity3::class.java)
            startActivity((intent))
        }
    }

    private fun insertar() {
        val id1 = findViewById<EditText>(R.id.id)
        val nombre = findViewById<EditText>(R.id.nombre)
        val producto = findViewById<EditText>(R.id.producto)
        val precio = findViewById<EditText>(R.id.precio)

        try {
            var transaccion = baseSQLite.writableDatabase

            var SQL ="INSERT INTO Apartado VALUES('${id1.text.toString().toInt()}', '${nombre.text.toString()}','${producto.text.toString()}','${precio.text.toString().toFloat()}')"
            transaccion.execSQL(SQL)

            transaccion.close()
            limpiarCampos()
            cargarContacto()

        }catch (err: SQLiteException){
            mensaje(err.message.toString())
        }
    }

    private fun consulta() {
        val id = findViewById<EditText>(R.id.id)
        val resultado = findViewById<TextView>(R.id.resultado)

        try {
            var transaccion = baseSQLite.readableDatabase
            var idABuscar = id.text.toString()
            var cursor = transaccion.query(
                "Apartado", arrayOf(
                    "nombreCliente",
                    "Producto",
                    "Precio"
                ), "IdApartado=?",
                arrayOf(idABuscar), null, null, null
            )

            if (cursor.moveToFirst()){
                resultado.setText(
                    "nombreCliente: ${cursor.getString(0)}\n " +
                            "Producto ${cursor.getString(1)}\n Precio: ${cursor.getString(2)}"
                )
            } else {
                mensaje(("ERROR! No se encontro resultado tras la consulta"))
            }
            transaccion.close()
            limpiarCampos()
            cargarContacto()
        }catch (err: SQLiteException){
            mensaje(err.message!!)
        }
    }

    private fun sincro() {
// fot
        var transaccion = baseSQLite.readableDatabase
        var apartado = ArrayList<String>()
        var cursos = transaccion.query("Apartado", arrayOf("*"), null, null, null, null, null)

        if(cursos.moveToFirst()){
            do {
                var nombre = cursos.getString(1)
                var producto = cursos.getString(2)
                var precio = cursos.getFloat(3)

                //  apartado.add(data)
                //   listaId.add(cursos.getInt(0).toString())

                var datosInsertar = hashMapOf(
                    //  "IdApartado" to id.text.toString(),
                    "nombreCliente" to nombre,
                    "Producto" to producto,
                    "Precio" to precio
                )

                baseRemota.collection("Apartado")
                        .add(datosInsertar as Any)
                        .addOnSuccessListener {
                            alerta("SE INSERTO CORRECTAMENTE EN LA NUBE")
                        }
                        .addOnFailureListener{
                            mensaje("ERROR: ${it.message!!}")
                        }
            } while (cursos.moveToNext())
        }
        try {
            var transaccion = baseSQLite.writableDatabase

            var SQL ="DELETE FROM Apartado"
            transaccion.execSQL(SQL)
            transaccion.close()
            cargarContacto()
        }catch (err: SQLiteException){
            mensaje(err.message.toString())
        }
    }

    fun cargarContacto() {
        val listacontactos = findViewById<ListView>(R.id.listacontactos)

        try {
            var transaccion = baseSQLite.readableDatabase
            var apartado = ArrayList<String>()
            var cursos = transaccion.query("Apartado", arrayOf("*"), null, null, null, null, null)

            if(cursos.moveToFirst()){
                listaId.clear()
                do {
                    var data = "["+ cursos.getInt(0)+"] - "+cursos.getString(1)+" - "+ cursos.getString(
                        2
                    )+" - "+ cursos.getString(3)

                    apartado.add(data)
                    listaId.add(cursos.getInt(0).toString())
                } while (cursos.moveToNext())
            } else {
                apartado.add("NO HAY DATOS CAPTURADOS AUN")
            }

            listacontactos.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                apartado
            )

            listacontactos.setOnItemClickListener{ adaterView, view, posicionInteSelecionado, l ->
                //mensaje(" ID recuperado: "+ listaId.get(posicionInteSelecionado)) ver id
                var idABorrar = listaId.get(posicionInteSelecionado)
                AlertDialog.Builder(this)
                        .setMessage("que deseas hacer con el ID: " + idABorrar + "?")
                        .setTitle("Atencion")
                        .setPositiveButton("Eliminar"){ d, i->
                            eliminar(idABorrar)
                        }
                        .setNegativeButton("Cancelar"){ d, i->
                            d.dismiss()
                        }
                        .setNeutralButton("Actualizar"){ d, i ->
                            var intent = Intent(this, MainActivity2::class.java)
                            intent.putExtra("idactualizar", idABorrar)
                            startActivity((intent))
                        }
                        .show()
            }
            transaccion.close()
        }catch (err: SQLiteException){
            mensaje(err.message!!)
        }
    }//cargar

    fun eliminar(idABorrar: String){
        try {
            var transaccion = baseSQLite.writableDatabase

            var resultado = transaccion.delete("Apartado", "IdApartado=?", arrayOf(idABorrar))

            if (resultado==0){
                mensaje("No se encontro el ID " + idABorrar + " \nNo se pudo eliminar")
            }
            transaccion.close()
            cargarContacto()

        }catch (err: SQLiteException){
            mensaje(err.message!!)
        }
    }

    private fun limpiarCampos() {
    val id = findViewById<EditText>(R.id.id)
    val nombre = findViewById<EditText>(R.id.nombre)
    val producto = findViewById<EditText>(R.id.producto)
    val precio = findViewById<EditText>(R.id.precio)

    id.setText("")
    nombre.setText("")
    producto.setText("")
    precio.setText("")
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