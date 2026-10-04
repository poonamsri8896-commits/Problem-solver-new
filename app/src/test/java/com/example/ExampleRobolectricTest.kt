package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Problem Solver", appName)
  }

  @Test
  fun `verify level calculation thresholds`() {
    val seed = com.example.data.getLevelInfo(10)
    assertEquals(1, seed.levelNumber)
    assertEquals("Seed", seed.name)

    val sprout = com.example.data.getLevelInfo(25)
    assertEquals(2, sprout.levelNumber)
    assertEquals("Sprout", sprout.name)

    val sequoia = com.example.data.getLevelInfo(350)
    assertEquals(5, sequoia.levelNumber)
    assertEquals("Sequoia", sequoia.name)
  }
}
