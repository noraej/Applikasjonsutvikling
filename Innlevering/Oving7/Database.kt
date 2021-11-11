package com.noraej.oving7.service

import android.content.Context
import com.noraej.oving7.managers.DatabaseManagers

class Database(context: Context) : DatabaseManagers(context) {

    init {
        try {
            this.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val movieSelect = arrayOf(
        "'Movie: '  || $TABLE_MOVIE.$MOVIE_NAME || '\n'",
        "'Director: '  || $TABLE_MOVIE.$MOVIE_DIRECTOR || '\n'",
        "'Actors: '  || group_concat($TABLE_ACTOR.$ACTOR_NAME)"
    )

    val allMovies: ArrayList<String>
        get() = performQuery(TABLE_MOVIE, arrayOf(MOVIE_NAME))

    val allDirectors: ArrayList<String>
        get() = performQuery(TABLE_MOVIE, arrayOf(MOVIE_DIRECTOR))

    val allActors: ArrayList<String>
        get() = performQuery(TABLE_ACTOR, arrayOf(ID, ACTOR_NAME), null)


    val getAllActorsGroupByMovie: ArrayList<String>
        get() {
            val query = "SELECT ${movieSelect.joinToString(", ")} FROM $TABLE_MOVIE " +
                    "INNER JOIN $TABLE_ACTOR on $TABLE_MOVIE.$ID=$TABLE_ACTOR.$MOVIE_ID " +
                    "GROUP BY $TABLE_MOVIE.$MOVIE_NAME " +
                    "ORDER BY $TABLE_MOVIE.$MOVIE_NAME"

            readableDatabase.use { db ->
                db.rawQuery("$query;", null).use { cursor ->
                    return readFromCursor(cursor, movieSelect.size)
                }
            }
        }

    fun getMoviesByDirector(director: String): ArrayList<String> {
        val from = arrayOf(TABLE_MOVIE, TABLE_ACTOR)
        val join = JOIN_MOVIE_ACTOR
        val where = "$TABLE_MOVIE.$MOVIE_DIRECTOR='$director'"

        return performRawQuery(movieSelect, from, join, where)
    }
}