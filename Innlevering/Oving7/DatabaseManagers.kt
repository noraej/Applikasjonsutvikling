package com.noraej.oving7.managers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DatabaseManagers(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        const val DATABASE_NAME = "MoviesDatabase"
        const val DATABASE_VERSION = 1

        const val ID = "_id"

        const val TABLE_MOVIE = "MOVIE"
        const val MOVIE_NAME = "name"
        const val MOVIE_DIRECTOR = "director"

        const val TABLE_ACTOR = "ACTOR"
        const val ACTOR_NAME = "name"
        const val MOVIE_ID = "movie"

        val JOIN_MOVIE_ACTOR = arrayOf(
            "$TABLE_MOVIE.$ID=$TABLE_ACTOR.$MOVIE_ID",
        )
    }

    /**
     * Creating the tables in the database
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """create table $TABLE_MOVIE (
                            $ID integer primary key autoincrement, 
                            $MOVIE_NAME text unique not null,
                            $MOVIE_DIRECTOR text not null
                            );"""
        )
        db.execSQL(
            """create table $TABLE_ACTOR (
						$ID integer primary key autoincrement, 
						$ACTOR_NAME text unique not null,
						$MOVIE_ID numeric,
						FOREIGN KEY($MOVIE_ID) REFERENCES $TABLE_MOVIE($ID)
						);"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIE")
        onCreate(db)
    }

    fun insert(movie: String, director: String, actors: List<String>) {
        writableDatabase.use { database ->
            val addMovie = ContentValues()
            addMovie.put(MOVIE_NAME, movie)
            addMovie.put(MOVIE_DIRECTOR, director)
            val movieId = insertValues(database, TABLE_MOVIE, addMovie)

            actors.forEach {
                val addActor = ContentValues()
                addActor.put(ACTOR_NAME, it)
                addActor.put(MOVIE_ID, movieId)
                insertValues(database, TABLE_ACTOR, addActor)
            }
        }
    }

    fun clear() {
        writableDatabase.use { onUpgrade(it, 0, 0) }
    }

    private fun insertValues(database: SQLiteDatabase, table: String, values: ContentValues): Long {
        return database.insert(table, null, values)
    }

    fun performQuery(table: String, columns: Array<String>, selection: String? = null):
            ArrayList<String> {
        assert(columns.isNotEmpty())
        readableDatabase.use { database ->
            query(database, table, columns, selection).use { cursor ->
                return readFromCursor(cursor, columns.size)
            }
        }
    }

    fun performRawQueryWithoutJoin(
        select: Array<String>,
        from: Array<String>,
        where: String
    ): ArrayList<String> {
        val query = StringBuilder("SELECT ")
        for ((i, field) in select.withIndex()) {
            query.append(field)
            if (i != select.lastIndex) query.append(", ")
        }

        query.append(" FROM ")
        for ((i, table) in from.withIndex()) {
            query.append(table)
            if (i != from.lastIndex) query.append(", ")
        }

        query.append(" WHERE $where")

        readableDatabase.use { db ->
            db.rawQuery("$query;", null).use { cursor ->
                return readFromCursor(cursor, select.size)
            }
        }
    }

    fun performRawQuery(
        select: Array<String>, from: Array<String>, join: Array<String>, where: String? = null
    ): ArrayList<String> {

        val query = StringBuilder("SELECT ")
        for ((i, field) in select.withIndex()) {
            query.append(field)
            if (i != select.lastIndex) query.append(", ")
        }

        query.append(" FROM ")
        for ((i, table) in from.withIndex()) {
            query.append(table)
            if (i != from.lastIndex) query.append(", ")
        }

        query.append(" WHERE ")
        if (join.isNotEmpty()) {
            for ((i, link) in join.withIndex()) {
                query.append(link)
                if (i != join.lastIndex) query.append(" and ")
            }
        }

        if (where != null) {
            if (join.isNotEmpty()) query.append(" and $where")
            else query.append(" $where")
        }

        readableDatabase.use { db ->
            db.rawQuery("$query;", null).use { cursor ->
                return readFromCursor(cursor, select.size)
            }
        }
    }


    fun readFromCursor(cursor: Cursor, numberOfColumns: Int): ArrayList<String> {
        val result = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val item = StringBuilder("")
            for (i in 0 until numberOfColumns) {
                item.append("${cursor.getString(i)} ")
            }
            result.add("$item")
            cursor.moveToNext()
        }
        return result
    }

    private fun query(
        database: SQLiteDatabase, table: String, columns: Array<String>, selection: String?
    ): Cursor {
        return database.query(table, columns, selection, null, null, null, null, null)
    }


}