package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        stopKoin()

        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
                ApplicationProvider.getApplicationContext(),
                fakeDataSource
        )
        runBlocking{ fakeDataSource.deleteAllReminders()}
    }

    private fun getReminder(): ReminderDataItem {
        return ReminderDataItem(
                title = "title",
                description = "desc",
                location = "loc",
                latitude = 51.271712,
                longitude = 5.571989)
    }

    @Test
    fun show_loading() {

        val reminder = getReminder()
        // The loading animation appeared
        mainCoroutineRule.pauseDispatcher()

        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))

        // The loading animation disappeared
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false))
    }

    @Test
    fun saveReminder_ShowToast() = runBlockingTest {
        val reminder = getReminder()
        // When saving a reminder
        saveReminderViewModel.saveReminder(reminder)

        // Then we get displayed a Toast with Success message
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved"))
    }

    @Test
    fun saveReminder_withoutTitle() {
        // Create Reminder without Title
        val reminder = ReminderDataItem(
                title = "",
                description = "desc",
                location = "loc",
                latitude = 51.271712,
                longitude = 5.571989)

        // When saving reminder
        saveReminderViewModel.saveReminder(reminder)

        // Then show Snackbar with message
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())
        }

}