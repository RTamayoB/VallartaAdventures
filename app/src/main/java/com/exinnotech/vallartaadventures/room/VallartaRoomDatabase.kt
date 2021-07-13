package com.exinnotech.vallartaadventures.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.exinnotech.vallartaadventures.room.dao.ReservationDAO
import com.exinnotech.vallartaadventures.room.entities.Reservation

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(Reservation::class), version = 1, exportSchema = false)
public abstract class VallartaRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): ReservationDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: VallartaRoomDatabase? = null

        fun getDatabase(context: Context): VallartaRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VallartaRoomDatabase::class.java,
                    "vallarta_db"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}