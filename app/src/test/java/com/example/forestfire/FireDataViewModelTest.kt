package com.example.forestfire

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.forestfire.model.FireModel
import com.example.forestfire.repository.FireApiService
import com.example.forestfire.viewModel.fetchAPI.FireDataViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock


internal class FireDataViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()
    private val serviceMock = mock<FireApiService>()
    private val fireViewModel = FireDataViewModel(serviceMock)

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Mock
    private lateinit var mockContext : Context

    @Before
    fun setUp(){
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun locationTest() = runBlockingTest {
        val nelaugTest = FireModel.Location("Nelaug", "Agder", "36560", "-")
        val aarnesTest = FireModel.Location("Årnes", "Viken", "4920", "-")

        //whenever(serviceMock.fetchFireData()).thenReturn(listOf(FireModel.Dag))

    }

}
