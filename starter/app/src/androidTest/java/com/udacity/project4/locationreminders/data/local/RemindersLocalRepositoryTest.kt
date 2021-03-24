package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//  Add testing implementation to the RemindersLocalRepository.kt
private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        )
                .allowMainThreadQueries()
                .build()

        localDataSource =
                RemindersLocalRepository(
                        database.reminderDao(),
                        Dispatchers.Main
                )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
                title = "title",
                description = "description",
                location = "location",
                latitude = 51.271712,
                longitude = 5.571989)
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        // GIVEN - a new task saved in the database
        val reminder = getReminder()
        localDataSource.saveReminder(reminder)

        // WHEN  - Task retrieved by ID
        val result = localDataSource.getReminder(reminder.id)

        // THEN - Same task is returned
        Assert.assertThat(result is Result.Success, `is`(true))
        result as Result.Success
        Assert.assertThat(result.data.title, `is`(reminder.title))
        Assert.assertThat(result.data.description, `is`(reminder.description))
        Assert.assertThat(result.data.location, `is`(reminder.location))
        Assert.assertThat(result.data.latitude, `is`(reminder.latitude))
        Assert.assertThat(result.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun deleteAllReminders_getRemindersById() = runBlocking {
        // Given a new task in the persistent repository
        val reminder = getReminder()
        localDataSource.saveReminder(reminder)
        localDataSource.deleteAllReminders()

        // When completed in the persistent repository
        val result = localDataSource.getReminder(reminder.id)

        // Then the task can be retrieved from the persistent repository and is complete
        Assert.assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        Assert.assertThat(result.message, `is`("Reminder not found!"))
    }

}