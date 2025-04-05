package com.recifemecatron.deolhonaconsulta.data.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DB(context: Context) : SQLiteOpenHelper(context, "consulta", null, 1) {

 // Cria a tabela 'consulta' com duas colunas: id (PK autoincrement) e cod_solicitacao
 private val sqlCreateConsulta = """
        CREATE TABLE consulta (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            cod_solicitacao TEXT
        )
    """

 private val sqlDropConsulta = "DROP TABLE IF EXISTS consulta"

 override fun onCreate(db: SQLiteDatabase) {
  db.execSQL(sqlCreateConsulta)
 }

 override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
  db.execSQL(sqlDropConsulta)
  onCreate(db)
 }
}
