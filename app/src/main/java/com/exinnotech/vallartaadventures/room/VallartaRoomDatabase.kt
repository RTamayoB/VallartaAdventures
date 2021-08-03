package com.exinnotech.vallartaadventures.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.exinnotech.vallartaadventures.room.dao.FatherTourDAO
import com.exinnotech.vallartaadventures.room.dao.HotelDAO
import com.exinnotech.vallartaadventures.room.dao.ReservationDAO
import com.exinnotech.vallartaadventures.room.dao.TourDAO
import com.exinnotech.vallartaadventures.room.entity.FatherTour
import com.exinnotech.vallartaadventures.room.entity.Hotel
import com.exinnotech.vallartaadventures.room.entity.Reservation
import com.exinnotech.vallartaadventures.room.entity.Tour

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [Reservation::class, Hotel::class, Tour::class, FatherTour::class],
    version = 1,
    exportSchema = false
)
public abstract class VallartaRoomDatabase : RoomDatabase() {

    abstract fun reservationDao(): ReservationDAO
    abstract fun hotelDao(): HotelDAO
    abstract fun tourDao(): TourDAO
    abstract fun fatherTourDao(): FatherTourDAO

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
                    "vallarta_db",
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}