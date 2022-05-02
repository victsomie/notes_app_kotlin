package notes_app_kotlin.dao

import androidx.room.*
import notes_app_kotlin.entities.Notes

@Dao
interface NoteDao {

    @Query ("SELECT  * from notes ORDER BY id DESC")
    suspend fun getAllNotes(): List<Notes>

    @Query ("SELECT * FROM notes WHERE id=:id")
    suspend fun getSpecificNote(id:Int): Notes


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(note: Notes)

    @Delete
    suspend fun deleteNote (note: Notes) // Deletes all notes

    @Query("DELETE from notes WHERE id =:id")
    suspend fun deleteSpecificNote (id: Int)

    @Update
    suspend fun updateNote(note: Notes)

}